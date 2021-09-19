package myfreeer.unsafe.utils.factory;

import myfreeer.unsafe.utils.exception.UnsafeException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

public class AsmSunUnsafeFactory extends AbstractAsmUnsafeFactory {
    private final Set<MethodDef> methodDefs;
    private final AbstractUnsafe unsafe;
    private final UnsafeConstantImpl constant;
    private final Object theUnsafe;

    public AsmSunUnsafeFactory(ByteCodeClassLoader loader) throws UnsafeException {

        Class<?> unsafeClass;
        Object theUnsafe;
        try {
            unsafeClass = Class.forName("sun.misc.Unsafe");
            final Field theUnsafeField = unsafeClass.getDeclaredField("theUnsafe");
            theUnsafeField.setAccessible(true);
            theUnsafe = theUnsafeField.get(null);
        } catch (ClassNotFoundException e) {
            throw new UnsafeException("Unsafe class not found: sun.misc.Unsafe", e);
        } catch (NoSuchFieldException e) {
            throw new UnsafeException(e);
        } catch (IllegalAccessException e) {
            throw new UnsafeException("Can not get unsafe instance", e);
        } catch (RuntimeException e) {
            if ("java.lang.reflect.InaccessibleObjectException"
                    .equals(e.getClass().getName())) {
                throw new UnsafeException("Can not access object", e);
            }
            throw e;
        }
        final String proxyClassName = nextProxyClassName();
        final byte[] bytes = assembleByteCode(unsafeClass, proxyClassName);
        final Class<?> proxyClass = loader.defineClass(proxyClassName, bytes);
        methodDefs = methodDef(proxyClass);

        try {
            //noinspection JavaReflectionInvocation
            unsafe = (AbstractUnsafe) proxyClass.getConstructor(unsafeClass)
                    .newInstance(theUnsafe);
        } catch (InstantiationException | IllegalAccessException |
                InvocationTargetException | NoSuchMethodException e) {
            throw new UnsafeException("unsafe init", e);
        } catch (RuntimeException e) {
            if ("java.lang.reflect.InaccessibleObjectException"
                    .equals(e.getClass().getName())) {
                throw new UnsafeException("Can not access object", e);
            }
            throw e;
        }
        constant = new UnsafeConstantImpl(unsafe);
        this.theUnsafe = theUnsafe;
    }

    public AsmSunUnsafeFactory() throws UnsafeException {
        this(new ByteCodeClassLoader());
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
}
