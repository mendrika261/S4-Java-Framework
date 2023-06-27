package etu2024.framework.utility;

import etu2024.framework.annotation.Url;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


// This class is used to store the mapping between the url and the method
public class Mapping {
    String className;
    String method;

    // Constructor
    public Mapping(String className, String method) {
        setClassName(className);
        setMethod(method);
    }

    // Get all the class names in a package
    public static List<String> getClassNameIn(String path, String packageRoot) {
        List<String> classNames = new ArrayList<>();

        // Get the file from the path
        File file = new File(path);
        for(File f: Objects.requireNonNull(file.listFiles())) {
            // If the file is a directory, call the method recursively
            if (f.isDirectory()) {
                classNames.addAll(getClassNameIn(f.getPath(), packageRoot));

            // If the file is a java file, add the class name to the list
            } else if (f.isFile() && f.getName().endsWith(".java")) {
                // Remove the .java extension and the package root from the path
                classNames.add(f.getAbsolutePath()
                        .split("\\.java")[0]
                        .split(packageRoot)[1]
                        .replace("/", "."));
            }
        }

        return classNames;
    }

    // Get all the annotated methods with @Url in a package
    public static HashMap<String, Mapping> getAnnotatedUrlMethod(String path) {
        HashMap<String, Mapping> mappings = new HashMap<>();

        try {
            // Get all the class names in the path
            for (String className : getClassNameIn(path, path)) {
                Class<?> classA = Class.forName(className);
                // Get all the methods in the class
                Method[] methods = classA.getDeclaredMethods();
                for (Method method : methods) {
                    // Get the method annotated with @Url
                    Url url = method.getDeclaredAnnotation(Url.class);
                    if (url != null)
                        mappings.put(url.url(), new Mapping(classA.getName(), method.getName()));
                }
            }
        } catch (ClassNotFoundException ignored) {
        }

        return mappings;
    }

    // Getters and setters
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
