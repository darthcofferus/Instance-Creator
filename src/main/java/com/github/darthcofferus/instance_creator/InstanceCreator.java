package com.github.darthcofferus.instance_creator;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Search for classes and create instances of them. Works with compiled files and JAR.
 * @version 1.0
 * @author Darth Cofferus
 */
public class InstanceCreator {

    private static final int CREATING_INSTANCES_FROM_PACKAGE = 0, CREATING_INSTANCE = 1;

    private static final String PATH;

    static {
        String path;
        try {
            path = InstanceCreator.class.getClassLoader().getResources("").nextElement().getPath();
        } catch (NoSuchElementException e) {
            path = getPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        PATH = path;
    }

    /** Class instance creation mode. */
    public enum Mode {
        /**
         * Instances of all suitable classes that are
         * not marked with the {@link Ignoring} annotation will be created.
         */
        GREEDY,
        /** Only those suitable classes that are
         * marked with the {@link CreatingInstance} annotation will be instantiated.
         */
        LAZY
    }

    private final Mode mode;

    private final Class<?>[] classes;

    private CreatingInstanceAction creatingInstanceAction = CreatingInstanceAction.DEFAULT;

    /**
     * @param mode the mode in which class instances will be created.
     * @param classes classes to create instances of (you can specify a parent class or interface).
     */
    public InstanceCreator(@NotNull Mode mode, @NotNull Class<?>... classes) {
        this.mode = mode;
        Arrays.stream(classes).forEach(Objects::requireNonNull);
        this.classes = classes;
    }

    private static String getPath() {
        try {
            return InstanceCreator.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Search and create instances of selected classes in all packages of the program.
     */
    public void createInstances() {
        createInstances("");
    }

    /**
     * Search for and create instances of selected classes in a specific package and its subpackages.
     * @param pkg package name. If you want to check all the packages in the program, pass an empty string.
     */
    public void createInstances(@NotNull String pkg) {
        createInstances(pkg, true);
    }

    /**
     * Search for and create instances of selected classes in a specific package (you can include/exclude subpackages).
     * @param pkg package name. If you want to check all the packages in the program, pass an empty string.
     * @param subPackages true: include subpackages, false: exclude subpackages.
     */
    public void createInstances(@NotNull String pkg, boolean subPackages) {
        try {
            if (PATH.endsWith(".jar")) {
                createInstancesInJar(pkg, subPackages);
            } else {
                createInstancesInFiles(pkg, subPackages);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void createInstancesInJar(String pkg, boolean subPackages) throws Exception {
        pkg = pkg.replace('.', '/') + "/";
        try (ZipInputStream zip = new ZipInputStream(new FileInputStream(PATH))) {
            for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
                String name = entry.getName();
                if (pkg.length() == 1 || name.startsWith(pkg)) {
                    switch (getActionId(entry.isDirectory(), subPackages, name)) {
                        case CREATING_INSTANCES_FROM_PACKAGE:
                            createInstancesInJar(name, true);
                            break;
                        case CREATING_INSTANCE:
                            createInstance(crop(name).replace('/', '.'));
                            break;
                    }
                }
            }
        }
    }

    private void createInstancesInFiles(String pkg, boolean subPackages) throws Exception {
        File directory = new File(PATH + pkg.replace('.', '/'));
        for (File file : directory.listFiles()) {
            String p = pkg.isEmpty() ? pkg : pkg + ".";
            switch (getActionId(file.isDirectory(), subPackages, file.getName())) {
                case CREATING_INSTANCES_FROM_PACKAGE:
                    createInstancesInFiles(p + file.getName(), true);
                    break;
                case CREATING_INSTANCE:
                    createInstance(p + crop(file.getName()));
                    break;
            }
        }
    }

    private int getActionId(boolean isDirectory, boolean subPackages, String name) {
        if (isDirectory && subPackages)
            return CREATING_INSTANCES_FROM_PACKAGE;
        else if (!isDirectory && name.endsWith(".class"))
            return CREATING_INSTANCE;
        else return -1;
    }

    private String crop(String rawClassName) {
        return rawClassName.replace(".class", "");
    }

    private void createInstance(String classFullName) throws Exception {
        Class<?> c;
        try {
            c = Class.forName(classFullName);
        } catch (NoClassDefFoundError e) {
            return;
        }
        if ((mode == Mode.GREEDY && c.isAnnotationPresent(Ignoring.class)) ||
                (mode == Mode.LAZY && !c.isAnnotationPresent(CreatingInstance.class)) ||
                c.isInterface() || c.isAnnotation() || c.isEnum()) {
            return;
        }
        for (Class<?> parent : classes) {
            if (parent.isAssignableFrom(c)) {
                creatingInstanceAction.createInstance(c);
                return;
            }
        }
    }

    /**
     * Setting the action to create an instance.
     * @param creatingInstanceAction action to create an instance.
     */
    public void setCreatingInstanceAction(@NotNull CreatingInstanceAction creatingInstanceAction) {
        this.creatingInstanceAction = creatingInstanceAction;
    }

}