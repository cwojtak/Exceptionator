/*
 * Copyright (c) 2020, Oracle and/or its affiliates. All rights reserved.
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
package org.graalvm.compiler.hotspot.sparc;

import static jdk.vm.ci.code.ValueUtil.asRegister;
import static org.graalvm.compiler.hotspot.HotSpotHostBackend.DEOPT_BLOB_UNPACK_WITH_EXCEPTION_IN_TLS;

import org.graalvm.compiler.asm.sparc.SPARCAddress;
import org.graalvm.compiler.asm.sparc.SPARCMacroAssembler;
import org.graalvm.compiler.hotspot.GraalHotSpotVMConfig;
import org.graalvm.compiler.lir.LIRInstructionClass;
import org.graalvm.compiler.lir.Opcode;
import org.graalvm.compiler.lir.asm.CompilationResultBuilder;
import org.graalvm.compiler.lir.sparc.SPARCCall;

import jdk.vm.ci.code.Register;
import jdk.vm.ci.meta.Value;
import jdk.vm.ci.sparc.SPARC;

/**
 * Removes the current frame and tail calls the uncommon trap routine.
 */
@Opcode("DEOPT_WITH_EXCEPTION_IN_CALLER")
final class SPARCHotSpotDeoptimizeWithExceptionCallerOp extends SPARCHotSpotEpilogueOp {
    public static final LIRInstructionClass<SPARCHotSpotDeoptimizeWithExceptionCallerOp> TYPE = LIRInstructionClass.create(SPARCHotSpotDeoptimizeWithExceptionCallerOp.class);
    public static final SizeEstimate SIZE = SizeEstimate.create(32);

    private final GraalHotSpotVMConfig config;
    private final Register thread;
    @Use(OperandFlag.REG) private Value exception;

    protected SPARCHotSpotDeoptimizeWithExceptionCallerOp(GraalHotSpotVMConfig config, Value exception, Register thread) {
        super(TYPE, SIZE);
        this.config = config;
        this.exception = exception;
        this.thread = thread;
    }

    @Override
    public void emitCode(CompilationResultBuilder crb, SPARCMacroAssembler masm) {
        Register exc = asRegister(exception);

        // Save exception oop in TLS
        masm.stx(exc, new SPARCAddress(thread, config.threadExceptionOopOffset));
        // Store original return address in TLS
        masm.stx(SPARC.i7, new SPARCAddress(thread, config.threadExceptionPcOffset));

        leaveFrame(crb);

        try (SPARCMacroAssembler.ScratchRegister sc = masm.getScratchRegister()) {
            Register scratch = sc.getRegister();
            SPARCCall.indirectJmp(crb, masm, scratch, crb.foreignCalls.lookupForeignCall(DEOPT_BLOB_UNPACK_WITH_EXCEPTION_IN_TLS));
        }
    }
}
