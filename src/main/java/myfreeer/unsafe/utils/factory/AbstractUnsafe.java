package myfreeer.unsafe.utils.factory;

import myfreeer.unsafe.utils.IUnsafe;
import myfreeer.unsafe.utils.UnsafeUtils;
import myfreeer.unsafe.utils.invoke.LookupFactory;
import myfreeer.unsafe.utils.log.Logger;
import myfreeer.unsafe.utils.log.Logging;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.security.ProtectionDomain;

public abstract class AbstractUnsafe implements IUnsafe, LookupFactory {
    private static final int JAVA_14 = 14;
    private static final Logger log =
            Logging.getLogger(AbstractUnsafe.class);
    private static final int TRUSTED = -1;

    private volatile MethodHandle lookupConstructor;
    private volatile boolean lookupConstructorFail = false;
    private volatile MethodHandle defineClass1;
    private volatile boolean defineClass1Fail = false;

    private MethodHandle getLookupConstructor() {
        if (lookupConstructorFail) {
            return null;
        }
        final MethodHandle lookupConstructor = this.lookupConstructor;
        if (lookupConstructor != null) {
            return lookupConstructor;
        }
        return getLookupConstructor0();
    }

    private synchronized MethodHandle getLookupConstructor0() {
        if (lookupConstructorFail) {
            return null;
        }
        if (lookupConstructor != null) {
            return lookupConstructor;
        }
        try {
            final Field implLookup =
                    MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            final long staticFieldOffset = staticFieldOffset(implLookup);
            final Object staticFieldBase = staticFieldBase(implLookup);
            ensureClassInitialized(MethodHandles.Lookup.class);
            final MethodHandles.Lookup lookup = (MethodHandles.Lookup)
                    getObject(staticFieldBase, staticFieldOffset);
            if (UnsafeUtils.getMajorJavaVersion() >= JAVA_14) {
                lookupConstructor = lookup.findConstructor(MethodHandles.Lookup.class,
                        MethodType.methodType(void.class, Class.class, Class.class, int.class));
            } else {
                lookupConstructor = lookup.findConstructor(MethodHandles.Lookup.class,
                        MethodType.methodType(void.class, Class.class, int.class));
            }
        } catch (NoSuchMethodException | IllegalAccessException | NoSuchFieldException e) {
            log.warn("lookup() would not work", e);
            lookupConstructor = null;
            lookupConstructorFail = true;
        }
        return lookupConstructor;
    }

    private MethodHandle getDefineClass() {
        if (defineClass1Fail) {
            return null;
        }
        final MethodHandle defineClass1 = this.defineClass1;
        if (defineClass1 != null) {
            return defineClass1;
        }
        return getDefineClass0();
    }

