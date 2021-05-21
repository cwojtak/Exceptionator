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
 * Defines Nashorn shell module.
 *
 * <p>This module includes the command line tool <em>{@index jjs jjs tool}</em>
 * to invoke the Nashorn engine.
 *
 * @toolGuide jjs
 *
 * @moduleGraph
 * @since 9
 */
@Deprecated(since="11", forRemoval=true)
module jdk.scripting.nashorn.shell {
    requires static java.compiler;
    requires jdk.internal.le;
    requires jdk.scripting.nashorn;
    requires jdk.internal.ed;
    uses jdk.internal.editor.spi.BuildInEditorProvider;
}

