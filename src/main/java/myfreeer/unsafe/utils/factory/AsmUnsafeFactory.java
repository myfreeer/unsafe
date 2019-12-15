package myfreeer.unsafe.utils.factory;

import myfreeer.unsafe.utils.IUnsafe;
import myfreeer.unsafe.utils.exception.UnsafeException;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.Opcodes.*;

public class AsmUnsafeFactory implements UnsafeFactory {

    private final Set<MethodDef> methodDefs;
    private final AbstractUnsafe unsafe;
    private final UnsafeConstantImpl constant;
    private final Object theUnsafe;

    public AsmUnsafeFactory() {
        Class<?> unsafeClass;
        try {
            unsafeClass = Class.forName("sun.misc.Unsafe");
            final Field theUnsafeField = unsafeClass.getDeclaredField("theUnsafe");
            theUnsafeField.setAccessible(true);
            theUnsafe = theUnsafeField.get(null);
        } catch (ClassNotFoundException e) {
            throw new UnsafeException("Unsafe class not found", e);
        } catch (NoSuchFieldException e) {
            throw new UnsafeException(e);
        } catch (IllegalAccessException e) {
            throw new UnsafeException("Can not get unsafe instance", e);
        }
        final ByteCodeClassLoader loader = new ByteCodeClassLoader();
        final String proxyClassName = AsmUnsafeFactory.class.getName() +
                "_Proxy_" +
                Long.toHexString(ThreadLocalRandom.current().nextLong());
        final byte[] bytes = assembleByteCode(unsafeClass,
                proxyClassName,
                AbstractUnsafe.class,
                new Class<?>[]{IUnsafe.class});
        final Class<?> proxyClass = loader.defineClass(proxyClassName, bytes);
        final Method[] methods = proxyClass.getMethods();
        final ConcurrentMap<MethodDef, Boolean> map =
                new ConcurrentHashMap<>(methods.length + (methods.length >> 2));
        for (final Method method : methods) {
            if (Modifier.isAbstract(method.getModifiers())) {
                continue;
            }
            map.put(MethodDef.of(method), Boolean.TRUE);
        }
        methodDefs = map.keySet();

        try {
            //noinspection JavaReflectionInvocation
            unsafe = (AbstractUnsafe) proxyClass.getConstructor(unsafeClass)
                    .newInstance(theUnsafe);
        } catch (InstantiationException | IllegalAccessException |
                InvocationTargetException | NoSuchMethodException e) {
            throw new UnsafeException("unsafe init", e);
        }
        constant = new UnsafeConstantImpl(unsafe);
    }

    @Override
    public Object getTheUnsafe() {
        return theUnsafe;
    }

    @Override
    public AbstractUnsafe getUnsafe() throws UnsafeException {
        return unsafe;
    }

    @Override
    public boolean hasMethod(Method method) {
        return methodDefs.contains(MethodDef.of(method));
    }

    @Override
    public boolean hasMethod(String methodName, Class<?>... parameterTypes) {
        return methodDefs.contains(MethodDef.of(methodName, parameterTypes));
    }

    @Override
    public UnsafeConstant getConstant() {
        return constant;
    }

    protected static String getInternalName(String className) {
        if (className == null) {
            return null;
        }
        return className.replace('.', '/');
    }

    protected static String[] getInternalName(String[] className) {
        if (className == null) {
            return null;
        }
        String[] byteCodeClassName = new String[className.length];
        for (int i = 0; i < className.length; i++) {
            byteCodeClassName[i] = getInternalName(className[i]);
        }
        return byteCodeClassName;
    }

    protected static String[] getInternalName(Class<?>[] className) {
        if (className == null) {
            return null;
        }
        String[] byteCodeClassName = new String[className.length];
        for (int i = 0; i < className.length; i++) {
            byteCodeClassName[i] = Type.getInternalName(className[i]);
        }
        return byteCodeClassName;
    }

    protected static String superClass(String className) {
        return className == null ?
                Type.getInternalName(Object.class) :
                getInternalName(className);
    }


    private static final Class<?>[] TYPE_TABLE = {
            boolean.class, byte.class, char.class, short.class, int.class,
            long.class,
            float.class,
            double.class,
            void.class
    };

    private static final int[] LOAD_TABLE = {
            // int
            ILOAD, ILOAD, ILOAD, ILOAD, ILOAD,
            // long
            LLOAD,
            // float
            FLOAD,
            // double
            DLOAD,
            // would fail here
            -1
    };

    private static final int[] RETURN_TABLE = {
            // int
            IRETURN, IRETURN, IRETURN, IRETURN, IRETURN,
            // long
            LRETURN,
            // float
            FRETURN,
            // double
            DRETURN,
            // void
            RETURN
    };

