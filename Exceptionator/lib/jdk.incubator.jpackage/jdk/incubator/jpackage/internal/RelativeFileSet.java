/*
 * Copyright (c) 2012, 2019, Oracle and/or its affiliates. All rights reserved.
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

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * RelativeFileSet
 *
 * A class encapsulating a directory and a set of files within it.
 */
class RelativeFileSet {

    private File basedir;
    private Set<String> files = new LinkedHashSet<>();

    RelativeFileSet(File base, Collection<File> files) {
        basedir = base;
        String baseAbsolute = basedir.getAbsolutePath();
        for (File f: files) {
            String absolute = f.getAbsolutePath();
            if (!absolute.startsWith(baseAbsolute)) {
                throw new RuntimeException("File " + f.getAbsolutePath() +
                        " does not belong to " + baseAbsolute);
            }
            if (!absolute.equals(baseAbsolute)) {
                    // possible in jpackage case
                this.files.add(absolute.substring(baseAbsolute.length()+1));
            }
        }
    }

    RelativeFileSet(File base, Set<File> files) {
        this(base, (Collection<File>) files);
    }

    File getBaseDirectory() {
        return basedir;
    }

    Set<String> getIncludedFiles() {
        return files;
    }

    @Override
    public String toString() {
        if (files.size() ==  1) {
            return "" + basedir + File.pathSeparator + files;
        }
        return "RelativeFileSet {basedir:" + basedir
                + ", files: {" + files + "}";
    }

}
