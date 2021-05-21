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
package jdk.incubator.jpackage.internal;

import java.nio.file.Path;
import java.util.Map;


/**
 * Application directory layout.
 */
public final class ApplicationLayout implements PathGroup.Facade<ApplicationLayout> {
    enum PathRole {
        RUNTIME, APP, LAUNCHERS, DESKTOP, APP_MODS, DLLS
    }

    ApplicationLayout(Map<Object, Path> paths) {
        data = new PathGroup(paths);
    }

    private ApplicationLayout(PathGroup data) {
        this.data = data;
    }

    @Override
    public PathGroup pathGroup() {
        return data;
    }

    @Override
    public ApplicationLayout resolveAt(Path root) {
        return new ApplicationLayout(pathGroup().resolveAt(root));
    }

    /**
     * Path to launchers directory.
     */
    public Path launchersDirectory() {
        return pathGroup().getPath(PathRole.LAUNCHERS);
    }

    /**
     * Path to directory with dynamic libraries.
     */
    public Path dllDirectory() {
        return pathGroup().getPath(PathRole.DLLS);
    }

    /**
     * Path to application data directory.
     */
    public Path appDirectory() {
        return pathGroup().getPath(PathRole.APP);
    }

    /**
     * Path to Java runtime directory.
     */
    public Path runtimeDirectory() {
        return pathGroup().getPath(PathRole.RUNTIME);
    }

    /**
     * Path to application mods directory.
     */
    public Path appModsDirectory() {
        return pathGroup().getPath(PathRole.APP_MODS);
    }

    /**
     * Path to directory with application's desktop integration files.
     */
    public Path destktopIntegrationDirectory() {
        return pathGroup().getPath(PathRole.DESKTOP);
    }

    static ApplicationLayout linuxAppImage() {
        return new ApplicationLayout(Map.of(
                PathRole.LAUNCHERS, Path.of("bin"),
                PathRole.APP, Path.of("lib/app"),
                PathRole.RUNTIME, Path.of("lib/runtime"),
                PathRole.DESKTOP, Path.of("lib"),
                PathRole.DLLS, Path.of("lib"),
                PathRole.APP_MODS, Path.of("lib/app/mods")
        ));
    }

    static ApplicationLayout windowsAppImage() {
        return new ApplicationLayout(Map.of(
                PathRole.LAUNCHERS, Path.of(""),
                PathRole.APP, Path.of("app"),
                PathRole.RUNTIME, Path.of("runtime"),
                PathRole.DESKTOP, Path.of(""),
                PathRole.DLLS, Path.of(""),
                PathRole.APP_MODS, Path.of("app/mods")
        ));
    }

    static ApplicationLayout macAppImage() {
        return new ApplicationLayout(Map.of(
                PathRole.LAUNCHERS, Path.of("Contents/MacOS"),
                PathRole.APP, Path.of("Contents/app"),
                PathRole.RUNTIME, Path.of("Contents/runtime"),
                PathRole.DESKTOP, Path.of("Contents/Resources"),
                PathRole.DLLS, Path.of("Contents/MacOS"),
                PathRole.APP_MODS, Path.of("Contents/app/mods")
        ));
    }

    public static ApplicationLayout platformAppImage() {
        if (Platform.isWindows()) {
            return windowsAppImage();
        }

        if (Platform.isLinux()) {
            return linuxAppImage();
        }

        if (Platform.isMac()) {
            return macAppImage();
        }

        throw Platform.throwUnknownPlatformError();
    }

    public static ApplicationLayout javaRuntime() {
        return new ApplicationLayout(Map.of(PathRole.RUNTIME, Path.of("")));
    }

    private final PathGroup data;
}
