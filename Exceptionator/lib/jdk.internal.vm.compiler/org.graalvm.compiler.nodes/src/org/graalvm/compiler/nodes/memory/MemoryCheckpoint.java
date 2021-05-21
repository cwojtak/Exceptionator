/*
 * Copyright (c) 2011, 2019, Oracle and/or its affiliates. All rights reserved.
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


package org.graalvm.compiler.nodes.memory;

import org.graalvm.compiler.graph.Node;
import org.graalvm.compiler.nodes.FixedNode;
import org.graalvm.compiler.nodes.FixedNodeInterface;
import jdk.internal.vm.compiler.word.LocationIdentity;

/**
 * This interface marks subclasses of {@link FixedNode} that kill a set of memory locations
 * represented by location identities (i.e. change a value at one or more locations that belong to
 * these location identities).
 */
public interface MemoryCheckpoint extends MemoryNode, FixedNodeInterface {

    interface Single extends MemoryCheckpoint {

        /**
         * This method is used to determine which memory location is killed by this node. Returning
         * the special value {@link LocationIdentity#any()} will kill all memory locations.
         *
         * @return the identity of the location killed by this node.
         */
        LocationIdentity getKilledLocationIdentity();
    }

    interface Multi extends MemoryCheckpoint {

        /**
         * This method is used to determine which set of memory locations is killed by this node.
         * Returning the special value {@link LocationIdentity#any()} will kill all memory
         * locations.
         *
         * @return the identities of all locations killed by this node.
         */
        LocationIdentity[] getKilledLocationIdentities();

    }

    class TypeAssertion {

        public static boolean correctType(Node node) {
            return !(node instanceof MemoryCheckpoint) || (node instanceof MemoryCheckpoint.Single ^ node instanceof MemoryCheckpoint.Multi);
        }
    }
}
