package etu2024.framework;

import jakarta.el.MethodNotFoundException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

// This class contains useful methods
public class Tools {
    public static Method getMethodByName(Class<?> type, String name) {
        Method[] methods = type.getDeclaredMethods();
        // For each method, check if the name is the same as the one given in parameter
        for(Method method : methods) {
            if(method.getName().equals(name)) {
                return method;
            }
        }
        // If the method is not found, throw an exception
        throw new MethodNotFoundException("FRAMEWORK ERROR - The method " + name +
                " is not found in the class " + type.getName());
    }

    public static Object cast(Class<?> type, String value) {
        if(type.isPrimitive()) {
            try {
                // Convert type to the corresponding wrapper class
                Class<?> wrapperClass = Class.forName("java.lang." + type.getName().substring(0, 1).toUpperCase() +
                        type.getName().substring(1));
                // Get the parse method
                Method method = wrapperClass.getMethod("parse" + type.getName().substring(0, 1).toUpperCase() +
                        type.getName().substring(1), String.class);
                return method.invoke(null, value);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | ClassNotFoundException e) {
                throw new MethodNotFoundException("FRAMEWORK ERROR - The type " + type.getName() +
                        " is not supported");
            }
        } else {
            try {
                // Try to get the constructor with a String parameter
                Constructor<?> constructor = type.getConstructor(String.class);
                return constructor.newInstance(value);
            } catch (NoSuchMethodException e) {
                try {
                    // Try to get the valueOf method with a String parameter
                    Method method = type.getMethod("valueOf", String.class);
                    return method.invoke(null, value);
                } catch (NoSuchMethodException ex) {
                    // If the constructor and the valueOf method are not found, throw an exception
                    throw new MethodNotFoundException("FRAMEWORK ERROR - The type " + type.getName() +
                            " doesn't have a constructor or a valueOf method with a String parameter");
                } catch (InvocationTargetException | IllegalAccessException ignored) {
                    return null;
                }
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                return null;
            }
        }
    }
}
