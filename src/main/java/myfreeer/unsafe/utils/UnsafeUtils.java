package myfreeer.unsafe.utils;

import myfreeer.unsafe.utils.accessor.Accessor;
import myfreeer.unsafe.utils.accessor.UnsafeAccessor;
import myfreeer.unsafe.utils.exception.UnsafeException;
import myfreeer.unsafe.utils.factory.AsmUnsafeFactory;
import myfreeer.unsafe.utils.factory.BaseUnsafeFactory;
import myfreeer.unsafe.utils.invoke.LookupFactory;

import java.lang.invoke.MethodHandles;

public class UnsafeUtils {
    private static volatile AsmUnsafeFactory factory = null;
    private static volatile boolean failed = false;
    private static volatile Accessor accessor = null;
    private static volatile boolean accessorFailed = false;
    private UnsafeUtils() {
    }

    public static BaseUnsafeFactory getUnsafeFactory() {
        if (failed) {
            return null;
        }
        if (factory == null) {
            synchronized (UnsafeUtils.class) {
                if (failed) {
                    return null;
                }
                if (factory == null) {
                    try {
                        factory = new AsmUnsafeFactory();
                    } catch (UnsafeException e) {
                        failed = true;
                    }
                }
            }
        }
        return factory;
    }

    private static boolean failed() {
        if (failed) {
            return true;
        }
        if (factory == null) {
            getUnsafeFactory();
        }
        return factory == null;
    }

    public static LookupFactory getLookupFactory() {
        if (failed()) {
            return null;
        }
        return factory.getUnsafe();
    }

    public static IUnsafe getUnsafe() {
        if (failed()) {
            return null;
        }
        return factory.getUnsafe();
    }

    public static MethodHandles.Lookup lookup() {
        if (failed()) {
            return null;
        }
        return factory.getUnsafe().lookup();
    }

    public static MethodHandles.Lookup lookup(final Class<?> clazz) {
        if (failed()) {
            return null;
        }
        return factory.getUnsafe().lookup(clazz);
    }

    public static Accessor getAccessor() {
        if (failed()) {
            return null;
        }
        if (accessor == null) {
            synchronized (Accessor.class) {
                if (failed || accessorFailed) {
                    return null;
                }
                if (accessor == null) {
                    try {
                        accessor = new UnsafeAccessor(factory.getUnsafe());
                    } catch (UnsafeException e) {
                        accessorFailed = true;
                    }
                }
            }
        }
        return accessor;
    }
}
