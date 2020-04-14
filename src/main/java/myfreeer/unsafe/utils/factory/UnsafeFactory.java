package myfreeer.unsafe.utils.factory;

import myfreeer.unsafe.utils.IUnsafe;
import myfreeer.unsafe.utils.exception.UnsafeException;

import java.lang.reflect.Method;

public interface UnsafeFactory {

    /**
     * Get the RAW instance of unsafe
     *
     * @return unsafe
     */
    Object getTheUnsafe();

    /**
     * Provides the caller with the capability of performing unsafe
     * operations.
     *
     * <p>The returned {@code Unsafe} object should be carefully guarded
     * by the caller, since it can be used to read and write data at arbitrary
     * memory addresses.  It must never be passed to untrusted code.
     *
     * <p>Most methods in this class are very low-level, and correspond to a
     * small number of hardware instructions (on typical machines).  Compilers
     * are encouraged to optimize these methods accordingly.
     *
     * <p>Here is a suggested idiom for using unsafe operations:
     *
     * <pre> {@code
     * class MyTrustedClass {
     *   private static final Unsafe unsafe = Unsafe.getUnsafe();
     *   ...
     *   private long myCountAddress = ...;
     *   public int getCount() { return unsafe.getByte(myCountAddress); }
     * }}</pre>
     * <p>
     * (It may assist compilers to make the local variable {@code final}.)
     *
     * @return proxy wrapper instance of unsafe
     */
    IUnsafe getUnsafe() throws UnsafeException;

    /**
     * Check if specified method is available
     *
     * @param method specified method
     * @return result
     */
    boolean hasMethod(Method method);

    /**
     * Check if specified method is available
     *
     * @param methodName     name of method
     * @param parameterTypes array of parameter type of method
     * @return result
     */
    boolean hasMethod(String methodName, Class<?>... parameterTypes);


    /**
     * Get the holder of runtime constants of unsafe
     *
     * @return instances of UnsafeConstant
     */
    UnsafeConstant getConstant();

}