    protected static int loadInsn(Class<?> type) {
        if (!type.isPrimitive()) {
            return ALOAD;
        }
        for (int i = 0; i < TYPE_TABLE.length; i++) {
            if (TYPE_TABLE[i] == type) {
                return LOAD_TABLE[i];
            }
        }
        return ALOAD;
    }

    protected static int returnInsn(Class<?> type) {
        if (!type.isPrimitive()) {
            return ARETURN;
        }
        for (int i = 0; i < TYPE_TABLE.length; i++) {
            if (TYPE_TABLE[i] == type) {
                return RETURN_TABLE[i];
            }
        }
        return ARETURN;
    }

    protected static byte[] assembleByteCode(
            String proxyClassName,
            String superName,
            String[] interfaces,
            Class<?> unsafeClass) {
        return assembleByteCode(unsafeClass,
                getInternalName(proxyClassName),
                superClass(superName),
                getInternalName(interfaces),
                unsafeClass.getMethods());
    }

    protected static byte[] assembleByteCode(final Class<?> unsafeClass,
                                             final String proxyClass) {
        return assembleByteCode(unsafeClass,
                getInternalName(proxyClass),
                Type.getInternalName(Object.class),
                null,
                unsafeClass.getMethods());
    }

    protected static byte[] assembleByteCode(final Class<?> unsafeClass,
                                             final String proxyClass,
                                             final Class<?> superClass,
                                             final Class<?>[] interfaces) {
        return assembleByteCode(unsafeClass,
                getInternalName(proxyClass),
                Type.getInternalName(superClass == null ? Object.class : superClass),
                getInternalName(interfaces),
                unsafeClass.getMethods());
    }

    protected static byte[] assembleByteCode(final Class<?> unsafeClass,
                                             final String proxyClassInternal,
                                             final String superClassInternal,
                                             final String[] interfacesInternal,
                                             final Method[] methods) {
        final String fieldName = "unsafe";
        final String unsafeClassNameForByteCode = Type.getInternalName(unsafeClass);

        final String unsafeDescriptor = Type.getDescriptor(unsafeClass);
        ClassWriter cw = new ClassWriter(COMPUTE_FRAMES);
        cw.visit(V1_7, ACC_PUBLIC, proxyClassInternal, null,
                superClassInternal, interfacesInternal);
        final FieldVisitor fv = cw.visitField(ACC_PRIVATE | ACC_FINAL | ACC_TRANSIENT,
                fieldName, unsafeDescriptor, null, null);
        fv.visitEnd();

        constructor(proxyClassInternal, fieldName, superClassInternal, unsafeDescriptor, cw);

        for (final Method method : methods) {
            proxyMethod(unsafeClassNameForByteCode, proxyClassInternal,
                    fieldName, unsafeDescriptor, cw, method);
        }

        cw.visitEnd();
        return cw.toByteArray();
    }

    private static void proxyMethod(final String proxyTargetClass,
                                    final String ownerClass,
                                    final String fieldName,
                                    final String unsafeDescriptor,
                                    final ClassWriter cw,
                                    final Method method) {
        if (method.getDeclaringClass() == Object.class ||
                Modifier.isStatic(method.getModifiers())) {
            return;
        }
        final Class<?> returnType = method.getReturnType();
        final Class<?>[] parameterTypes = method.getParameterTypes();
        final Class<?>[] exceptionTypes = method.getExceptionTypes();
        final String methodDescriptor = Type.getMethodDescriptor(method);

        final MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, method.getName(),
                methodDescriptor,
                null, getInternalName(exceptionTypes));
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, ownerClass, fieldName, unsafeDescriptor);
        for (int i = 0, j = 1, l, len = parameterTypes.length; i < len; i++, j++) {
            l = loadInsn(parameterTypes[i]);
            mv.visitVarInsn(l, j);
            if (l == LLOAD || l == DLOAD) {
                j++;
            }
        }
        mv.visitMethodInsn(INVOKEVIRTUAL, proxyTargetClass, method.getName(),
                methodDescriptor, false);
        mv.visitInsn(returnInsn(returnType));
        // computed by asm
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    private static void constructor(final String ownerClass,
                                    final String fieldName,
                                    final String superClass,
                                    final String fieldDescriptor,
                                    final ClassWriter cw) {
        final MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>",
                "(" + fieldDescriptor + ")V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, superClass,
                "<init>", "()V", false);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitFieldInsn(PUTFIELD, ownerClass, fieldName, fieldDescriptor);
        mv.visitInsn(RETURN);
        // computed by asm
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    static class ByteCodeClassLoader extends ClassLoader {
        Class<?> defineClass(String name, byte[] code) {
            return defineClass(name, code, 0, code.length);
        }
    }

}
