package myfreeer.unsafe.utils.factory;

import myfreeer.unsafe.utils.IUnsafe;
import myfreeer.unsafe.utils.exception.UnsafeException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Deprecated
public abstract class BaseUnsafeFactory implements UnsafeFactory, UnsafeConstant {

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
    final Class<?> unsafeClass;
    final Object theUnsafe;

    public BaseUnsafeFactory() throws UnsafeException {
        try {
            unsafeClass = Class.forName("sun.misc.Unsafe");
            final Field theUnsafeField = unsafeClass.getDeclaredField("theUnsafe");
            theUnsafeField.setAccessible(true);
            theUnsafe = theUnsafeField.get(null);
            ADDRESS_SIZE = readInt("ADDRESS_SIZE");
            INVALID_FIELD_OFFSET = readInt("INVALID_FIELD_OFFSET");
            ARRAY_BOOLEAN_BASE_OFFSET = readInt("ARRAY_BOOLEAN_BASE_OFFSET");
            ARRAY_BYTE_BASE_OFFSET = readInt("ARRAY_BYTE_BASE_OFFSET");
            ARRAY_SHORT_BASE_OFFSET = readInt("ARRAY_SHORT_BASE_OFFSET");
            ARRAY_CHAR_BASE_OFFSET = readInt("ARRAY_CHAR_BASE_OFFSET");
            ARRAY_INT_BASE_OFFSET = readInt("ARRAY_INT_BASE_OFFSET");
            ARRAY_LONG_BASE_OFFSET = readInt("ARRAY_LONG_BASE_OFFSET");
            ARRAY_FLOAT_BASE_OFFSET = readInt("ARRAY_FLOAT_BASE_OFFSET");
            ARRAY_DOUBLE_BASE_OFFSET = readInt("ARRAY_DOUBLE_BASE_OFFSET");
            ARRAY_OBJECT_BASE_OFFSET = readInt("ARRAY_OBJECT_BASE_OFFSET");
            ARRAY_BOOLEAN_INDEX_SCALE = readInt("ARRAY_BOOLEAN_INDEX_SCALE");
            ARRAY_BYTE_INDEX_SCALE = readInt("ARRAY_BYTE_INDEX_SCALE");
            ARRAY_SHORT_INDEX_SCALE = readInt("ARRAY_SHORT_INDEX_SCALE");
            ARRAY_CHAR_INDEX_SCALE = readInt("ARRAY_CHAR_INDEX_SCALE");
            ARRAY_INT_INDEX_SCALE = readInt("ARRAY_INT_INDEX_SCALE");
            ARRAY_LONG_INDEX_SCALE = readInt("ARRAY_LONG_INDEX_SCALE");
            ARRAY_FLOAT_INDEX_SCALE = readInt("ARRAY_FLOAT_INDEX_SCALE");
            ARRAY_DOUBLE_INDEX_SCALE = readInt("ARRAY_DOUBLE_INDEX_SCALE");
            ARRAY_OBJECT_INDEX_SCALE = readInt("ARRAY_OBJECT_INDEX_SCALE");
        } catch (ClassNotFoundException e) {
            throw new UnsafeException("Unsafe class not found", e);
        } catch (NoSuchFieldException e) {
            throw new UnsafeException(e);
        } catch (IllegalAccessException e) {
            throw new UnsafeException("Can not get unsafe instance", e);
        }
    }

    /**
     * Get the RAW instance of unsafe
     *
     * @return unsafe
     */
    @Override
    public Object getTheUnsafe() {
        return theUnsafe;
    }

    private int readInt(String fieldName)
            throws IllegalAccessException, NoSuchFieldException {
        final Field field = unsafeClass.getDeclaredField(fieldName);
        return field.getInt(null);
    }

    @Override
    public boolean hasMethod(Method method) {
        if (method == null) {
            return false;
        }
        return hasMethod(method.getName(), method.getParameterTypes());
    }

    @Override
    public BaseUnsafeFactory getConstant() {
        return this;
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
