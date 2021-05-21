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

package jdk.internal.access.foreign;

/**
 * This proxy interface is required to allow instances of the {@code MemoryAddress} interface (which is defined inside
 * an incubating module) to be accessed from the memory access var handles.
 */
public interface MemoryAddressProxy {
    /**
     * Check that memory access is within spatial and temporal bounds.
     * @throws IllegalStateException if underlying segment has been closed already.
     * @throws IndexOutOfBoundsException if access is out-of-bounds.
     */
    void checkAccess(long offset, long length, boolean readOnly);
    long unsafeGetOffset();
    Object unsafeGetBase();
}
