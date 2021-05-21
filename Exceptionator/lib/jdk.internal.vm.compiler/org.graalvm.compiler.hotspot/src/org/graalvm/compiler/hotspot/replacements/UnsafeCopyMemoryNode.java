/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
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


package org.graalvm.compiler.hotspot.replacements;

import static org.graalvm.compiler.nodeinfo.InputType.Memory;
import static org.graalvm.compiler.nodeinfo.NodeCycles.CYCLES_64;
import static org.graalvm.compiler.nodeinfo.NodeSize.SIZE_16;

import org.graalvm.compiler.core.common.type.StampFactory;
import org.graalvm.compiler.graph.Node;
import org.graalvm.compiler.graph.NodeClass;
import org.graalvm.compiler.nodeinfo.NodeInfo;
import org.graalvm.compiler.nodes.AbstractStateSplit;
import org.graalvm.compiler.nodes.ValueNode;
import org.graalvm.compiler.nodes.ValueNodeUtil;
import org.graalvm.compiler.nodes.memory.MemoryAccess;
import org.graalvm.compiler.nodes.memory.MemoryCheckpoint;
import org.graalvm.compiler.nodes.memory.MemoryNode;
import org.graalvm.compiler.nodes.spi.Lowerable;
import org.graalvm.compiler.nodes.spi.LoweringTool;
import jdk.internal.vm.compiler.word.LocationIdentity;

@NodeInfo(cycles = CYCLES_64, size = SIZE_16, allowedUsageTypes = {Memory})
public class UnsafeCopyMemoryNode extends AbstractStateSplit implements Lowerable, MemoryCheckpoint.Single, MemoryAccess {

    public static final NodeClass<UnsafeCopyMemoryNode> TYPE = NodeClass.create(UnsafeCopyMemoryNode.class);

    @Input ValueNode receiver;
    @Input ValueNode srcBase;
    @Input ValueNode srcOffset;
    @Input ValueNode destBase;
    @Input ValueNode desOffset;
    @Input ValueNode bytes;

    @OptionalInput(Memory) Node lastLocationAccess;

    private final boolean guarded;

    public UnsafeCopyMemoryNode(boolean guarded, ValueNode receiver, ValueNode srcBase, ValueNode srcOffset, ValueNode destBase, ValueNode desOffset,
                    ValueNode bytes) {
        super(TYPE, StampFactory.forVoid());
        this.guarded = guarded;
        this.receiver = receiver;
        this.srcBase = srcBase;
        this.srcOffset = srcOffset;
        this.destBase = destBase;
        this.desOffset = desOffset;
        this.bytes = bytes;
    }

    public boolean isGuarded() {
        return guarded;
    }

    @Override
    public LocationIdentity getKilledLocationIdentity() {
        return LocationIdentity.any();
    }

    @Override
    public void lower(LoweringTool tool) {
        tool.getLowerer().lower(this, tool);
    }

    @Override
    public LocationIdentity getLocationIdentity() {
        return getKilledLocationIdentity();
    }

    @Override
    public MemoryNode getLastLocationAccess() {
        return (MemoryNode) lastLocationAccess;
    }

    @Override
    public void setLastLocationAccess(MemoryNode lla) {
        Node newLla = ValueNodeUtil.asNode(lla);
        updateUsages(lastLocationAccess, newLla);
        lastLocationAccess = newLla;
    }

    @NodeIntrinsic
    public static native void copyMemory(@ConstantNodeParameter boolean guarded, Object receiver, Object srcBase, long srcOffset, Object destBase, long destOffset, long bytes);
}
