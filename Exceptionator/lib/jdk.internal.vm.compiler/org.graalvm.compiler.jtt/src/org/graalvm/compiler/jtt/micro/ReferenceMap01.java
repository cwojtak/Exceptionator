/*
 * Copyright (c) 2010, 2018, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */


package org.graalvm.compiler.jtt.micro;

import org.junit.Test;

import org.graalvm.compiler.jtt.JTTTest;

/*
 */
public class ReferenceMap01 extends JTTTest {

    public static Integer val1 = Integer.valueOf(3);
    public static Integer val2 = Integer.valueOf(4);

    @SuppressWarnings("unused")
    private static String foo(String[] a) {
        String[] args = new String[]{"78"};
        Integer i1 = Integer.valueOf(1);
        Integer i2 = Integer.valueOf(2);
        Integer i3 = val1;
        Integer i4 = val2;
        Integer i5 = Integer.valueOf(5);
        Integer i6 = Integer.valueOf(6);
        Integer i7 = Integer.valueOf(7);
        Integer i8 = Integer.valueOf(8);
        Integer i9 = Integer.valueOf(9);
        Integer i10 = Integer.valueOf(10);
        Integer i11 = Integer.valueOf(11);
        Integer i12 = Integer.valueOf(12);

        System.gc();
        int sum = i1 + i2 + i3 + i4 + i5 + i6 + i7 + i8 + i9 + i10 + i11 + i12;
        return args[0] + sum;
    }

    public static int test() {
        return Integer.valueOf(foo(new String[]{"asdf"}));
    }

    @Test
    public void run0() throws Throwable {
        runTest("test");
    }

}
