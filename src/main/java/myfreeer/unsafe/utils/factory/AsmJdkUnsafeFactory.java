package myfreeer.unsafe.utils.factory;

import myfreeer.unsafe.utils.IUnsafe;
import myfreeer.unsafe.utils.exception.UnsafeException;
import myfreeer.unsafe.utils.log.Logger;
import myfreeer.unsafe.utils.log.Logging;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

public class AsmJdkUnsafeFactory extends AbstractAsmUnsafeFactory {
    private static final Logger log =
            Logging.getLogger(AsmJdkUnsafeFactory.class);
    private final Set<MethodDef> methodDefs;
    private final AbstractUnsafe unsafe;
    private final UnsafeConstantImpl constant;
    private final Object theUnsafe;

    public AsmJdkUnsafeFactory(
            Object theUnsafe, Class<?> unsafeClass, IDefineClass defineClass) {
        final String proxyNameInJavaBaseModule =
                "java.lang.invoke.UnsafeProxy_" + nextCount();
        final byte[] byteCodeProxyInJavaBaseModule =
                assembleByteCode(unsafeClass, proxyNameInJavaBaseModule);
        final Class<?> jdkUnsafeProxy = defineClass.defineClass(proxyNameInJavaBaseModule,
                byteCodeProxyInJavaBaseModule);
        Class<?> proxyClass;
        AbstractUnsafe jdkUnsafe;
        try {
            final Object jdkUnsafeProxyIns = jdkUnsafeProxy.getConstructor(unsafeClass)
                    .newInstance(theUnsafe);
            final String proxyClassName = AsmJdkUnsafeFactory.class.getName() +
                    "_Proxy_" + nextCount();
            final byte[] proxyByteCode = assembleByteCode(jdkUnsafeProxy, proxyClassName,
                    AbstractUnsafe.class, new Class<?>[]{IUnsafe.class});
            proxyClass = defineClass.defineClass(proxyClassName, proxyByteCode);
            jdkUnsafe = (AbstractUnsafe) proxyClass.getConstructor(jdkUnsafeProxy)
                    .newInstance(jdkUnsafeProxyIns);
        } catch (ReflectiveOperationException e) {
            throw new UnsafeException("Fail to init proxy for jdk unsafe, falling back", e);
        } catch (RuntimeException e) {
            if ("java.lang.reflect.InaccessibleObjectException"
                    .equals(e.getClass().getName())) {
                throw new UnsafeException("Can not access object", e);
            }
            throw e;
        }
        this.methodDefs = methodDef(proxyClass);
        this.unsafe = jdkUnsafe;
        this.constant = new UnsafeConstantImpl(jdkUnsafe);
        this.theUnsafe = theUnsafe;
    }

    public static AsmJdkUnsafeFactory createInstance(IUnsafe unsafe) {
        Class<?> jdkUnsafeClass;
        try {
            jdkUnsafeClass = Class.forName("jdk.internal.misc.Unsafe");
        } catch (ClassNotFoundException e) {
            throw new UnsafeException("class jdk.internal.misc.Unsafe not found", e);
        }
        Field jdkUnsafeField;
        try {
            jdkUnsafeField = jdkUnsafeClass.getDeclaredField("theUnsafe");
        } catch (ReflectiveOperationException e) {
            throw new UnsafeException("jdkUnsafeClass.getDeclaredField(\"theUnsafe\")", e);
        }

        final Object jdkUnsafeFieldBase = unsafe.staticFieldBase(jdkUnsafeField);
        final long jdkUnsafeFieldFieldOffset = unsafe.staticFieldOffset(jdkUnsafeField);
        Object theUnsafe = unsafe.getObject(jdkUnsafeFieldBase, jdkUnsafeFieldFieldOffset);

        return new AsmJdkUnsafeFactory(
                theUnsafe, jdkUnsafeClass, new UnsafeDefineClass(unsafe));
    }

    @Override
    public Object getTheUnsafe() {
        return theUnsafe;
    }

    @Override
    public AbstractUnsafe getUnsafe() {
        return unsafe;
    }

    @Override
    public boolean hasMethod(Method method) {
        return methodDefs.contains(MethodDef.of(method));
    }

    @Override
    public boolean hasMethod(String methodName, Class<?>... parameterTypes) {
        return methodDefs.contains(MethodDef.of(methodName, parameterTypes));
    }

    @Override
    public UnsafeConstant getConstant() {
        return constant;
    }

    public interface IDefineClass {
        Class<?> defineClass(String className, byte[] byteCode);
    }

    public static class UnsafeDefineClass implements IDefineClass {
        final IUnsafe unsafe;

        public UnsafeDefineClass(final IUnsafe unsafe) {
            this.unsafe = unsafe;
        }

        @Override
        public Class<?> defineClass(final String className, final byte[] byteCode) {
            return unsafe.defineClass(className, byteCode, 0, byteCode.length,
                    null, null);
        }
    }
}
