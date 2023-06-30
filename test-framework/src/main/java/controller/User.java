package controller;

import etu2024.framework.annotation.Session;
import etu2024.framework.annotation.Url;
import etu2024.framework.core.ModelView;
import etu2024.framework.utility.Conf; // Conf class to interact with the configuration file app.xml

import java.util.HashMap;

public class User {
    String password;
    String username;
    HashMap<String, Object> sessions;

    @Url(url = "/login") // Annotation to declare the controller and set the url
    @Session // This annotation is required to use session in the controller
    public ModelView login() {
        ModelView modelView = new ModelView();

        // NOTE: Value is directly get from the attribute of the class or parameter(s) of the function
        //       and must be the same as the input name
        // If the user has submitted the form
        if (username != null && password != null) {

            // Make user credentials check here
            if (username.equals("admin") && password.equals("admin")) {

                // Log in the user as admin, check and redirect
                if (modelView.login("admin"))
                    return modelView.redirect("/");
                    // Same as modelView.redirect(Conf.getAuthRedirections().get("AUTH_REDIRECT_LOGIN")); as set in app.xml

                else
                    // addItem() method is used to send data to the view
                    modelView.addItem("error", "Error while logging in");

            } else
                modelView.addItem("error", "Invalid credentials");
        }

        modelView.setView("login.jsp");
        return modelView;
    }

    @Url(url = "/logout")
    @Session
    public ModelView logout() {
        ModelView modelView = new ModelView();
        modelView.logout(); // Simply log out the user
        return modelView.redirect("/login");
    }
}
