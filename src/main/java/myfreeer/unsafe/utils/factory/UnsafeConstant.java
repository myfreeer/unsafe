package myfreeer.unsafe.utils.factory;

import myfreeer.unsafe.utils.IUnsafe;

public interface UnsafeConstant {

    /**
     * This constant differs from all results that will ever be returned from
     * {@link IUnsafe#staticFieldOffset}, {@link IUnsafe#objectFieldOffset},
     * or {@link IUnsafe#arrayBaseOffset}.
     */
    int INVALID_FIELD_OFFSET();

    /**
     * The value of {@code arrayBaseOffset(boolean[].class)}
     */
    int ARRAY_BOOLEAN_BASE_OFFSET();

    /**
     * The value of {@code arrayBaseOffset(byte[].class)}
     */
    int ARRAY_BYTE_BASE_OFFSET();

    /**
     * The value of {@code arrayBaseOffset(short[].class)}
     */
    int ARRAY_SHORT_BASE_OFFSET();

    /**
     * The value of {@code arrayBaseOffset(char[].class)}
     */
    int ARRAY_CHAR_BASE_OFFSET();

    /**
     * The value of {@code arrayBaseOffset(int[].class)}
     */
    int ARRAY_INT_BASE_OFFSET();

    /**
     * The value of {@code arrayBaseOffset(long[].class)}
     */
    int ARRAY_LONG_BASE_OFFSET();

    /**
     * The value of {@code arrayBaseOffset(float[].class)}
     */
    int ARRAY_FLOAT_BASE_OFFSET();

    /**
     * The value of {@code arrayBaseOffset(double[].class)}
     */
    int ARRAY_DOUBLE_BASE_OFFSET();

    /**
     * The value of {@code arrayBaseOffset(Object[].class)}
     */
    int ARRAY_OBJECT_BASE_OFFSET();

    /**
     * The value of {@code arrayIndexScale(boolean[].class)}
     */
    int ARRAY_BOOLEAN_INDEX_SCALE();

    /**
     * The value of {@code arrayIndexScale(byte[].class)}
     */
    int ARRAY_BYTE_INDEX_SCALE();

    /**
     * The value of {@code arrayIndexScale(short[].class)}
     */
    int ARRAY_SHORT_INDEX_SCALE();

    /**
     * The value of {@code arrayIndexScale(char[].class)}
     */
    int ARRAY_CHAR_INDEX_SCALE();

    /**
     * The value of {@code arrayIndexScale(int[].class)}
     */
    int ARRAY_INT_INDEX_SCALE();

    /**
     * The value of {@code arrayIndexScale(long[].class)}
     */
    int ARRAY_LONG_INDEX_SCALE();

    /**
     * The value of {@code arrayIndexScale(float[].class)}
     */
    int ARRAY_FLOAT_INDEX_SCALE();

    /**
     * The value of {@code arrayIndexScale(double[].class)}
     */
    int ARRAY_DOUBLE_INDEX_SCALE();

    /**
     * The value of {@code arrayIndexScale(Object[].class)}
     */
    int ARRAY_OBJECT_INDEX_SCALE();

    /**
     * The value of {@link IUnsafe#addressSize()}
     */
    int ADDRESS_SIZE();
}
