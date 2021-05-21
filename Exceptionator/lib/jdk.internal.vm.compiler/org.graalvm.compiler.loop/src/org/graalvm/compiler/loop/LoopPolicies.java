/*
 * Copyright (c) 2015, 2019, Oracle and/or its affiliates. All rights reserved.
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


package org.graalvm.compiler.loop;

import java.util.List;

import org.graalvm.compiler.nodes.ControlSplitNode;
import org.graalvm.compiler.nodes.cfg.ControlFlowGraph;
import org.graalvm.compiler.options.Option;
import org.graalvm.compiler.options.OptionKey;
import org.graalvm.compiler.options.OptionType;

import jdk.vm.ci.meta.MetaAccessProvider;

public interface LoopPolicies {

    class Options {
        @Option(help = "", type = OptionType.Expert) public static final OptionKey<Boolean> PeelALot = new OptionKey<>(false);
    }

    boolean shouldPeel(LoopEx loop, ControlFlowGraph cfg, MetaAccessProvider metaAccess);

    boolean shouldFullUnroll(LoopEx loop);

    boolean shouldPartiallyUnroll(LoopEx loop);

    boolean shouldTryUnswitch(LoopEx loop);

    boolean shouldUnswitch(LoopEx loop, List<ControlSplitNode> controlSplits);
}
