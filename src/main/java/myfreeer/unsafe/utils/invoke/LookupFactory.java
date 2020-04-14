package myfreeer.unsafe.utils.invoke;

import java.lang.invoke.MethodHandles;

public interface LookupFactory {
    /**
     * Get or create instance of {@code MethodHandles.Lookup}
     *
     * @return instance
     */
    MethodHandles.Lookup lookup();

    /**
     * Get or create instance of {@code MethodHandles.Lookup}
     * for specified class
     *
     * @param clazz target class
     * @return instance
     */
    MethodHandles.Lookup lookup(Class<?> clazz);
}
