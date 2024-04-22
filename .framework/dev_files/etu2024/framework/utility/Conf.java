package etu2024.framework.utility;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.HashMap;

// Interact with configuration in app.xml
public class Conf {
    public static String CONFIG_FILE = "app.xml";

    // Get the configuration file
    static Document getDocument() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(CONFIG_FILE);
        } catch (Exception e) {
            throw new RuntimeException("Error while parsing app.xml config, verify path or syntax");
        }
    }

    public static String getAuthSessionName() {
        try {
            Element sessionElement = (Element) getDocument().getElementsByTagName("session").item(0);
            return sessionElement.getAttribute("name");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Session name for auth not found in app.xml");
        }
    }

    public static HashMap<String, String> getAuthProfiles() {
        try {
            Element profilesElement = (Element) getDocument().getElementsByTagName("profiles").item(0);
            NodeList profileNodes = profilesElement.getElementsByTagName("profile");

            HashMap<String, String> profilesMap = new HashMap<>();
            for (int i = 0; i < profileNodes.getLength(); i++) {
                Element profileElement = (Element) profileNodes.item(i);
                String profileName = profileElement.getAttribute("name");
                String profileValue = profileElement.getAttribute("value");
                profilesMap.put(profileName, profileValue);
            }
            return profilesMap;
        } catch (Exception e) {
            throw new RuntimeException("Profiles for auth not found in app.xml");
        }
    }

    public static HashMap<String, String> getAuthRedirections() {
        try {
            Element redirectionsElement = (Element) getDocument().getElementsByTagName("redirections").item(0);
            NodeList redirectionNodes = redirectionsElement.getElementsByTagName("redirect");

            HashMap<String, String> redirectionsMap = new HashMap<>();
            for (int i = 0; i < redirectionNodes.getLength(); i++) {
                Element redirectionElement = (Element) redirectionNodes.item(i);
                String redirectionName = redirectionElement.getAttribute("name");
                String redirectionValue = redirectionElement.getAttribute("value");
                redirectionsMap.put(redirectionName, redirectionValue);
            }
            return redirectionsMap;
        } catch (Exception e) {
            throw new RuntimeException("Redirections for auth not found in app.xml");
        }
    }
}
