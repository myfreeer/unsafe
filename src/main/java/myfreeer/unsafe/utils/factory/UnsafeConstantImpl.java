package myfreeer.unsafe.utils.factory;

import myfreeer.unsafe.utils.IUnsafe;

public class UnsafeConstantImpl implements UnsafeConstant {

    /**
     * This constant differs from all results that will ever be returned from
     * {@link IUnsafe#staticFieldOffset}, {@link IUnsafe#objectFieldOffset},
     * or {@link IUnsafe#arrayBaseOffset}.
     */
    public final int INVALID_FIELD_OFFSET;
    /**
     * The value of {@code arrayBaseOffset(boolean[].class)}
     */
    public final int ARRAY_BOOLEAN_BASE_OFFSET;
    /**
     * The value of {@code arrayBaseOffset(byte[].class)}
     */
    public final int ARRAY_BYTE_BASE_OFFSET;
    /**
     * The value of {@code arrayBaseOffset(short[].class)}
     */
    public final int ARRAY_SHORT_BASE_OFFSET;
    /**
     * The value of {@code arrayBaseOffset(char[].class)}
     */
    public final int ARRAY_CHAR_BASE_OFFSET;
    /**
     * The value of {@code arrayBaseOffset(int[].class)}
     */
    public final int ARRAY_INT_BASE_OFFSET;
    /**
     * The value of {@code arrayBaseOffset(long[].class)}
     */
    public final int ARRAY_LONG_BASE_OFFSET;
    /**
     * The value of {@code arrayBaseOffset(float[].class)}
     */
    public final int ARRAY_FLOAT_BASE_OFFSET;
    /**
     * The value of {@code arrayBaseOffset(double[].class)}
     */
    public final int ARRAY_DOUBLE_BASE_OFFSET;
    /**
     * The value of {@code arrayBaseOffset(Object[].class)}
     */
    public final int ARRAY_OBJECT_BASE_OFFSET;
    /**
     * The value of {@code arrayIndexScale(boolean[].class)}
     */
    public final int ARRAY_BOOLEAN_INDEX_SCALE;
    /**
     * The value of {@code arrayIndexScale(byte[].class)}
     */
    public final int ARRAY_BYTE_INDEX_SCALE;
    /**
     * The value of {@code arrayIndexScale(short[].class)}
     */
    public final int ARRAY_SHORT_INDEX_SCALE;
    /**
     * The value of {@code arrayIndexScale(char[].class)}
     */
    public final int ARRAY_CHAR_INDEX_SCALE;
    /**
     * The value of {@code arrayIndexScale(int[].class)}
     */
    public final int ARRAY_INT_INDEX_SCALE;
    /**
     * The value of {@code arrayIndexScale(long[].class)}
     */
    public final int ARRAY_LONG_INDEX_SCALE;
    /**
     * The value of {@code arrayIndexScale(float[].class)}
     */
    public final int ARRAY_FLOAT_INDEX_SCALE;
    /**
     * The value of {@code arrayIndexScale(double[].class)}
     */
    public final int ARRAY_DOUBLE_INDEX_SCALE;
    /**
     * The value of {@code arrayIndexScale(Object[].class)}
     */
    public final int ARRAY_OBJECT_INDEX_SCALE;
    /**
     * The value of {@code addressSize()}
     */
    public final int ADDRESS_SIZE;

