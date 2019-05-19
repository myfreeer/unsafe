package myfreeer.unsafe.utils.accessor;

import java.lang.reflect.AccessibleObject;

public interface Accessor {
    /**
     * Try to make {@link AccessibleObject} accessible,
     * throws no exceptions.
     *
     * @param ao target
     * @return succeed or not
     */
    boolean access(AccessibleObject ao);
}
