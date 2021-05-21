/*
 * Copyright (c) 2008, 2019, Oracle and/or its affiliates. All rights reserved.
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
 *
 *
 */

package java.lang.invoke;

import sun.invoke.util.Wrapper;

import java.lang.ref.SoftReference;

import static java.lang.invoke.MethodHandleStatics.newIllegalArgumentException;

/**
 * Shared information for a group of method types, which differ
 * only by reference types, and therefore share a common erasure
 * and wrapping.
 * <p>
 * For an empirical discussion of the structure of method types,
 * see <a href="http://groups.google.com/group/jvm-languages/browse_thread/thread/ac9308ae74da9b7e/">
 * the thread "Avoiding Boxing" on jvm-languages</a>.
 * There are approximately 2000 distinct erased method types in the JDK.
 * There are a little over 10 times that number of unerased types.
 * No more than half of these are likely to be loaded at once.
 * @author John Rose
 */
final class MethodTypeForm {
    final short parameterSlotCount;
    final short primitiveCount;
    final MethodType erasedType;        // the canonical erasure
    final MethodType basicType;         // the canonical erasure, with primitives simplified

    // Cached adapter information:
    final SoftReference<MethodHandle>[] methodHandles;

    // Indexes into methodHandles:
    static final int
            MH_BASIC_INV      =  0,  // cached instance of MH.invokeBasic
            MH_NF_INV         =  1,  // cached helper for LF.NamedFunction
            MH_UNINIT_CS      =  2,  // uninitialized call site
            MH_LIMIT          =  3;

    // Cached lambda form information, for basic types only:
    final SoftReference<LambdaForm>[] lambdaForms;

    // Indexes into lambdaForms:
    static final int
            LF_INVVIRTUAL              =  0,  // DMH invokeVirtual
            LF_INVSTATIC               =  1,
            LF_INVSPECIAL              =  2,
            LF_NEWINVSPECIAL           =  3,
            LF_INVINTERFACE            =  4,
            LF_INVSTATIC_INIT          =  5,  // DMH invokeStatic with <clinit> barrier
            LF_INTERPRET               =  6,  // LF interpreter
            LF_REBIND                  =  7,  // BoundMethodHandle
            LF_DELEGATE                =  8,  // DelegatingMethodHandle
            LF_DELEGATE_BLOCK_INLINING =  9,  // Counting DelegatingMethodHandle w/ @DontInline
            LF_EX_LINKER               = 10,  // invokeExact_MT (for invokehandle)
            LF_EX_INVOKER              = 11,  // MHs.invokeExact
            LF_GEN_LINKER              = 12,  // generic invoke_MT (for invokehandle)
            LF_GEN_INVOKER             = 13,  // generic MHs.invoke
            LF_CS_LINKER               = 14,  // linkToCallSite_CS
            LF_MH_LINKER               = 15,  // linkToCallSite_MH
            LF_GWC                     = 16,  // guardWithCatch (catchException)
            LF_GWT                     = 17,  // guardWithTest
            LF_TF                      = 18,  // tryFinally
            LF_LOOP                    = 19,  // loop
            LF_INVSPECIAL_IFC          = 20,  // DMH invokeSpecial of (private) interface method
            LF_LIMIT                   = 21;

    /** Return the type corresponding uniquely (1-1) to this MT-form.
     *  It might have any primitive returns or arguments, but will have no references except Object.
     */
    public MethodType erasedType() {
        return erasedType;
    }

    /** Return the basic type derived from the erased type of this MT-form.
     *  A basic type is erased (all references Object) and also has all primitive
     *  types (except int, long, float, double, void) normalized to int.
     *  Such basic types correspond to low-level JVM calling sequences.
     */
    public MethodType basicType() {
        return basicType;
    }

    public MethodHandle cachedMethodHandle(int which) {
        SoftReference<MethodHandle> entry = methodHandles[which];
        return (entry != null) ? entry.get() : null;
    }

    public synchronized MethodHandle setCachedMethodHandle(int which, MethodHandle mh) {
        // Simulate a CAS, to avoid racy duplication of results.
        SoftReference<MethodHandle> entry = methodHandles[which];
        if (entry != null) {
            MethodHandle prev = entry.get();
            if (prev != null) {
                return prev;
            }
        }
        methodHandles[which] = new SoftReference<>(mh);
        return mh;
    }

    public LambdaForm cachedLambdaForm(int which) {
        SoftReference<LambdaForm> entry = lambdaForms[which];
        return (entry != null) ? entry.get() : null;
    }

    public synchronized LambdaForm setCachedLambdaForm(int which, LambdaForm form) {
        // Simulate a CAS, to avoid racy duplication of results.
        SoftReference<LambdaForm> entry = lambdaForms[which];
        if (entry != null) {
            LambdaForm prev = entry.get();
            if (prev != null) {
                return prev;
            }
        }
        lambdaForms[which] = new SoftReference<>(form);
        return form;
    }

