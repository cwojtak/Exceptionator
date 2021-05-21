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

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

/**
 * This class manages the temporal bounds associated with a memory segment. A scope has a liveness bit, which is updated
 * when the scope is closed (this operation is triggered by {@link MemorySegmentImpl#close()}). Furthermore, a scope is
 * associated with an <em>atomic</em> counter which can be incremented (upon calling the {@link #acquire()} method),
 * and is decremented (when a previously acquired segment is later closed).
 */
public final class MemoryScope {

    //reference to keep hold onto
    final Object ref;

    int activeCount = UNACQUIRED;

    final static VarHandle COUNT_HANDLE;

    static {
        try {
            COUNT_HANDLE = MethodHandles.lookup().findVarHandle(MemoryScope.class, "activeCount", int.class);
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    final static int UNACQUIRED = 0;
    final static int CLOSED = -1;
    final static int MAX_ACQUIRE = Integer.MAX_VALUE;

    final Runnable cleanupAction;

    public MemoryScope(Object ref, Runnable cleanupAction) {
        this.ref = ref;
        this.cleanupAction = cleanupAction;
    }

    /**
     * This method performs a full, thread-safe liveness check; can be used outside confinement thread.
     */
    final boolean isAliveThreadSafe() {
        return ((int)COUNT_HANDLE.getVolatile(this)) != CLOSED;
    }

    /**
     * This method performs a quick liveness check; must be called from the confinement thread.
     */
    final void checkAliveConfined() {
        if (activeCount == CLOSED) {
            throw new IllegalStateException("Segment is not alive");
        }
    }

    MemoryScope acquire() {
        int value;
        do {
            value = (int)COUNT_HANDLE.getVolatile(this);
            if (value == CLOSED) {
                //segment is not alive!
                throw new IllegalStateException("Segment is not alive");
            } else if (value == MAX_ACQUIRE) {
                //overflow
                throw new IllegalStateException("Segment acquire limit exceeded");
            }
        } while (!COUNT_HANDLE.compareAndSet(this, value, value + 1));
        return new MemoryScope(ref, this::release);
    }

    private void release() {
        int value;
        do {
            value = (int)COUNT_HANDLE.getVolatile(this);
            if (value <= UNACQUIRED) {
                //cannot get here - we can't close segment twice
                throw new IllegalStateException();
            }
        } while (!COUNT_HANDLE.compareAndSet(this, value, value - 1));
    }

    void close() {
        if (!COUNT_HANDLE.compareAndSet(this, UNACQUIRED, CLOSED)) {
            //first check if already closed...
            checkAliveConfined();
            //...if not, then we have acquired views that are still active
            throw new IllegalStateException("Cannot close a segment that has active acquired views");
        }
        if (cleanupAction != null) {
            cleanupAction.run();
        }
    }
}
