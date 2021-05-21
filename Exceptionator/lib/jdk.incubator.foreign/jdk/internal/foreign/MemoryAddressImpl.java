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
package jdk.internal.foreign;

import jdk.internal.access.foreign.MemoryAddressProxy;
import jdk.internal.misc.Unsafe;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;

import java.lang.ref.Reference;
import java.util.Objects;

/**
 * This class provides an immutable implementation for the {@code MemoryAddress} interface. This class contains information
 * about the segment this address is associated with, as well as an offset into such segment.
 */
public final class MemoryAddressImpl implements MemoryAddress, MemoryAddressProxy {

    private static final Unsafe UNSAFE = Unsafe.getUnsafe();

    private final MemorySegmentImpl segment;
    private final long offset;

    public MemoryAddressImpl(MemorySegmentImpl segment, long offset) {
        this.segment = Objects.requireNonNull(segment);
        this.offset = offset;
    }

    public static void copy(MemoryAddressImpl src, MemoryAddressImpl dst, long size) {
        src.checkAccess(0, size, true);
        dst.checkAccess(0, size, false);
        //check disjoint
        long offsetSrc = src.unsafeGetOffset();
        long offsetDst = dst.unsafeGetOffset();
        Object baseSrc = src.unsafeGetBase();
        Object baseDst = dst.unsafeGetBase();
        UNSAFE.copyMemory(baseSrc, offsetSrc, baseDst, offsetDst, size);
    }

    // MemoryAddress methods

    @Override
    public long offset() {
        return offset;
    }

    @Override
    public MemorySegment segment() {
        return segment;
    }

    @Override
    public MemoryAddress addOffset(long bytes) {
        return new MemoryAddressImpl(segment, offset + bytes);
    }

    // MemoryAddressProxy methods

    public void checkAccess(long offset, long length, boolean readOnly) {
        segment.checkRange(this.offset + offset, length, !readOnly);
    }

    public long unsafeGetOffset() {
        return segment.min + offset;
    }

    public Object unsafeGetBase() {
        return segment.base();
    }

    // Object methods

    @Override
    public int hashCode() {
        return Objects.hash(unsafeGetBase(), unsafeGetOffset());
    }

    @Override
    public boolean equals(Object that) {
        if (that instanceof MemoryAddressImpl) {
            MemoryAddressImpl addr = (MemoryAddressImpl)that;
            return Objects.equals(unsafeGetBase(), ((MemoryAddressImpl) that).unsafeGetBase()) &&
                    unsafeGetOffset() == addr.unsafeGetOffset();
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "MemoryAddress{ region: " + segment + " offset=0x" + Long.toHexString(offset) + " }";
    }
}
