package etu2024.framework.core;

import etu2024.framework.utility.Conf;
import etu2024.framework.utility.User;

import java.util.HashMap;


// This class is used to store the view and the data to be sent to the view
public class ModelView {
    String view;
    HashMap<String, Object> data = new HashMap<>();
    HashMap<String, Object> session = new HashMap<>();
    boolean isJson = false;

    // Constructor
    public ModelView() {}

    public ModelView(String view) {
        setView(view);
    }

    // Auth methods
    public boolean login(String profile) {
        User.profileExists(profile);
        addSessionItem(Conf.getAuthSessionName(), profile);
        return isLogged(profile);
    }

    public boolean loginUser() {
        try {
            return login(Conf.getAuthProfiles().get("AUTH_PROFILE_USER"));
        } catch (Exception e) {
            throw new RuntimeException("FRAMEWORK ERROR - The profile for AUTH_PROFILE_USER does not exist, set it in web.xml");
        }
    }

    public boolean loginAdmin() {
        try {
            return login(Conf.getAuthProfiles().get("AUTH_PROFILE_ADMIN"));
        } catch (Exception e) {
            throw new RuntimeException("FRAMEWORK ERROR - The profile for AUTH_PROFILE_ADMIN does not exist, set it in web.xml");
        }
    }

    public boolean logout() {
        removeSessionItem(Conf.getAuthSessionName());
        return !getSession().containsKey(Conf.getAuthSessionName()) || getSession().get(Conf.getAuthSessionName()) == null;
    }

    public boolean isLogged(String profile) {
        return getSession().containsKey(Conf.getAuthSessionName()) && getSession().get(Conf.getAuthSessionName()) != null &&
                getSession().get(Conf.getAuthSessionName()).equals(profile);
    }

    // Getters and setters
    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public HashMap<String, Object> getData() {
        return data;
    }

    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }

    public void addItem(String key, Object value) {
        getData().put(key, value);
    }

    public HashMap<String, Object> getSession() {
        return session;
    }

    public void setSession(HashMap<String, Object> session) {
        this.session = session;
    }

    public void addSessionItem(String key, Object value) {
        getSession().put(key, value);
    }

    public void removeSessionItem(String key) {
        getSession().put(key, null);
    }

    public boolean isJson() {
        return isJson;
    }

    public void setJson(boolean isJson) {
        this.isJson = isJson;
    }
}
