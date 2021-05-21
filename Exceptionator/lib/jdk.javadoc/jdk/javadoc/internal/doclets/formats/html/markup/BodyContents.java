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
 *
 *
 */

package jdk.javadoc.internal.doclets.formats.html.markup;

import jdk.javadoc.internal.doclets.toolkit.Content;

import java.util.ArrayList;
import java.util.List;

/**
 * A builder for the contents of the BODY element.
 *
 *  <p><b>This is NOT part of any supported API.
 *  If you write code that depends on this, you do so at your own risk.
 *  This code and its internal interfaces are subject to change or
 *  deletion without notice.</b>
 */
public class BodyContents {

    private List<Content> mainContents = new ArrayList<>();
    private Content header = HtmlTree.EMPTY;
    private Content footer = HtmlTree.EMPTY;

    public BodyContents addMainContent(Content content) {
        mainContents.add(content);
        return this;
    }

    public BodyContents setHeader(Content header) {
        this.header = header;
        return this;
    }

    public BodyContents setFooter(Content footer) {
        this.footer = footer;
        return this;
    }

    /**
     * Returns the HTML for the contents of the BODY element.
     *
     * @return the HTML
     */
    public Content toContent() {
        HtmlTree mainTree = HtmlTree.MAIN();
        mainContents.forEach(mainTree::add);
        HtmlTree flexHeader = HtmlTree.HEADER().setStyle(HtmlStyle.flexHeader);
        flexHeader.add(header);
        HtmlTree flexBox = HtmlTree.DIV(HtmlStyle.flexBox, flexHeader);
        HtmlTree flexContent = HtmlTree.DIV(HtmlStyle.flexContent, mainTree);
        flexContent.add(footer);
        flexBox.add(flexContent);
        return flexBox;
    }
}
