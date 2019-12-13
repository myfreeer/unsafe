package myfreeer.unsafe.utils.accessor;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The fallback impl of {@link Accessor},
 * using only java public api.
 */
public class SafeAccessor implements Accessor {
    private static final Logger log =
            Logger.getLogger(SafeAccessor.class.getSimpleName());

    @Override
    @SuppressWarnings("deprecation")
    public boolean access(AccessibleObject ao) {
        if (ao == null || ao.isAccessible()) {
            return false;
        }
        if (ao instanceof Member) {
            final Member member = (Member) ao;
            if (Modifier.isPublic(member.getModifiers()) &&
                    Modifier.isPublic(member.getDeclaringClass().getModifiers())) {
                // no need to setAccessible
                return true;
            }
        }
        try {
            ao.setAccessible(true);
        } catch (RuntimeException ex) {
            log.log(Level.WARNING, "setAccessible" + ao, ex);
            return false;
        }
        return true;
    }
}
