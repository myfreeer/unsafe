package myfreeer.unsafe.utils.factory;

import myfreeer.unsafe.utils.IUnsafe;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@Deprecated
final class MethodHandleInvocationHandler implements InvocationHandler {
    private final MethodHandleUnsafeFactory factory;

    MethodHandleInvocationHandler(final MethodHandleUnsafeFactory factory) {
        this.factory = factory;
    }

    @Override
    public Object invoke(final Object proxy,
                         final Method method,
                         final Object[] args) throws Throwable {
        final MethodHandle methodHandle;
        if (proxy instanceof IUnsafe) {
            methodHandle = factory.cache.get(method);
        } else {
            methodHandle = factory.map.get(MethodDef.of(method));
        }
        if (methodHandle == null) {
            throw new NullPointerException("Method " + method + " not available here");
        }
        return methodHandle.invokeExact(args);
    }
}
