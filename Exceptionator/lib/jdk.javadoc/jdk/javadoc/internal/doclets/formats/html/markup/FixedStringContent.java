/*
 * Copyright (c) 2010, 2019, Oracle and/or its affiliates. All rights reserved.
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

import java.io.IOException;
import java.io.Writer;

import jdk.javadoc.internal.doclets.toolkit.Content;
import jdk.javadoc.internal.doclets.toolkit.util.DocletConstants;

/**
 * Class for containing fixed string content for HTML tags of javadoc output.
 *
 *  <p><b>This is NOT part of any supported API.
 *  If you write code that depends on this, you do so at your own risk.
 *  This code and its internal interfaces are subject to change or
 *  deletion without notice.</b>
 */
public class FixedStringContent extends Content {
    private final String string;

    /**
     * Constructor to construct FixedStringContent object.
     *
     * @param content content for the object
     */
    public FixedStringContent(CharSequence content) {
        string = Entity.escapeHtmlChars(content);
    }

    /**
     * This method is not supported by the class.
     *
     * @param content content that needs to be added
     * @throws UnsupportedOperationException always
     */
    @Override
    public void add(Content content) {
        throw new UnsupportedOperationException();
    }

    /**
     * Adds content for the StringContent object.  The method escapes
     * HTML characters for the string content that is added.
     *
     * @param strContent string content to be added
     * @throws UnsupportedOperationException always
     */
    @Override
    public void add(CharSequence strContent) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return string.isEmpty();
    }

    @Override
    public int charCount() {
        return RawHtml.charCount(string);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return string;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean write(Writer out, boolean atNewline) throws IOException {
        out.write(string);
        return string.endsWith(DocletConstants.NL);
    }

}
