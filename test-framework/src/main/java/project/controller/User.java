package project.controller;

import etu2024.framework.annotation.Session;
import etu2024.framework.annotation.Url;
import etu2024.framework.core.ModelView;

import java.util.HashMap;

public class User {
    String password;
    String username;
    HashMap<String, Object> sessions;

    @Url(url = "/login")
    @Session
    public ModelView login() {
        ModelView modelView = new ModelView();

        if (username != null && password != null) {

            if (username.equals("admin") && password.equals("admin")) {
                if (modelView.login("admin"))
                    return modelView.redirect("/");
                else
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
        modelView.logout();
        return modelView.redirect("/login");
    }
}
