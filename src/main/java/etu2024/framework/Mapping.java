package etu2024.framework;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Mapping {
    String className;
    String method;
    public static String BASE_SOURCE = "/Users/mendrika/IdeaProjects/S4-Framework/src/main/java/";

    public Mapping(String className, String method) {
        setClassName(className);
        setMethod(method);
    }

    public static List<String> getClassNameIn(String path) {
        List<String> classNames = new ArrayList<>();

        File file = new File(path);
        for(File f: Objects.requireNonNull(file.listFiles())) {
            if (f.isDirectory()) {
                classNames.addAll(getClassNameIn(f.getPath()));
            } else if (f.isFile() && f.getName().endsWith(".java")) {
                classNames.add(f.getAbsolutePath()
                        .split("\\.java")[0]
                        .split(BASE_SOURCE)[1]
                        .replace("/", "."));
            }
        }

        return classNames;
    }

    public static HashMap<String, Mapping> getAnnotatedUrlMethod(String path) {
        HashMap<String, Mapping> mappings = new HashMap<>();

        try {
            for (String className : getClassNameIn(path)) {
                Class<?> classA = Class.forName(className);
                Method[] methods = classA.getDeclaredMethods();
                for (Method method : methods) {
                    Url url = method.getDeclaredAnnotation(Url.class);
                    if (url != null)
                        mappings.put(url.url(), new Mapping(classA.getName(), method.getName()));
                }
            }
        } catch (ClassNotFoundException e) {
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
