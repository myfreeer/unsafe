package myfreeer.unsafe.utils;

import myfreeer.unsafe.utils.accessor.Accessor;
import myfreeer.unsafe.utils.accessor.SafeAccessor;
import myfreeer.unsafe.utils.accessor.UnsafeAccessor;
import myfreeer.unsafe.utils.exception.UnsafeException;
import myfreeer.unsafe.utils.factory.AsmUnsafeFactory;
import myfreeer.unsafe.utils.factory.UnsafeFactory;
import myfreeer.unsafe.utils.invoke.LookupFactory;
import myfreeer.unsafe.utils.log.Logger;
import myfreeer.unsafe.utils.log.Logging;

import java.lang.invoke.MethodHandles;

public class UnsafeUtils {
    private static final Logger log = Logging.getLogger(UnsafeUtils.class);
    private static volatile AsmUnsafeFactory factory = null;
    private static volatile boolean failed = false;
    private static volatile Accessor accessor = null;
    private static volatile boolean accessorFailed = false;

    private UnsafeUtils() {
    }

    public static UnsafeFactory getUnsafeFactory() {
        if (failed) {
            return null;
        }
        if (factory == null) {
            synchronized (UnsafeUtils.class) {
                if (failed) {
                    return null;
                }
                if (factory == null) {
                    try {
                        factory = new AsmUnsafeFactory();
                    } catch (UnsafeException e) {
                        failed = true;
                        log.warn("getUnsafeFactory fail", e);
                    }
                }
            }
        }
        return factory;
    }

    private static boolean failed() {
        if (failed) {
            return true;
        }
        if (factory == null) {
            getUnsafeFactory();
        }
        return factory == null;
    }

    public static LookupFactory getLookupFactory() {
        if (failed()) {
            return null;
        }
        return factory.getUnsafe();
    }

    public static IUnsafe getUnsafe() {
        if (failed()) {
            return null;
        }
        return factory.getUnsafe();
    }

    public static MethodHandles.Lookup lookup() {
        if (failed()) {
            return null;
        }
        return factory.getUnsafe().lookup();
    }

    public static MethodHandles.Lookup lookup(final Class<?> clazz) {
        if (failed()) {
            return null;
        }
        return factory.getUnsafe().lookup(clazz);
    }

    public static Accessor getAccessor() {
        final Accessor accessor = getAccessor0();
        return accessor == null ? new SafeAccessor() : accessor;
    }

    private static Accessor getAccessor0() {
        if (failed()) {
            return null;
        }
        if (accessor == null) {
            synchronized (Accessor.class) {
                if (failed || accessorFailed) {
                    return null;
                }
                if (accessor == null) {
                    try {
                        accessor = new UnsafeAccessor(factory.getUnsafe());
                    } catch (UnsafeException e) {
                        log.warn("getAccessor0 fail", e);
                        accessorFailed = true;
                    }
                }
            }
        }
        return accessor;
    }


    private static final int majorJavaVersion = JavaVersion.determineMajorJavaVersion();

    /**
     * @return the major Java version, i.e. '8' for Java 1.8, '9' for Java 9 etc.
     */
    public static int getMajorJavaVersion() {
        return majorJavaVersion;
    }

    /*
     * Copyright (C) 2017 The Gson authors
     *
     * Licensed under the Apache License, Version 2.0 (the "License");
     * you may not use this file except in compliance with the License.
     * You may obtain a copy of the License at
     *
     * http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     */

    static class JavaVersion {

        private static int determineMajorJavaVersion() {
            String javaVersion = System.getProperty("java.version");
            return getMajorJavaVersion(javaVersion);
        }

        static int getMajorJavaVersion(String javaVersion) {

            // Visible for testing only
            // Oracle defines naming conventions at
            // http://www.oracle.com/technetwork/java/javase/versioning-naming-139433.html
            // However, many alternate implementations differ.
            // For example, Debian used 9-debian as the version string

            int version = parseDotted(javaVersion);
            if (version == -1) {
                version = extractBeginningInt(javaVersion);
            }
            if (version == -1) {
                log.info("JavaVersion.getMajorJavaVersion fail");
                return 7;  // Choose minimum supported JDK version as default
            }
            return version;
        }

        private static int parseDotted(String javaVersion) {
            try {
                // Parses both legacy 1.8 style and newer 9.0.4 style
                String[] parts = javaVersion.split("[._]");
                int firstVer = Integer.parseInt(parts[0]);
                if (firstVer == 1 && parts.length > 1) {
                    return Integer.parseInt(parts[1]);
                } else {
                    return firstVer;
                }
            } catch (NumberFormatException e) {
                return -1;
            }
        }

        private static int extractBeginningInt(String javaVersion) {
            try {
                StringBuilder num = new StringBuilder();
                for (int i = 0; i < javaVersion.length(); ++i) {
                    char c = javaVersion.charAt(i);
                    if (Character.isDigit(c)) {
                        num.append(c);
                    } else {
                        break;
                    }
                }
                return Integer.parseInt(num.toString());
            } catch (NumberFormatException e) {
                return -1;
            }
        }

    }

}