    /**
     * Build an MTF for a given type, which must have all references erased to Object.
     * This MTF will stand for that type and all un-erased variations.
     * Eagerly compute some basic properties of the type, common to all variations.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected MethodTypeForm(MethodType erasedType) {
        this.erasedType = erasedType;

        Class<?>[] ptypes = erasedType.ptypes();
        int pslotCount = ptypes.length;

        // Walk the argument types, looking for primitives.
        short primitiveCount = 0, longArgCount = 0;
        Class<?>[] erasedPtypes = ptypes;
        Class<?>[] basicPtypes = erasedPtypes;
        for (int i = 0; i < erasedPtypes.length; i++) {
            Class<?> ptype = erasedPtypes[i];
            if (ptype != Object.class) {
                ++primitiveCount;
                Wrapper w = Wrapper.forPrimitiveType(ptype);
                if (w.isDoubleWord())  ++longArgCount;
                if (w.isSubwordOrInt() && ptype != int.class) {
                    if (basicPtypes == erasedPtypes)
                        basicPtypes = basicPtypes.clone();
                    basicPtypes[i] = int.class;
                }
            }
        }
        pslotCount += longArgCount;                  // #slots = #args + #longs
        Class<?> returnType = erasedType.returnType();
        Class<?> basicReturnType = returnType;
        if (returnType != Object.class) {
            ++primitiveCount; // even void.class counts as a prim here
            Wrapper w = Wrapper.forPrimitiveType(returnType);
            if (w.isSubwordOrInt() && returnType != int.class)
                basicReturnType = int.class;
        }
        if (erasedPtypes == basicPtypes && basicReturnType == returnType) {
            // Basic type
            this.basicType = erasedType;

            if (pslotCount >= 256)  throw newIllegalArgumentException("too many arguments");

            this.primitiveCount = primitiveCount;
            this.parameterSlotCount = (short)pslotCount;
            this.lambdaForms   = new SoftReference[LF_LIMIT];
            this.methodHandles = new SoftReference[MH_LIMIT];
        } else {
            this.basicType = MethodType.makeImpl(basicReturnType, basicPtypes, true);
            // fill in rest of data from the basic type:
            MethodTypeForm that = this.basicType.form();
            assert(this != that);

            this.parameterSlotCount = that.parameterSlotCount;
            this.primitiveCount = that.primitiveCount;
            this.methodHandles = null;
            this.lambdaForms = null;
        }
    }

    public int parameterCount() {
        return erasedType.parameterCount();
    }
    public int parameterSlotCount() {
        return parameterSlotCount;
    }
    public boolean hasPrimitives() {
        return primitiveCount != 0;
    }

    static MethodTypeForm findForm(MethodType mt) {
        MethodType erased = canonicalize(mt, ERASE, ERASE);
        if (erased == null) {
            // It is already erased.  Make a new MethodTypeForm.
            return new MethodTypeForm(mt);
        } else {
            // Share the MethodTypeForm with the erased version.
            return erased.form();
        }
    }

    /** Codes for {@link #canonicalize(java.lang.Class, int)}.
     * ERASE means change every reference to {@code Object}.
     * WRAP means convert primitives (including {@code void} to their
     * corresponding wrapper types.  UNWRAP means the reverse of WRAP.
     * INTS means convert all non-void primitive types to int or long,
     * according to size.  LONGS means convert all non-void primitives
     * to long, regardless of size.  RAW_RETURN means convert a type
     * (assumed to be a return type) to int if it is smaller than an int,
     * or if it is void.
     */
    public static final int ERASE = 1, WRAP = 2, UNWRAP = 3, INTS = 4, LONGS = 5, RAW_RETURN = 6;

    /** Canonicalize the types in the given method type.
     * If any types change, intern the new type, and return it.
     * Otherwise return null.
     */
    public static MethodType canonicalize(MethodType mt, int howRet, int howArgs) {
        Class<?>[] ptypes = mt.ptypes();
        Class<?>[] ptypesCanonical = canonicalizeAll(ptypes, howArgs);
        Class<?> rtype = mt.returnType();
        Class<?> rtypeCanonical = canonicalize(rtype, howRet);
        if (ptypesCanonical == null && rtypeCanonical == null) {
            // It is already canonical.
            return null;
        }
        // Find the erased version of the method type:
        if (rtypeCanonical == null)  rtypeCanonical = rtype;
        if (ptypesCanonical == null)  ptypesCanonical = ptypes;
        return MethodType.makeImpl(rtypeCanonical, ptypesCanonical, true);
    }

    /** Canonicalize the given return or param type.
     *  Return null if the type is already canonicalized.
     */
    static Class<?> canonicalize(Class<?> t, int how) {
        Class<?> ct;
        if (t == Object.class) {
            // no change, ever
        } else if (!t.isPrimitive()) {
            switch (how) {
                case UNWRAP:
                    ct = Wrapper.asPrimitiveType(t);
                    if (ct != t)  return ct;
                    break;
                case RAW_RETURN:
                case ERASE:
                    return Object.class;
            }
        } else if (t == void.class) {
            // no change, usually
            switch (how) {
                case RAW_RETURN:
                    return int.class;
                case WRAP:
                    return Void.class;
            }
        } else {
            // non-void primitive
            switch (how) {
                case WRAP:
                    return Wrapper.asWrapperType(t);
                case INTS:
                    if (t == int.class || t == long.class)
                        return null;  // no change
                    if (t == double.class)
                        return long.class;
                    return int.class;
                case LONGS:
                    if (t == long.class)
                        return null;  // no change
                    return long.class;
                case RAW_RETURN:
                    if (t == int.class || t == long.class ||
                        t == float.class || t == double.class)
                        return null;  // no change
                    // everything else returns as an int
                    return int.class;
            }
        }
        // no change; return null to signify
        return null;
    }

    /** Canonicalize each param type in the given array.
     *  Return null if all types are already canonicalized.
     */
    static Class<?>[] canonicalizeAll(Class<?>[] ts, int how) {
        Class<?>[] cs = null;
        for (int imax = ts.length, i = 0; i < imax; i++) {
            Class<?> c = canonicalize(ts[i], how);
            // Void parameters may be unwrapped to void; ignore those
            if (c != null && c != void.class) {
                if (cs == null)
                    cs = ts.clone();
                cs[i] = c;
            }
        }
        return cs;
    }

    @Override
    public String toString() {
        return "Form"+erasedType;
    }
}