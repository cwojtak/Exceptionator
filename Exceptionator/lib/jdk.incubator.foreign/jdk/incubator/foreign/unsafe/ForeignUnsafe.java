/*
 *  Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *  ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 *
 *
 *
 */

package jdk.incubator.foreign.unsafe;

import jdk.incubator.foreign.MemoryAddress;
import jdk.internal.foreign.MemoryAddressImpl;

/**
 * Unsafe methods to allow interop between sun.misc.unsafe and memory access API.
 */
public final class ForeignUnsafe {

    private ForeignUnsafe() {
        //just the one, please
    }

    // The following methods can be used in conjunction with the java.foreign API.

    /**
     * Obtain the base object (if any) associated with this address. This can be used in conjunction with
     * {@link #getUnsafeOffset(MemoryAddress)} in order to obtain a base/offset addressing coordinate pair
     * to be used with methods like {@link sun.misc.Unsafe#getInt(Object, long)} and the likes.
     *
     * @param address the address whose base object is to be obtained.
     * @return the base object associated with the address, or {@code null}.
     */
    public static Object getUnsafeBase(MemoryAddress address) {
        return ((MemoryAddressImpl)address).unsafeGetBase();
    }

    /**
     * Obtain the offset associated with this address. If {@link #getUnsafeBase(MemoryAddress)} returns {@code null} on the passed
     * address, then the offset is to be interpreted as the (absolute) numerical value associated said address.
     * Alternatively, the offset represents the displacement of a field or an array element within the containing
     * base object. This can be used in conjunction with {@link #getUnsafeBase(MemoryAddress)} in order to obtain a base/offset
     * addressing coordinate pair to be used with methods like {@link sun.misc.Unsafe#getInt(Object, long)} and the likes.
     *
     * @param address the address whose offset is to be obtained.
     * @return the offset associated with the address.
     */
    public static long getUnsafeOffset(MemoryAddress address) {
        return ((MemoryAddressImpl)address).unsafeGetOffset();
    }
}
