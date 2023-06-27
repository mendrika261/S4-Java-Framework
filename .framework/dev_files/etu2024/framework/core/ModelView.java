package etu2024.framework.core;

import java.util.HashMap;


// This class is used to store the view and the data to be sent to the view
public class ModelView {
    String view;
    HashMap<String, Object> data = new HashMap<>();

    // Constructor
    public ModelView(String view) {
        setView(view);
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
        data.put(key, value);
    }
}
