package myfreeer.unsafe.utils.factory;

import myfreeer.unsafe.utils.IUnsafe;
import myfreeer.unsafe.utils.exception.UnsafeException;
import myfreeer.unsafe.utils.invoke.LookupFactory;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class MethodHandleUnsafeFactory extends BaseUnsafeFactory
        implements UnsafeFactory, LookupFactory {
    private static final int TRUSTED = -1;
    final Map<MethodDef, MethodHandle> map;
    final Map<Method, MethodHandle> cache;
    private final MethodHandles.Lookup lookup;
    private final MethodHandle lookupConstructor;
    private volatile IUnsafe instance;

    public MethodHandleUnsafeFactory() throws UnsafeException {
        super();
        try {
            final Field implLookup =
                    MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            final Method staticFieldBase =
                    unsafeClass.getMethod("staticFieldBase", Field.class);
            final Method staticFieldOffset =
                    unsafeClass.getMethod("staticFieldOffset", Field.class);
            final Method getObjectVolatile =
                    unsafeClass.getMethod("getObjectVolatile", Object.class, long.class);
            // base =  unsafe.staticFieldBase(implLookup)
            final Object base = staticFieldBase.invoke(theUnsafe, implLookup);
            // offset =  unsafe.staticFieldOffset(implLookup)
            final long offset = (long) staticFieldOffset.invoke(theUnsafe, implLookup);
            // Ensure the class is fully loaded.
            MethodHandles.lookup();
            // lookup =  unsafe.getObjectVolatile(base, offset)
            lookup = (MethodHandles.Lookup) getObjectVolatile.invoke(theUnsafe, base, offset);
            final Method[] methods = unsafeClass.getMethods();
            final Map<MethodDef, MethodHandle> map = new ConcurrentHashMap<>(methods.length);
            final Map<Method, MethodHandle> cache = new ConcurrentHashMap<>(methods.length);
            final Map<Integer, MethodHandle> spreadInvokerMap = new HashMap<>(16);
            for (final Method method : methods) {
                final Class<?>[] parameterTypes = method.getParameterTypes();
                MethodHandle invoker = spreadInvokerMap.get(parameterTypes.length);
                if (invoker == null) {
                    invoker = MethodHandles.spreadInvoker(
                            MethodType.genericMethodType(
                                    parameterTypes.length), 0);
                    spreadInvokerMap.put(parameterTypes.length, invoker);
                }
                final MethodHandle handle = invoker.bindTo(
                        lookup.unreflect(method).bindTo(theUnsafe));
                map.put(MethodDef.of(method.getName(), parameterTypes), handle);
            }
            for (final Method method : IUnsafe.class.getMethods()) {
                final MethodHandle methodHandle = map.get(MethodDef.of(method));
                if (methodHandle != null) {
                    cache.put(method, methodHandle);
                }
            }
            this.map = Collections.unmodifiableMap(map);
            this.cache = Collections.unmodifiableMap(cache);
            lookupConstructor = lookup.findConstructor(MethodHandles.Lookup.class,
                    MethodType.methodType(void.class, Class.class, int.class));
        } catch (Throwable e) {
            throw new UnsafeException(e);
        }
    }

    private IUnsafe makeUnsafe() {
        return (IUnsafe) Proxy.newProxyInstance(IUnsafe.class.getClassLoader(),
                new Class[]{IUnsafe.class},
                new MethodHandleInvocationHandler(this));
    }

    @Override
    public IUnsafe getUnsafe() throws UnsafeException {
        if (instance == null) {
            synchronized (this) {
                if (instance == null) {
                    instance = makeUnsafe();
                }
            }
        }
        return instance;
    }

    @Override
    public boolean hasMethod(final Method method) {
        if (method.getDeclaringClass().equals(IUnsafe.class)) {
            return cache.containsKey(method);
        }
        return map.containsKey(MethodDef.of(method));
    }

    @Override
    public boolean hasMethod(final String methodName, final Class<?>... parameterTypes) {
        return map.containsKey(MethodDef.of(methodName, parameterTypes));
    }

    /**
     * Get trusted instance of {@link MethodHandles.Lookup}
     *
     * @return lookup
     */
    @Override
    public MethodHandles.Lookup lookup() {
        return lookup;
    }

    /**
     * Create trusted instance of {@link MethodHandles.Lookup} for any class
     *
     * @param clazz target class
     * @return lookup
     */
    @Override
    public MethodHandles.Lookup lookup(final Class<?> clazz) {
        Objects.requireNonNull(clazz);
        try {
            return (MethodHandles.Lookup) lookupConstructor.invokeExact(clazz, TRUSTED);
        } catch (Throwable ignored) {
            return lookup.in(clazz);
        }
    }
}
