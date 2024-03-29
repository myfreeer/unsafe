package myfreeer.unsafe.utils.test;

import myfreeer.unsafe.utils.IUnsafe;
import myfreeer.unsafe.utils.UnsafeUtils;
import myfreeer.unsafe.utils.accessor.Accessor;
import myfreeer.unsafe.utils.factory.UnsafeFactory;
import myfreeer.unsafe.utils.workaround.Jdk12GetAllFieldsAndMethods;
import myfreeer.unsafe.utils.workaround.Jdk9AllowReflectiveAccess;
import org.junit.Test;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

public class UnsafeTest {
    @Test
    public void initFactory() {
        final UnsafeFactory unsafeFactory = UnsafeUtils.getUnsafeFactory();
        assertNotNull(unsafeFactory);
        assertNotNull(unsafeFactory.getTheUnsafe());
        assertNotNull(unsafeFactory.getUnsafe());
    }

    @Test
    public void initAccessor() {
        assertNotNull(UnsafeUtils.getAccessor());
    }

    @Test
    public void access() throws NoSuchFieldException, IllegalAccessException {
        final Accessor accessor = UnsafeUtils.getAccessor();
        assertNotNull(accessor);
        final Field value = String.class.getDeclaredField("value");
        assertTrue(accessor.access(value));
        assertNotNull(value.get("111"));
    }

    @Test
    public void initLookupFactory() {
        assertNotNull(UnsafeUtils.getLookupFactory());
    }

    @Test
    public void initLookup() {
        assertNotNull(UnsafeUtils.lookup());
        assertNotNull(UnsafeUtils.lookup(MethodHandles.Lookup.class));
    }

    @Test
    public void jdk12() throws NoSuchFieldException {
        if (UnsafeUtils.getMajorJavaVersion() >= 12) {
            assertTrue(Jdk12GetAllFieldsAndMethods.unlock());
            assertTrue(Jdk12GetAllFieldsAndMethods.refreshClass(AccessibleObject.class));
            assertNotNull(AccessibleObject.class.getDeclaredField("override"));
        }
    }

    @Test
    public void jdk9Reflect() throws NoSuchFieldException, IllegalAccessException {
        if (UnsafeUtils.getMajorJavaVersion() < 9) {
            return;
        }
        assertTrue(Jdk9AllowReflectiveAccess.addOpens());

        if (UnsafeUtils.getMajorJavaVersion() >= 12) {
            assertTrue(Jdk12GetAllFieldsAndMethods.unlock());
            assertTrue(Jdk12GetAllFieldsAndMethods.refreshClass(AccessibleObject.class));
        }

        assertTrue(Jdk9AllowReflectiveAccess.disableIllegalAccessLogger());

        final Field override = AccessibleObject.class.getDeclaredField("override");
        override.setAccessible(true);
        override.set(override, true);

    }
    @Test
    public void unsafeTest() throws NoSuchFieldException {
        final Field value = Integer.class.getDeclaredField("value");
        // no caching
        final Integer integer = 128;
        final IUnsafe unsafe = UnsafeUtils.getUnsafe();
        assertNotNull(unsafe);
        final long offset = unsafe.objectFieldOffset(value);
        unsafe.putInt(integer, offset, 127);
        assertEquals(127, (int) integer);
        assertNotSame(127, integer);
    }

    @Test
    public void unsafeTest2() throws InstantiationException {
        final IUnsafe unsafe = UnsafeUtils.getUnsafe();
        assertNotNull(unsafe);
        unsafe.ensureClassInitialized(Void.class);
        final Object o = unsafe.allocateInstance(Void.class);
        assertTrue(o instanceof Void);
    }

    @Test
    public void unsafeTest3() throws NoSuchMethodException {
        int[] arr = new int[]{1, 2, 2, 3, 4, 4, 32, 2, 4, 3, 1};
        int[] arr2 = new int[arr.length];
        final IUnsafe unsafe = UnsafeUtils.getUnsafe();
        assertNotNull(unsafe);
        UnsafeFactory factory = UnsafeUtils.getUnsafeFactory();
        assertNotNull(factory);
        final Method copyMemory = IUnsafe.class.getMethod(
                "copyMemory", long.class, long.class, long.class);
        assertTrue(factory.hasMethod(copyMemory));
        assertTrue(factory.hasMethod(copyMemory.getName(), copyMemory.getParameterTypes()));
        unsafe.copyMemory(arr, factory.getConstant().ARRAY_INT_BASE_OFFSET(),
                arr2, factory.getConstant().ARRAY_INT_BASE_OFFSET(),
                // sizeof(int) -> 4
                arr.length * 4);
        assertArrayEquals(arr, arr2);
    }

    @Test
    public void putLong() {
        byte[] arr = new byte[8];
        final IUnsafe unsafe = UnsafeUtils.getUnsafe();
        assertNotNull(unsafe);
        UnsafeFactory factory = UnsafeUtils.getUnsafeFactory();
        assertNotNull(factory);
        final long l = ThreadLocalRandom.current().nextLong();
        unsafe.putLong(arr, (long)unsafe.arrayBaseOffset(byte[].class), l);
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        byteBuffer.order(ByteOrder.nativeOrder()).putLong(l);
        assertArrayEquals(arr, byteBuffer.array());
    }
}