    public UnsafeConstantImpl(IUnsafe unsafe) {
        INVALID_FIELD_OFFSET = -1;
        ARRAY_BOOLEAN_BASE_OFFSET
                = unsafe.arrayBaseOffset(boolean[].class);
        ARRAY_BYTE_BASE_OFFSET
                = unsafe.arrayBaseOffset(byte[].class);
        ARRAY_SHORT_BASE_OFFSET
                = unsafe.arrayBaseOffset(short[].class);
        ARRAY_CHAR_BASE_OFFSET
                = unsafe.arrayBaseOffset(char[].class);
        ARRAY_INT_BASE_OFFSET
                = unsafe.arrayBaseOffset(int[].class);
        ARRAY_LONG_BASE_OFFSET
                = unsafe.arrayBaseOffset(long[].class);
        ARRAY_FLOAT_BASE_OFFSET
                = unsafe.arrayBaseOffset(float[].class);
        ARRAY_DOUBLE_BASE_OFFSET
                = unsafe.arrayBaseOffset(double[].class);
        ARRAY_OBJECT_BASE_OFFSET
                = unsafe.arrayBaseOffset(Object[].class);
        ARRAY_BOOLEAN_INDEX_SCALE
                = unsafe.arrayIndexScale(boolean[].class);
        ARRAY_BYTE_INDEX_SCALE
                = unsafe.arrayIndexScale(byte[].class);
        ARRAY_SHORT_INDEX_SCALE
                = unsafe.arrayIndexScale(short[].class);
        ARRAY_CHAR_INDEX_SCALE
                = unsafe.arrayIndexScale(char[].class);
        ARRAY_INT_INDEX_SCALE
                = unsafe.arrayIndexScale(int[].class);
        ARRAY_LONG_INDEX_SCALE
                = unsafe.arrayIndexScale(long[].class);
        ARRAY_FLOAT_INDEX_SCALE
                = unsafe.arrayIndexScale(float[].class);
        ARRAY_DOUBLE_INDEX_SCALE
                = unsafe.arrayIndexScale(double[].class);
        ARRAY_OBJECT_INDEX_SCALE
                = unsafe.arrayIndexScale(Object[].class);
        ADDRESS_SIZE = unsafe.addressSize();
    }

    @Override
    public int INVALID_FIELD_OFFSET() {
        return INVALID_FIELD_OFFSET;
    }

    @Override
    public int ARRAY_BOOLEAN_BASE_OFFSET() {
        return ARRAY_BOOLEAN_BASE_OFFSET;
    }

    @Override
    public int ARRAY_BYTE_BASE_OFFSET() {
        return ARRAY_BYTE_BASE_OFFSET;
    }

    @Override
    public int ARRAY_SHORT_BASE_OFFSET() {
        return ARRAY_SHORT_BASE_OFFSET;
    }

    @Override
    public int ARRAY_CHAR_BASE_OFFSET() {
        return ARRAY_CHAR_BASE_OFFSET;
    }

    @Override
    public int ARRAY_INT_BASE_OFFSET() {
        return ARRAY_INT_BASE_OFFSET;
    }

    @Override
    public int ARRAY_LONG_BASE_OFFSET() {
        return ARRAY_LONG_BASE_OFFSET;
    }

    @Override
    public int ARRAY_FLOAT_BASE_OFFSET() {
        return ARRAY_FLOAT_BASE_OFFSET;
    }

    @Override
    public int ARRAY_DOUBLE_BASE_OFFSET() {
        return ARRAY_DOUBLE_BASE_OFFSET;
    }

    @Override
    public int ARRAY_OBJECT_BASE_OFFSET() {
        return ARRAY_OBJECT_BASE_OFFSET;
    }

    @Override
    public int ARRAY_BOOLEAN_INDEX_SCALE() {
        return ARRAY_BOOLEAN_INDEX_SCALE;
    }

    @Override
    public int ARRAY_BYTE_INDEX_SCALE() {
        return ARRAY_BYTE_INDEX_SCALE;
    }

    @Override
    public int ARRAY_SHORT_INDEX_SCALE() {
        return ARRAY_SHORT_INDEX_SCALE;
    }

    @Override
    public int ARRAY_CHAR_INDEX_SCALE() {
        return ARRAY_CHAR_INDEX_SCALE;
    }

    @Override
    public int ARRAY_INT_INDEX_SCALE() {
        return ARRAY_INT_INDEX_SCALE;
    }

    @Override
    public int ARRAY_LONG_INDEX_SCALE() {
        return ARRAY_LONG_INDEX_SCALE;
    }

    @Override
    public int ARRAY_FLOAT_INDEX_SCALE() {
        return ARRAY_FLOAT_INDEX_SCALE;
    }

    @Override
    public int ARRAY_DOUBLE_INDEX_SCALE() {
        return ARRAY_DOUBLE_INDEX_SCALE;
    }

    @Override
    public int ARRAY_OBJECT_INDEX_SCALE() {
        return ARRAY_OBJECT_INDEX_SCALE;
    }

    @Override
    public int ADDRESS_SIZE() {
        return ADDRESS_SIZE;
    }
}
