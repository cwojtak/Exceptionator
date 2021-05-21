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
 *
 *
 */

package jdk.incubator.jpackage.internal;

import java.text.MessageFormat;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.File;
import java.io.IOException;

import static jdk.incubator.jpackage.internal.StandardBundlerParam.*;

/**
 * AbstractImageBundler
 *
 * This is the base class for each of the Application Image Bundlers.
 *
 * It contains methods and parameters common to all Image Bundlers.
 *
 * Application Image Bundlers are created in "create-app-image" mode,
 * or as an intermediate step in "create-installer" mode.
 *
 * The concrete implementations are in the platform specific Bundlers.
 */
public abstract class AbstractImageBundler extends AbstractBundler {

    private static final ResourceBundle I18N = ResourceBundle.getBundle(
            "jdk.incubator.jpackage.internal.resources.MainResources");

    public void imageBundleValidation(Map<String, ? super Object> params)
             throws ConfigException {
        StandardBundlerParam.validateMainClassInfoFromAppResources(params);

    }

    protected File createRoot(Map<String, ? super Object> params,
            File outputDirectory, boolean dependentTask, String name)
            throws PackagerException {

        IOUtils.writableOutputDir(outputDirectory.toPath());

        if (!dependentTask) {
            Log.verbose(MessageFormat.format(
                    I18N.getString("message.creating-app-bundle"),
                    name, outputDirectory.getAbsolutePath()));
        }

        // NAME will default to CLASS, so the real problem is no MAIN_CLASS
        if (name == null) {
            throw new PackagerException("ERR_NoMainClass");
        }

        // Create directory structure
        File rootDirectory = new File(outputDirectory, name);

        if (rootDirectory.exists()) {
            throw new PackagerException("error.root-exists",
                    rootDirectory.getAbsolutePath());
        }

        rootDirectory.mkdirs();

        return rootDirectory;
    }

}
