package myfreeer.unsafe.utils.workaround;

import myfreeer.unsafe.utils.IUnsafe;
import myfreeer.unsafe.utils.UnsafeUtils;
import myfreeer.unsafe.utils.log.Logger;
import myfreeer.unsafe.utils.log.Logging;

import java.lang.invoke.MethodHandles;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.Map;

public class Jdk12GetAllFieldsAndMethods {
    private static final Logger log =
            Logging.getLogger(Jdk12GetAllFieldsAndMethods.class);
    private static volatile MethodHandles.Lookup lookup = null;
    private static volatile boolean failed = false;

    private Jdk12GetAllFieldsAndMethods() {
    }

    public static boolean unlock() {
        if (failed) {
            return false;
        }
        try {
            final Class<?> clazz = Class.forName("jdk.internal.reflect.Reflection");
            final IUnsafe unsafe = UnsafeUtils.getUnsafe();
            if (unsafe != null) {
                unsafe.ensureClassInitialized(clazz);
            }
            final MethodHandles.Lookup lookup = UnsafeUtils.lookup(clazz);
            if (lookup == null) {
                failed = true;
                log.warn("unlock: fail to get lookup instance");
                return false;
            }
            lookup.findStaticSetter(clazz, "fieldFilterMap", Map.class)
                    .invokeExact(Collections.EMPTY_MAP);
            lookup.findStaticSetter(clazz, "methodFilterMap", Map.class)
                    .invokeExact(Collections.EMPTY_MAP);

            if (Jdk12GetAllFieldsAndMethods.lookup == null) {
                synchronized (Jdk12GetAllFieldsAndMethods.class) {
                    if (Jdk12GetAllFieldsAndMethods.lookup == null) {
                        Jdk12GetAllFieldsAndMethods.lookup =
                                UnsafeUtils.lookup(Class.class);
                    }
                }
            }
            return true;
        } catch (Throwable e) {
            failed = true;
            log.warn("unlock", e);
            return false;
        }
    }

    public static boolean refreshClass(final Class<?> clazz) {
        if (failed || clazz == null) {
            return false;
        }
        if (lookup == null && !unlock()) {
            return false;
        }
        try {
            final SoftReference<?> reference = ((SoftReference<?>) lookup.findGetter(
                    Class.class, "reflectionData", SoftReference.class)
                    .invokeExact(clazz));
            if (reference != null) {
                reference.clear();
            }
            return true;
        } catch (Throwable e) {
            log.warn("refreshClass", e);
            return false;
        }
    }
}
