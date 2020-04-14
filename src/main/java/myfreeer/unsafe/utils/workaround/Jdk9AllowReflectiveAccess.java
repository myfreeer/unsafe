package myfreeer.unsafe.utils.workaround;

import myfreeer.unsafe.utils.UnsafeUtils;
import myfreeer.unsafe.utils.log.Logger;
import myfreeer.unsafe.utils.log.Logging;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Set;

public class Jdk9AllowReflectiveAccess {
    private static final Logger log = Logging.getLogger(Jdk9AllowReflectiveAccess.class);

    public static boolean addOpens() {
        // lookup with super power
        final MethodHandles.Lookup lookup = UnsafeUtils.lookup();
        if (lookup == null) {
            return false;
        }
        try {
            addOpens0(lookup);
            return true;
        } catch (Throwable e) {
            log.warn("addOpens fail", e);
            return false;
        }
    }

    private static void addOpens0(final MethodHandles.Lookup lookup) throws Throwable {
        final Class<?> moduleLayer = Class.forName("java.lang.ModuleLayer");
        final Object boot = lookup.findStatic(moduleLayer, "boot",
                MethodType.methodType(moduleLayer)).invoke();
        final Set<?> modules = (Set<?>) lookup.findVirtual(moduleLayer, "modules",
                MethodType.methodType(Set.class)).invoke(boot);
        final Class<?> module = Class.forName("java.lang.Module");
        final MethodHandle getPackages = lookup.findVirtual(module, "getPackages",
                MethodType.methodType(Set.class));
        final MethodHandle implAddOpens = lookup.findVirtual(module, "implAddOpens",
                MethodType.methodType(void.class, String.class));
        for (Object o : modules) {
            final Set<?> packages = (Set<?>) getPackages.invoke(o);
            for (Object pkg : packages) {
                implAddOpens.invoke(o, pkg);
            }
        }
    }

    public static boolean disableIllegalAccessLogger() {
        // lookup with super power
        final MethodHandles.Lookup lookup = UnsafeUtils.lookup();
        if (lookup == null) {
            return false;
        }
        try {
            final Class<?> illegalAccessLogger =
                    Class.forName("jdk.internal.module.IllegalAccessLogger");
            lookup.findStaticSetter(illegalAccessLogger, "logger", illegalAccessLogger)
                    .invoke((Object) null);
            return true;
        } catch (Throwable e) {
            log.warn("disableIllegalAccessLogger fail", e);
            return false;
        }
    }

}