    private synchronized MethodHandle getDefineClass0() {
        if (defineClass1Fail) {
            return null;
        }
        if (defineClass1 != null) {
            return defineClass1;
        }
        final MethodHandles.Lookup lookup = lookup(ClassLoader.class);
        if (lookup == null) {
            defineClass1Fail = true;
            return null;
        }
        try {
            return defineClass1 = lookup.findStatic(ClassLoader.class,
                    "defineClass1",
                    MethodType.methodType(
                            Class.class, ClassLoader.class, String.class,
                            byte[].class, int.class, int.class,
                            ProtectionDomain.class, String.class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            log.warn("Failed to get ClassLoader.defineClass1", e);
            defineClass1Fail = true;
            return null;
        }
    }

    @SuppressWarnings("WeakerAccess")
    public MethodHandles.Lookup lookup(final Class<?> lookupClass,
                                       final int allowedModes) {
        final MethodHandle lookupConstructor = getLookupConstructor();
        if (lookupConstructor == null) {
            return null;
        }
        try {
            if (UnsafeUtils.getMajorJavaVersion() >= JAVA_14) {
                return (MethodHandles.Lookup)
                        lookupConstructor.invokeExact(lookupClass, (Class<?>) null, allowedModes);
            } else {
                return (MethodHandles.Lookup)
                        lookupConstructor.invokeExact(lookupClass, allowedModes);
            }
        } catch (Throwable throwable) {
            log.warn("lookup(" + lookupClass + ") fail", throwable);
            return null;
        }
    }

    @Override
    public MethodHandles.Lookup lookup(final Class<?> lookupClass) {
        return lookup(lookupClass, TRUSTED);
    }

    @Override
    public MethodHandles.Lookup lookup() {
        return lookup(Object.class);
    }

    /// peek and poke operations
    /// (compilers should optimize these to memory ops)

    // These work on object fields in the Java heap.
    // They will not work on elements of packed arrays.

    /**
     * Fetches a reference value from a given Java variable.
     *
     * @see #getInt(Object, long)
     */
    @Override

    public Object getReference(Object o, long offset) {
        return getObject(o, offset);
    }

    /**
     * Stores a reference value into a given Java variable.
     * <p>
     * Unless the reference {@code x} being stored is either null
     * or matches the field type, the results are undefined.
     * If the reference {@code o} is non-null, card marks or
     * other store barriers for that object (if the VM requires them)
     * are updated.
     *
     * @see #putInt(Object, long, int)
     */
    @Override
    public void putReference(Object o, long offset, Object x) {
        putObject(o, offset, x);
    }

    /**
     * Fetches a reference value from a given Java variable.
     *
     * @see #getInt(Object, long)
     */

    @Override
    public Object getObject(Object o, long offset) {
        return getReference(o, offset);
    }

    /**
     * Stores a reference value into a given Java variable.
     * <p>
     * Unless the reference {@code x} being stored is either null
     * or matches the field type, the results are undefined.
     * If the reference {@code o} is non-null, card marks or
     * other store barriers for that object (if the VM requires them)
     * are updated.
     *
     * @see #putInt(Object, long, int)
     */

    @Override
    public void putObject(Object o, long offset, Object x) {
        putReference(o, offset, x);
    }

    /**
     * This method, like all others with 32-bit offsets, was native
     * in a previous release but is now a wrapper which simply casts
     * the offset to a long value.  It provides backward compatibility
     * with bytecodes compiled against 1.4.
     *
     * @deprecated As of 1.4.1, cast the 32-bit offset argument to a long.
     * See {@link #staticFieldOffset}.
     */
    @Override
    @Deprecated
    public int getInt(Object o, int offset) {
        return getInt(o, (long) offset);
    }

    /**
     * @deprecated As of 1.4.1, cast the 32-bit offset argument to a long.
     * See {@link #staticFieldOffset}.
     */
    @Override
    @Deprecated
    public void putInt(Object o, int offset, int x) {
        putInt(o, (long) offset, x);
    }

    /**
     * @deprecated As of 1.4.1, cast the 32-bit offset argument to a long.
     * See {@link #staticFieldOffset}.
     */
    @Override
    @Deprecated
    public Object getObject(Object o, int offset) {
        return getObject(o, (long) offset);
    }

    /**
     * @deprecated As of 1.4.1, cast the 32-bit offset argument to a long.
     * See {@link #staticFieldOffset}.
     */
    @Override
    @Deprecated
    public void putObject(Object o, int offset, Object x) {
        putObject(o, (long) offset, x);
    }

    /**
     * @deprecated As of 1.4.1, cast the 32-bit offset argument to a long.
     * See {@link #staticFieldOffset}.
     */
    @Override
    @Deprecated
    public boolean getBoolean(Object o, int offset) {
        return getBoolean(o, (long) offset);
    }

    /**
     * @deprecated As of 1.4.1, cast the 32-bit offset argument to a long.
     * See {@link #staticFieldOffset}.
     */
    @Override
    @Deprecated
    public void putBoolean(Object o, int offset, boolean x) {
        putBoolean(o, (long) offset, x);
    }

    /**
     * @deprecated As of 1.4.1, cast the 32-bit offset argument to a long.
     * See {@link #staticFieldOffset}.
     */
    @Override
    @Deprecated
    public byte getByte(Object o, int offset) {
        return getByte(o, (long) offset);
    }

    /**
     * @deprecated As of 1.4.1, cast the 32-bit offset argument to a long.
     * See {@link #staticFieldOffset}.
     */
    @Override
    @Deprecated
    public void putByte(Object o, int offset, byte x) {
        putByte(o, (long) offset, x);
    }

    /**
     * @deprecated As of 1.4.1, cast the 32-bit offset argument to a long.
     * See {@link #staticFieldOffset}.
     */
    @Override
    @Deprecated
    public short getShort(Object o, int offset) {
        return getShort(o, (long) offset);
    }

    /**
     * @deprecated As of 1.4.1, cast the 32-bit offset argument to a long.
     * See {@link #staticFieldOffset}.
     */
    @Override
    @Deprecated
    public void putShort(Object o, int offset, short x) {
        putShort(o, (long) offset, x);
    }

    /**
     * @deprecated As of 1.4.1, cast the 32-bit offset argument to a long.
     * See {@link #staticFieldOffset}.
     */
    @Override
    @Deprecated
    public char getChar(Object o, int offset) {
        return getChar(o, (long) offset);
    }

    /**
     * @deprecated As of 1.4.1, cast the 32-bit offset argument to a long.
     * See {@link #staticFieldOffset}.
     */
    @Override
    @Deprecated
    public void putChar(Object o, int offset, char x) {
        putChar(o, (long) offset, x);
    }

    /**
     * @deprecated As of 1.4.1, cast the 32-bit offset argument to a long.
     * See {@link #staticFieldOffset}.
     */
    @Override
    @Deprecated
    public long getLong(Object o, int offset) {
        return getLong(o, (long) offset);
    }

    /**
     * @deprecated As of 1.4.1, cast the 32-bit offset argument to a long.
     * See {@link #staticFieldOffset}.
     */
    @Override
    @Deprecated
    public void putLong(Object o, int offset, long x) {
        putLong(o, (long) offset, x);
    }

    /**
     * @deprecated As of 1.4.1, cast the 32-bit offset argument to a long.
     * See {@link #staticFieldOffset}.
     */
    @Override
    @Deprecated
    public float getFloat(Object o, int offset) {
        return getFloat(o, (long) offset);
    }

    /**
     * @deprecated As of 1.4.1, cast the 32-bit offset argument to a long.
     * See {@link #staticFieldOffset}.
     */
    @Override
    @Deprecated
    public void putFloat(Object o, int offset, float x) {
        putFloat(o, (long) offset, x);
    }

    /**
     * @deprecated As of 1.4.1, cast the 32-bit offset argument to a long.
     * See {@link #staticFieldOffset}.
     */
    @Override
    @Deprecated
    public double getDouble(Object o, int offset) {
        return getDouble(o, (long) offset);
    }

    /**
     * @deprecated As of 1.4.1, cast the 32-bit offset argument to a long.
     * See {@link #staticFieldOffset}.
     */
    @Override
    @Deprecated
    public void putDouble(Object o, int offset, double x) {
        putDouble(o, (long) offset, x);
    }

    /// wrappers for malloc, realloc, free:

    /**
     * Sets all bytes in a given block of memory to a fixed value
     * (usually zero).  This provides a <em>single-register</em> addressing mode,
     * as discussed in {@link #getInt(Object, long)}.
     *
     * <p>Equivalent to <code>setMemory(null, address, bytes, value)</code>.
     */
    @Override
    public void setMemory(long address, long bytes, byte value) {
        setMemory(null, address, bytes, value);
    }

    /// random queries

    /**
     * Returns the offset of a field, truncated to 32 bits.
     * This method is implemented as follows:
     * <blockquote><pre>
     * public int fieldOffset(Field f) {
     *     if (Modifier.isStatic(f.getModifiers()))
     *         return (int) staticFieldOffset(f);
     *     else
     *         return (int) objectFieldOffset(f);
     * }
     * </pre></blockquote>
     *
     * @deprecated As of 1.4.1, use {@link #staticFieldOffset} for static
     * fields and {@link #objectFieldOffset} for non-static fields.
     */
    @Override
    @Deprecated
    public int fieldOffset(Field f) {
        if (Modifier.isStatic(f.getModifiers()))
            return (int) staticFieldOffset(f);
        else
            return (int) objectFieldOffset(f);
    }

    /**
     * Returns the base address for accessing some static field
     * in the given class.  This method is implemented as follows:
     * <blockquote><pre>
     * public Object staticFieldBase(Class c) {
     *     Field[] fields = c.getDeclaredFields();
     *     for (int i = 0; i < fields.length; i++) {
     *         if (Modifier.isStatic(fields[i].getModifiers())) {
     *             return staticFieldBase(fields[i]);
     *         }
     *     }
     *     return null;
     * }
     * </pre></blockquote>
     *
     * @deprecated As of 1.4.1, use {@link #staticFieldBase(Field)}
     * to obtain the base pertaining to a specific {@link Field}.
     * This method works only for JVMs which store all statics
     * for a given class in one place.
     */
    @Override
    @Deprecated
    public Object staticFieldBase(Class<?> c) {
        Field[] fields = c.getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                return staticFieldBase(field);
            }
        }
        return null;
    }


