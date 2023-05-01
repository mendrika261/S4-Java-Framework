package etu2024.framework;

import jakarta.el.MethodNotFoundException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

// This class contains useful methods
public class Tools {
    public static Object cast(Class<?> type, String value) {
        if(type.isPrimitive()) {
            // If the type is a primitive type, throw an exception
            throw new IllegalArgumentException("FRAMEWORK ERROR - The type " + type.getName() +
                    " is a primitive type, use the class instead");
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
                } catch (InvocationTargetException ex) {
                    // If the value can't be cast to the type, throw an exception
                    throw new RuntimeException("FRAMEWORK ERROR - The value " + value +
                            " can't be cast to the type " + type.getName());
                } catch (IllegalAccessException ignored) {
                    return null;
                }
            } catch (InvocationTargetException e) {
                // If the value can't be cast to the type, throw an exception
                throw new RuntimeException("FRAMEWORK ERROR - The value " + value +
                        " can't be cast to the type " + type.getName());
            } catch (InstantiationException | IllegalAccessException e) {
                return null;
            }
        }
    }
}
