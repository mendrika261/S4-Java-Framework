package etu2024.framework.utility;

import etu2024.framework.core.File;
import jakarta.el.MethodNotFoundException;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

    public static Object cast(Class<?> type, Object value) throws IOException {
        if(type.isArray()) {
            // Get the type of the array
            Class<?> arrayType = type.getComponentType();
            // Create a new array with the same length as the value
            Object array = Array.newInstance(arrayType, Array.getLength(value));
            // For each element in the value, cast it to the array type and add it to the array
            for (int i = 0; i < Array.getLength(value); i++) {
                Array.set(array, i, cast(arrayType, Array.get(value, i)));
            }
            return array;
        } else if(type == File.class) {
            return new File((Part) value);
        } else if(type.isPrimitive()) {
            try {
                // Convert type to the corresponding wrapper class
                Class<?> wrapperClass = Class.forName("java.lang." + type.getName().substring(0, 1).toUpperCase() +
                        type.getName().substring(1));
                // Get the parse method
                Method method = wrapperClass.getMethod("parse" + type.getName().substring(0, 1).toUpperCase() +
                        type.getName().substring(1), String.class);
                return method.invoke(null, String.valueOf(value));
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | ClassNotFoundException e) {
                throw new MethodNotFoundException("FRAMEWORK ERROR - The type " + type.getName() +
                        " is not supported");
            }
        } else {
            try {
                // Try to get the constructor with a String parameter
                Constructor<?> constructor = type.getConstructor(String.class);
                return constructor.newInstance(String.valueOf(value));
            } catch (NoSuchMethodException e) {
                try {
                    // Try to get the valueOf method with a String parameter
                    Method method = type.getMethod("valueOf", String.class);
                    return method.invoke(null, String.valueOf(value));
                } catch (NoSuchMethodException ex) {
                    if(String.valueOf(value).trim().isEmpty())
                        return null;
                    try {
                        // Try to get the parse method with a String parameter (mostly for LocalDate, LocalDateTime, ...)
                        Method method = type.getMethod("parse", CharSequence.class);
                        return method.invoke(null, String.valueOf(value));
                    } catch (InvocationTargetException | IllegalAccessException exception) {
                        return null;
                    } catch (NoSuchMethodException exception) {
                        // If the type is not supported, throw an exception
                        throw new MethodNotFoundException("FRAMEWORK ERROR - The framework does not support " + type.getName() +
                                " yet!");
                    }
                } catch (InvocationTargetException | IllegalAccessException ignored) {
                    return null;
                }
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                return null;
            }
        }
    }
}
