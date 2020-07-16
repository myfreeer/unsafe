package myfreeer.unsafe.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
public class JavaVersionTest {
    @Test
    public void testGetMajorJavaVersion() {
        assertTrue(UnsafeUtils.getMajorJavaVersion() >= 7);
    }


    @Test
    public void testJava7() {
        // http://www.oracle.com/technetwork/java/javase/jdk7-naming-418744.html
        assertEquals(7, UnsafeUtils.JavaVersion.getMajorJavaVersion("1.7.0"));
    }

    @Test
    public void testJava8() {
        assertEquals(8, UnsafeUtils.JavaVersion.getMajorJavaVersion("1.8"));
        assertEquals(8, UnsafeUtils.JavaVersion.getMajorJavaVersion("1.8.0"));
        assertEquals(8, UnsafeUtils.JavaVersion.getMajorJavaVersion("1.8.0_131"));
        assertEquals(8, UnsafeUtils.JavaVersion.getMajorJavaVersion("1.8.0_60-ea"));
        assertEquals(8,
                UnsafeUtils.JavaVersion.getMajorJavaVersion("1.8.0_111-internal"));

        // openjdk8 per https://github.com/AdoptOpenJDK/openjdk-build/issues/93
        assertEquals(8, UnsafeUtils.JavaVersion.getMajorJavaVersion("1.8.0-internal"));
        assertEquals(8,
                UnsafeUtils.JavaVersion.getMajorJavaVersion("1.8.0_131-adoptopenjdk"));
    }

    @Test
    public void testJava9() {
        // Legacy style
        // Oracle JDK 9
        assertEquals(9, UnsafeUtils.JavaVersion.getMajorJavaVersion("9.0.4"));
        // Debian as reported in https://github.com/google/gson/issues/1310
        assertEquals(9, UnsafeUtils.JavaVersion.getMajorJavaVersion("9-Debian"));
        // New style
        assertEquals(9, UnsafeUtils.JavaVersion.getMajorJavaVersion("9-ea+19"));
        assertEquals(9, UnsafeUtils.JavaVersion.getMajorJavaVersion("9+100"));
        assertEquals(9, UnsafeUtils.JavaVersion.getMajorJavaVersion("9.0.1+20"));
        assertEquals(9, UnsafeUtils.JavaVersion.getMajorJavaVersion("9.1.1+20"));
    }

    @Test
    public void testJava10() {
        // Oracle JDK 10.0.1
        assertEquals(10, UnsafeUtils.JavaVersion.getMajorJavaVersion("10.0.1"));
    }

    @Test
    public void testUnknownVersionFormat() {
        // unknown format
        assertEquals(7, UnsafeUtils.JavaVersion.getMajorJavaVersion("Java9"));
    }

    @Test
    public void testJava14() {
        // Open JDK 14.0.1
        assertEquals(14, UnsafeUtils.JavaVersion.getMajorJavaVersion("14.0.1"));
    }

}
