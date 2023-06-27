package etu2024.framework.core;

import jakarta.servlet.http.Part;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

// This class represents a file uploaded from the client
public class File {
    String name;
    String path;
    byte[] content = new byte[0];

    // Constructor
    public File(String name, String path, byte[] content) {
        setName(name);
        setPath(path);
        setContent(content);
    }

    public File(Part filePart) throws IOException {
        setContent(File.partToByte(filePart));
    }

    // To check if the file is ready to be used (if it has been uploaded)
    public boolean isReady() {
        return getContent().length > 0;
    }

    // This method converts a Part object to a byte array
    public static byte[] partToByte(Part filePart) throws IOException {
        InputStream inputStream = filePart.getInputStream();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Read the file in chunks of 4096 bytes
        byte[] buffer = new byte[4096];
        int bytesRead;
        // While there are still bytes to read
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        // Convert the output stream to a byte array
        byte[] bytes = outputStream.toByteArray();

        // Close the streams
        outputStream.close();
        inputStream.close();

        return bytes;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
