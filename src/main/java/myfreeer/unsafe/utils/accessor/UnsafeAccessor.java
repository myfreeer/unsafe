package myfreeer.unsafe.utils.accessor;

import myfreeer.unsafe.utils.exception.UnsafeException;
import myfreeer.unsafe.utils.invoke.LookupFactory;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.AccessibleObject;

public class UnsafeAccessor implements Accessor {
    private final MethodHandle setAccessible;

    public UnsafeAccessor(final LookupFactory factory) {
        try {
            final MethodHandles.Lookup lookup = factory.lookup(AccessibleObject.class);
            setAccessible = lookup.findSetter(AccessibleObject.class,
                        "override", boolean.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new UnsafeException(e);
        }
    }

    @Override
    public boolean access(final AccessibleObject ao) {
        try {
            setAccessible.invokeExact(ao, true);
            return true;
        } catch (Throwable ignored) {
            try {
                ao.setAccessible(true);
                return true;
            } catch (Exception ignored1) {
            }
        }
        return false;
    }
}
