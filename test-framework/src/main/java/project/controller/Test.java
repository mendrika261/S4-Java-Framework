package project.controller;

import etu2024.framework.annotation.Auth;
import etu2024.framework.annotation.RestAPI;
import etu2024.framework.annotation.Session;
import etu2024.framework.annotation.Url;
import etu2024.framework.core.File;
import etu2024.framework.core.ModelView;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;

@Session // The session annotation can be applied to the class or to the method
public class Test {
    String someAttribute = "This is a JSON Response from class converted to JSON";
    LocalDateTime localDateTime = LocalDateTime.now();
    LocalDate localDate = LocalDate.now();
    LocalTime localTime = LocalTime.now();

    // The session attribute is automatically get from the request
    // Don't forget to put it if you want to get sessions
    HashMap<String, Object> sessions = new HashMap<>();

    @Url(url = "/index") // Don't forget to put / at the beginning of each url
    @Auth // The @auth annotation is used to check if the user is logged in
    public ModelView index() {
        ModelView modelView = new ModelView();

        modelView.setView("home.jsp");
        return modelView;
    }

    @Url(url = "/session")
    @Auth(profiles = {"admin", "user"}) // The @auth annotation can take a list of profiles as arguments
    public ModelView session(String session, String remove, String invalidate) {
        ModelView modelView = new ModelView("session.jsp");

        if (session != null) { // Add a session
            modelView.addSessionItem("session", session);
            modelView.setView("/session");
            modelView.setRedirect(true);
            return modelView;
        }

        if (remove != null) { // Remove one session
            modelView.removeSessionItem("session");
            modelView.setView("/session");
            modelView.setRedirect(true);
            return modelView;
        }

        if (invalidate != null) { // Remove all session
            modelView.setInvalidateSession(true);
            modelView.setView("/session");
            modelView.setRedirect(true);
            return modelView;
        }

        // Get session attributes in the session HashMap
        modelView.addItem("session", sessions.get("session"));

        return modelView;
    }

    @Url(url = "/json")
    @Auth
    public ModelView json() {
        ModelView modelView = new ModelView();

        modelView.setJson(true); // Set the response as a JSON response

        // Value in modelView.getData() are automatically converted to JSON
        modelView.addItem("someAttribute", "This is a JSON Response from data set in modelView");
        modelView.addItem("localDateTime", localDateTime);
        modelView.addItem("sessions", sessions);
        return modelView;
    }

    @Url(url = "/json2")
    @Auth
    @RestAPI // Activate the REST API mode, return the object as JSON
    public Test json2() { // For test purpose, let's use the same class as return
        return this;
    }

    @Url(url = "/file")
    @Auth
    public ModelView file(File file, String[] tag, LocalDateTime localDateTime, LocalDate localDate, LocalTime localTime) throws IOException {
        // Like all other parameters (and attributes), the file is automatically get from the request
        ModelView modelView = new ModelView();

        if (file != null) { // To check if it is the post request
            modelView.addItem("file", file.getName() + ": " + file.getContent().length + " bytes received");
            file.uploadToFile("/Users/mendrika/IdeaProjects/S4-Java-Framework/test-framework/output/");
        }
        modelView.addItem("tag", Arrays.toString(tag));
        modelView.addItem("localDateTime", localDateTime);
        modelView.addItem("localDate", localDate);
        modelView.addItem("localTime", localTime);

        modelView.setView("file.jsp");
        return modelView;
    }
}
