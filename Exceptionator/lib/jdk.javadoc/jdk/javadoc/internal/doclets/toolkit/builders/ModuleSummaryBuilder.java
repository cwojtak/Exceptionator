/*
 * Copyright (c) 2013, 2019, Oracle and/or its affiliates. All rights reserved.
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

package jdk.javadoc.internal.doclets.toolkit.builders;

import javax.lang.model.element.ModuleElement;

import jdk.javadoc.internal.doclets.toolkit.Content;
import jdk.javadoc.internal.doclets.toolkit.DocFilesHandler;
import jdk.javadoc.internal.doclets.toolkit.DocletException;
import jdk.javadoc.internal.doclets.toolkit.ModuleSummaryWriter;


/**
 * Builds the summary for a given module.
 *
 *  <p><b>This is NOT part of any supported API.
 *  If you write code that depends on this, you do so at your own risk.
 *  This code and its internal interfaces are subject to change or
 *  deletion without notice.</b>
 */
public class ModuleSummaryBuilder extends AbstractBuilder {

    /**
     * The module being documented.
     */
    private final ModuleElement mdle;

    /**
     * The doclet specific writer that will output the result.
     */
    private final ModuleSummaryWriter moduleWriter;

    /**
     * Construct a new ModuleSummaryBuilder.
     *
     * @param context  the build context.
     * @param mdle the module being documented.
     * @param moduleWriter the doclet specific writer that will output the
     *        result.
     */
    private ModuleSummaryBuilder(Context context,
            ModuleElement mdle, ModuleSummaryWriter moduleWriter) {
        super(context);
        this.mdle = mdle;
        this.moduleWriter = moduleWriter;
    }

    /**
     * Construct a new ModuleSummaryBuilder.
     *
     * @param context  the build context.
     * @param mdle the module being documented.
     * @param moduleWriter the doclet specific writer that will output the
     *        result.
     *
     * @return an instance of a ModuleSummaryBuilder.
     */
    public static ModuleSummaryBuilder getInstance(Context context,
            ModuleElement mdle, ModuleSummaryWriter moduleWriter) {
        return new ModuleSummaryBuilder(context, mdle, moduleWriter);
    }

    /**
     * Build the module summary.
     *
     * @throws DocletException if there is a problem while building the documentation
     */
    @Override
    public void build() throws DocletException {
        if (moduleWriter == null) {
            //Doclet does not support this output.
            return;
        }
        buildModuleDoc();
    }

    /**
     * Build the module documentation.
     *
     * @throws DocletException if there is a problem while building the documentation
     */
    protected void buildModuleDoc() throws DocletException {
        Content contentTree = moduleWriter.getModuleHeader(mdle.getQualifiedName().toString());

        buildContent();

        moduleWriter.addModuleFooter();
        moduleWriter.printDocument(contentTree);
        DocFilesHandler docFilesHandler = configuration.getWriterFactory().getDocFilesHandler(mdle);
        docFilesHandler.copyDocFiles();
    }

    /**
     * Build the content for the module doc.
     *
     * @throws DocletException if there is a problem while building the documentation
     */
    protected void buildContent() throws DocletException {
        Content moduleContentTree = moduleWriter.getContentHeader();

        buildModuleDescription(moduleContentTree);
        buildModuleTags(moduleContentTree);
        buildSummary(moduleContentTree);

        moduleWriter.addModuleContent(moduleContentTree);
    }

    /**
     * Build the module summary.
     *
     * @param moduleContentTree the module content tree to which the summaries will
     *                           be added
     * @throws DocletException if there is a problem while building the documentation
     */
    protected void buildSummary(Content moduleContentTree) throws DocletException {
        Content summaryContentTree = moduleWriter.getSummaryHeader();

        buildPackagesSummary(summaryContentTree);
        buildModulesSummary(summaryContentTree);
        buildServicesSummary(summaryContentTree);

        moduleContentTree.add(moduleWriter.getSummaryTree(summaryContentTree));
    }

    /**
     * Build the modules summary.
     *
     * @param summaryContentTree the content tree to which the summaries will
     *                           be added
     */
    protected void buildModulesSummary(Content summaryContentTree) {
        moduleWriter.addModulesSummary(summaryContentTree);
    }

    /**
     * Build the package summary.
     *
     * @param summaryContentTree the content tree to which the summaries will be added
     */
    protected void buildPackagesSummary(Content summaryContentTree) {
        moduleWriter.addPackagesSummary(summaryContentTree);
    }

    /**
     * Build the services summary.
     *
     * @param summaryContentTree the content tree to which the summaries will be added
     */
    protected void buildServicesSummary(Content summaryContentTree) {
        moduleWriter.addServicesSummary(summaryContentTree);
    }

    /**
     * Build the description for the module.
     *
     * @param moduleContentTree the tree to which the module description will
     *                           be added
     */
    protected void buildModuleDescription(Content moduleContentTree) {
        if (!configuration.nocomment) {
            moduleWriter.addModuleDescription(moduleContentTree);
        }
    }

    /**
     * Build the tags of the summary.
     *
     * @param moduleContentTree the tree to which the module tags will be added
     */
    protected void buildModuleTags(Content moduleContentTree) {
        if (!configuration.nocomment) {
            moduleWriter.addModuleTags(moduleContentTree);
        }
    }
}
