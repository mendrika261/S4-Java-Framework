package etu2024.framework;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

// This class contains useful methods
public class Tools {
    public static Object cast(Class<?> type, String value) {
        Object object;
        try {
            // Try to cast the value to the type using the constructor with a String parameter
            Constructor<?> constructor = type.getConstructor(String.class);
            object = constructor.newInstance(value);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException ignored) {
            throw new RuntimeException("FRAMEWORK ERROR - The type " + type.getName() +
                    " is not supported, make sure it has a constructor with a String parameter");
        }
        return object;
    }
}
