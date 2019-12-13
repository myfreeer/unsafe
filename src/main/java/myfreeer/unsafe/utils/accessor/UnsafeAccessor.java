package myfreeer.unsafe.utils.accessor;

import myfreeer.unsafe.utils.exception.UnsafeException;
import myfreeer.unsafe.utils.invoke.LookupFactory;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.AccessibleObject;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UnsafeAccessor implements Accessor {
    private static final Logger log =
            Logger.getLogger(UnsafeAccessor.class.getSimpleName());

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
        } catch (Throwable throwable) {
            log.log(Level.INFO,
                    "invokeExact on setAccessible fail, falling back",
                    throwable);
            try {
                ao.setAccessible(true);
                return true;
            } catch (Exception e) {
                log.log(Level.WARNING, "setAccessible fail", throwable);
            }
        }
        return false;
    }
}
