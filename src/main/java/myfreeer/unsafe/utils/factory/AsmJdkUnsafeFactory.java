package myfreeer.unsafe.utils.factory;

import myfreeer.unsafe.utils.IUnsafe;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AsmJdkUnsafeFactory extends AsmUnsafeFactory {
    private static final Logger log =
            Logger.getLogger(AsmJdkUnsafeFactory.class.getSimpleName());
    private final Set<MethodDef> methodDefs;
    private final AbstractUnsafe unsafe;
    private final UnsafeConstantImpl constant;
    private final Object theUnsafe;

    public AsmJdkUnsafeFactory() {
        final AbstractUnsafe unsafe = super.getUnsafe();
        Class<?> jdkUnsafeClass;
        try {
            jdkUnsafeClass = Class.forName("jdk.internal.misc.Unsafe");
        } catch (ClassNotFoundException e) {
            log.log(Level.INFO, "jdkUnsafeClass not found," +
                    " falling back to sun unsafe", e);
            this.methodDefs = null;
            this.unsafe = null;
            this.constant = null;
            this.theUnsafe = null;
            return;
        }
        final String proxyNameInJavaBaseModule =
                "java.lang.invoke.UnsafeProxy_" + nextCount();

        final byte[] byteCodeProxyInJavaBaseModule =
                assembleByteCode(jdkUnsafeClass, proxyNameInJavaBaseModule);
        final Class<?> jdkUnsafeProxy1 = unsafe.defineClass(proxyNameInJavaBaseModule,
                byteCodeProxyInJavaBaseModule,
                0, byteCodeProxyInJavaBaseModule.length,
                null, null);
        Class<?> proxyClass;
        AbstractUnsafe jdkUnsafe;
        Object theUnsafe;
        try {
            final Field jdkUnsafeField = jdkUnsafeClass.getDeclaredField("theUnsafe");
            final Object jdkUnsafeFieldBase = unsafe.staticFieldBase(jdkUnsafeField);
            final long jdkUnsafeFieldFieldOffset = unsafe.staticFieldOffset(jdkUnsafeField);
            theUnsafe = unsafe.getObject(jdkUnsafeFieldBase, jdkUnsafeFieldFieldOffset);
            //noinspection JavaReflectionInvocation
            final Object jdkUnsafeProxyIns = jdkUnsafeProxy1.getConstructor(jdkUnsafeClass)
                    .newInstance(theUnsafe);

            final String proxyClassName = AsmJdkUnsafeFactory.class.getName() +
                    "_Proxy_" + nextCount();
            final byte[] proxyByteCode = assembleByteCode(jdkUnsafeProxy1, proxyClassName,
                    AbstractUnsafe.class, new Class<?>[]{IUnsafe.class});
            proxyClass = unsafe.defineClass(proxyClassName,
                    proxyByteCode, 0, proxyByteCode.length,
                    AbstractUnsafe.class.getClassLoader(), null);
            jdkUnsafe = (AbstractUnsafe) proxyClass.getConstructor(jdkUnsafeProxy1)
                    .newInstance(jdkUnsafeProxyIns);
        } catch (InstantiationException | InvocationTargetException |
                IllegalAccessException | NoSuchFieldException |
                NoSuchMethodException e) {
            log.log(Level.WARNING, "Fail to init proxy for jdk unsafe, falling back", e);
            this.methodDefs = null;
            this.unsafe = null;
            this.constant = null;
            this.theUnsafe = null;
            return;
        }
        this.methodDefs = methodDef(proxyClass);
        this.unsafe = jdkUnsafe;
        this.constant = new UnsafeConstantImpl(jdkUnsafe);
        this.theUnsafe = theUnsafe;
    }

    @Override
    public Object getTheUnsafe() {
        return theUnsafe == null ? super.getTheUnsafe() : theUnsafe;
    }

    @Override
    public AbstractUnsafe getUnsafe() {
        return unsafe == null ? super.getUnsafe() : unsafe;
    }

    @Override
    public boolean hasMethod(Method method) {
        if (methodDefs == null) {
            return super.hasMethod(method);
        }
        return methodDefs.contains(MethodDef.of(method));
    }

    @Override
    public boolean hasMethod(String methodName, Class<?>... parameterTypes) {
        if (methodDefs == null) {
            return super.hasMethod(methodName, parameterTypes);
        }
        return methodDefs.contains(MethodDef.of(methodName, parameterTypes));
    }

    @Override
    public UnsafeConstant getConstant() {
        return constant == null ? super.getConstant() : constant;
    }
}