    /// random trusted operations from JNI:

    /**
     * Tells the VM to define a class, without security checks.  By default, the
     * class loader and protection domain come from the caller's class.
     */
    @Override
    public Class<?> defineClass(String name, byte[] b, int off, int len,
                                ClassLoader loader,
                                ProtectionDomain protectionDomain) {
        // Fallback impl for openjdk 9 or later
        final MethodHandles.Lookup lookup = lookup(ClassLoader.class);
        if (lookup == null) {
            return null;
        }
        final MethodHandle defineClass1 = getDefineClass();
        if (defineClass1 == null) {
            return null;
        }
        try {
            return (Class<?>) defineClass1.invokeExact(loader, name,
                    b, off, len, protectionDomain, "_unsafe_");
        } catch (Error | RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            log.warn("Failed to invoke ClassLoader.defineClass1", e);
            return null;
        }
    }

    /**
     * Lock the object.  It must get unlocked via {@link #monitorExit}.
     */
    @Override
    @Deprecated
    public void monitorEnter(Object o) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unlock the object.  It must have been locked via {@link
     * #monitorEnter}.
     */
    @Override
    @Deprecated
    public void monitorExit(Object o) {
        throw new UnsupportedOperationException();
    }

    /**
     * Tries to lock the object.  Returns true or false to indicate
     * whether the lock succeeded.  If it did, the object must be
     * unlocked via {@link #monitorExit}.
     */
    @Override
    @Deprecated
    public boolean tryMonitorEnter(Object o) {
        throw new UnsupportedOperationException();
    }

    /**
     * Atomically updates Java variable to {@code x} if it is currently
     * holding {@code expected}.
     *
     * <p>This operation has memory semantics of a {@code volatile} read
     * and write.  Corresponds to C11 atomic_compare_exchange_strong.
     *
     * @return {@code true} if successful
     */

    @Override
    public boolean compareAndSwapObject(Object o, long offset,
                                        Object expected,
                                        Object x) {
        return compareAndSetReference(o, offset, expected, x);
    }

    /**
     * Atomically updates Java variable to {@code x} if it is currently
     * holding {@code expected}.
     *
     * <p>This operation has memory semantics of a {@code volatile} read
     * and write.  Corresponds to C11 atomic_compare_exchange_strong.
     *
     * @return {@code true} if successful
     */
    @Override
    public boolean compareAndSetInt(Object o, long offset,
                                    int expected,
                                    int x) {
        return compareAndSwapInt(o, offset, expected, x);
    }

    /**
     * Atomically updates Java variable to {@code x} if it is currently
     * holding {@code expected}.
     *
     * <p>This operation has memory semantics of a {@code volatile} read
     * and write.  Corresponds to C11 atomic_compare_exchange_strong.
     *
     * @return {@code true} if successful
     */

    @Override
    public boolean compareAndSwapInt(Object o, long offset,
                                     int expected,
                                     int x) {
        return compareAndSetInt(o, offset, expected, x);
    }

    /**
     * Atomically updates Java variable to {@code x} if it is currently
     * holding {@code expected}.
     *
     * <p>This operation has memory semantics of a {@code volatile} read
     * and write.  Corresponds to C11 atomic_compare_exchange_strong.
     *
     * @return {@code true} if successful
     */
    @Override
    public boolean compareAndSetLong(Object o, long offset,
                                     long expected,
                                     long x) {
        return compareAndSwapLong(o, offset, expected, x);
    }

    /**
     * Atomically updates Java variable to {@code x} if it is currently
     * holding {@code expected}.
     *
     * <p>This operation has memory semantics of a {@code volatile} read
     * and write.  Corresponds to C11 atomic_compare_exchange_strong.
     *
     * @return {@code true} if successful
     */

    @Override
    public boolean compareAndSwapLong(Object o, long offset,
                                      long expected,
                                      long x) {
        return compareAndSetLong(o, offset, expected, x);
    }

    /**
     * Fetches a reference value from a given Java variable, with volatile
     * load semantics. Otherwise identical to {@link #getObject(Object, long)}
     */

    @Override
    public Object getObjectVolatile(Object o, long offset) {
        return getReferenceVolatile(o, offset);
    }

    /**
     * Stores a reference value into a given Java variable, with
     * volatile store semantics. Otherwise identical to
     * {@link #putObject(Object, long, Object)}
     */

    @Override
    public void putObjectVolatile(Object o, long offset, Object x) {
        putReferenceVolatile(o, offset, x);
    }


    /**
     * Fetches a reference value from a given Java variable, with volatile
     * load semantics. Otherwise identical to {@link #getReference(Object, long)}
     */
    @Override
    public Object getReferenceVolatile(Object o, long offset) {
        return getObjectVolatile(o, offset);
    }

    /**
     * Stores a reference value into a given Java variable, with
     * volatile store semantics. Otherwise identical to
     * {@link #putReference(Object, long, Object)}
     */
    @Override
    public void putReferenceVolatile(Object o, long offset, Object x) {
        putObjectVolatile(o, offset, x);
    }


    /**
     * Version of {@link #putObjectVolatile(Object, long, Object)}
     * that does not guarantee immediate visibility of the store to
     * other threads. This method is generally only useful if the
     * underlying field is a Java volatile (or if an array cell, one
     * that is otherwise only accessed using volatile accesses).
     * <p>
     * Corresponds to C11 atomic_store_explicit(..., memory_order_release).
     */

    @Override
    public void putOrderedObject(Object o, long offset, Object x) {
        putReferenceRelease(o, offset, x);
    }

    /**
     * Ordered/Lazy version of {@link #putIntVolatile(Object, long, int)}
     */

    @Override
    public void putOrderedInt(Object o, long offset, int x) {
        putIntRelease(o, offset, x);
    }

    /**
     * Ordered/Lazy version of {@link #putLongVolatile(Object, long, long)}
     */

    @Override
    public void putOrderedLong(Object o, long offset, long x) {
        putLongRelease(o, offset, x);
    }

    /*
     * Versions of {@link #putReferenceVolatile(Object, long, Object)}
     * that do not guarantee immediate visibility of the store to
     * other threads. This method is generally only useful if the
     * underlying field is a Java volatile (or if an array cell, one
     * that is otherwise only accessed using volatile accesses).
     *
     * Corresponds to C11 atomic_store_explicit(..., memory_order_release).
     */

    /**
     * Release version of {@link #putReferenceVolatile(Object, long, Object)}
     */

    @Override
    public void putReferenceRelease(Object o, long offset, Object x) {
        putReferenceVolatile(o, offset, x);
    }

    /**
     * Release version of {@link #putBooleanVolatile(Object, long, boolean)}
     */

    @Override
    public void putBooleanRelease(Object o, long offset, boolean x) {
        putBooleanVolatile(o, offset, x);
    }

    /**
     * Release version of {@link #putByteVolatile(Object, long, byte)}
     */

    @Override
    public void putByteRelease(Object o, long offset, byte x) {
        putByteVolatile(o, offset, x);
    }

    /**
     * Release version of {@link #putShortVolatile(Object, long, short)}
     */

    @Override
    public void putShortRelease(Object o, long offset, short x) {
        putShortVolatile(o, offset, x);
    }

    /**
     * Release version of {@link #putCharVolatile(Object, long, char)}
     */

    @Override
    public void putCharRelease(Object o, long offset, char x) {
        putCharVolatile(o, offset, x);
    }

    /**
     * Release version of {@link #putIntVolatile(Object, long, int)}
     */

    @Override
    public void putIntRelease(Object o, long offset, int x) {
        putIntVolatile(o, offset, x);
    }

    /**
     * Release version of {@link #putFloatVolatile(Object, long, float)}
     */

    @Override
    public void putFloatRelease(Object o, long offset, float x) {
        putFloatVolatile(o, offset, x);
    }

    /**
     * Release version of {@link #putLongVolatile(Object, long, long)}
     */

    @Override
    public void putLongRelease(Object o, long offset, long x) {
        putLongVolatile(o, offset, x);
    }

    /**
     * Release version of {@link #putDoubleVolatile(Object, long, double)}
     */

    @Override
    public void putDoubleRelease(Object o, long offset, double x) {
        putDoubleVolatile(o, offset, x);
    }

    // The following contain CAS-based Java implementations used on
    // platforms not supporting  instructions


    /**
     * Atomically exchanges the given reference value with the current
     * reference value of a field or array element within the given
     * object {@code o} at the given {@code offset}.
     *
     * @param o        object/array to update the field/element in
     * @param offset   field/element offset
     * @param newValue new value
     * @return the previous value
     * @since 1.8
     */
    @Override
    public Object getAndSetObject(Object o, long offset, Object newValue) {
        return getAndSetReference(o, offset, newValue);
    }

    /**
     * Atomically updates Java variable to {@code x} if it is currently
     * holding {@code expected}.
     *
     * <p>This operation has memory semantics of a {@code volatile} read
     * and write.  Corresponds to C11 atomic_compare_exchange_strong.
     *
     * @return {@code true} if successful
     */
    @Override
    public boolean compareAndSetReference(Object o, long offset,
                                          Object expected,
                                          Object x) {
        return compareAndSwapObject(o, offset, expected, x);
    }

    @Override
    public boolean weakCompareAndSetReference(Object o, long offset,
                                              Object expected,
                                              Object x) {
        return compareAndSetReference(o, offset, expected, x);
    }

    /**
     * Atomically exchanges the given reference value with the current
     * reference value of a field or array element within the given
     * object {@code o} at the given {@code offset}.
     *
     * @param o        object/array to update the field/element in
     * @param offset   field/element offset
     * @param newValue new value
     * @return the previous value
     * @since 1.8
     */
    @Override
    public Object getAndSetReference(Object o, long offset, Object newValue) {
        Object v;
        do {
            v = getReferenceVolatile(o, offset);
        } while (!weakCompareAndSetReference(o, offset, v, newValue));
        return v;
    }


    /**
     * Invokes the given direct byte buffer's cleaner, if any.
     *
     * @param directBuffer a direct byte buffer
     * @throws NullPointerException     if {@code directBuffer} is null
     * @throws IllegalArgumentException if {@code directBuffer} is non-direct,
     *                                  or is a {@link java.nio.Buffer#slice slice}, or is a
     *                                  {@link java.nio.Buffer#duplicate duplicate}
     */
    @Override
    public void invokeCleaner(java.nio.ByteBuffer directBuffer) {
        // Fallback impl of invokeCleaner for openjdk 8 or earlier,
        // and jdk.internal.misc.Unsafe for openjdk 9 to 11
        if (!directBuffer.isDirect()) {
            throw new IllegalArgumentException("buffer is non-direct");
        }
        final Class<? extends ByteBuffer> clazz = directBuffer.getClass();
        final MethodHandles.Lookup lookup = lookup(clazz);
        if (lookup == null) {
            log.warn("invokeCleaner: fail to create lookup");
            return;
        }
        try {
            if (lookup
                    .findVirtual(clazz, "attachment", MethodType.methodType(Object.class))
                    .invoke(directBuffer) != null) {
                throw new IllegalArgumentException("duplicate or slice");
            }
            final Method cleanerMethod = clazz.getMethod("cleaner");
            final Class<?> cleanerType = cleanerMethod.getReturnType();
            final Object cleaner = lookup.unreflect(cleanerMethod).invoke(directBuffer);
            if (cleaner == null) {
                return;
            }
            final Method cleanMethod = cleanerType.getMethod("clean");
            lookup.unreflect(cleanMethod).invoke(cleaner);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            log.warn("invokeCleaner fail", e);
        }
    }
}
