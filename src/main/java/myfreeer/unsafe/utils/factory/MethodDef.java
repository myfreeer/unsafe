package myfreeer.unsafe.utils.factory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

final class MethodDef {

    private final String methodName;
    private final Class<?>[] argTypes;
    private final int hashCode;

    private MethodDef(final String methodName, final Class<?>[] argTypes) {
        this.methodName = methodName;
        this.argTypes = argTypes;
        this.hashCode = 31 * Objects.hash(methodName) + Arrays.hashCode(argTypes);
    }

    private MethodDef(final Method method) {
        this.methodName = Objects.requireNonNull(method.getName());
        this.argTypes = Objects.requireNonNull(method.getParameterTypes());
        this.hashCode = 31 * Objects.hash(methodName) + Arrays.hashCode(argTypes);
    }

    static MethodDef of(final Method method) {
        return new MethodDef(method);
    }

    static MethodDef of(final String methodName, final Class<?>... argTypes) {
        return new MethodDef(methodName, argTypes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MethodDef)) return false;
        MethodDef methodDef = (MethodDef) o;
        return methodName.equals(methodDef.methodName) &&
                Arrays.equals(argTypes, methodDef.argTypes);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }
}
