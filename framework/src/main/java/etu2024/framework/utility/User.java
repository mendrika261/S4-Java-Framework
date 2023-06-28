package etu2024.framework.utility;

import etu2024.framework.annotation.Auth;

import java.lang.reflect.Method;

public class User {
    public static boolean profileExists(String profile) {
        if(Conf.getAuthProfiles().containsValue(profile))
            return true;
        throw new RuntimeException("FRAMEWORK ERROR - The profile " + profile + " does not exist, set it in web.xml");
    }

    // Verify if the user is authorized by checking if the session name
    public static boolean isAuthorized(Method method, Object actualProfile) {
        if(method.isAnnotationPresent(Auth.class)) { // If the method is annotated with @Auth
            String[] requiredProfiles = method.getAnnotation(Auth.class).profiles();

            if(actualProfile != null) { // check if the user is logged in (verify session)
                // If there is no profile specific required
                if (requiredProfiles.length == 0)
                    return true;

                // If there is a profile specific required, check if the user has the required profile
                for (String requiredProfile : requiredProfiles) {
                    if (profileExists(requiredProfile) && actualProfile.equals(requiredProfile))
                        return true;
                }
            }
            return false;
        }
        return true;
    }
}
