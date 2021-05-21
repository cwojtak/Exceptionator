/*
 * Copyright (c) 2014, 2019, Oracle and/or its affiliates. All rights reserved.
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

/**
 * Defines the <em>{@index rmic rmic}</em> compiler for generating stubs and
 * skeletons using the Java Remote Method Protocol (JRMP) for remote objects.
 *
 * @toolGuide rmic
 *
 * @moduleGraph
 * @since 9
 */
module jdk.rmic {
    requires jdk.compiler;
    requires jdk.javadoc;
}
