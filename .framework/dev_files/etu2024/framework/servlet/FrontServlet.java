package etu2024.framework.servlet;

import com.google.gson.Gson;
import etu2024.framework.annotation.Auth;
import etu2024.framework.annotation.RestAPI;
import etu2024.framework.annotation.Session;
import etu2024.framework.annotation.Singleton;
import etu2024.framework.core.File;
import etu2024.framework.core.ModelView;
import etu2024.framework.utility.Conf;
import etu2024.framework.utility.User;
import etu2024.framework.utility.Mapping;
import etu2024.framework.utility.Tools;
import jakarta.servlet.*;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.*;
import org.eclipse.jdt.internal.compiler.codegen.ObjectCache;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.http.HttpRequest;
import java.util.*;

@MultipartConfig
public class FrontServlet extends HttpServlet {
    HashMap<String, Mapping> mappingUrls; // The mapping urls
    HashMap<Class<?>, Object> instances; // The instances of the classes for singleton

    @Override
    public void init() throws ServletException {
        super.init();
        // Get the mapping urls from the package root set in the web.xml
        setMappingUrls(Mapping.getAnnotatedUrlMethod(getInitParameter("PACKAGE_ROOT")));
        setInstances(new HashMap<>());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Send the request to the processRequest method
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Send the request to the processRequest method
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // Get the url from the request
        String request_url = request.getRequestURL().toString().split(request.getContextPath())[1];

        // Get the mapping from the url
        Mapping mapping = getMappingUrls().get(request_url);

        // If the mapping is null, send a 404 error
        if (mapping == null) {
            response.sendError(404, "FRAMEWORK ERROR - The method for the url " + request_url +
                    " is not found, make sure it's annotated with @Url");
            return;
        }

        try {
            // Get the class from the mapping
            Class<?> objectClass = Class.forName(mapping.getClassName());

            // 1. Create the object and set the parameters from the request to the object
            Object object = setAttributeToTheObject(objectClass, request);

            // 2. set the parameters corresponding to method parameters
            Method method = Tools.getMethodByName(objectClass, mapping.getMethod());
            List<Object> parameters = setMethodParameters(method, request);


            // @RestAPI function directly return JSON
            if(method.isAnnotationPresent(RestAPI.class)) {
                printJson(method.invoke(object, parameters.toArray()), response);
                return;
            }

            // Check if the method return a ModelView object
            Object functionReturn = method.invoke(object, parameters.toArray());
            if(functionReturn == null || functionReturn.getClass() != ModelView.class) {
                response.sendError(500, "FRAMEWORK ERROR - The method \"" + method.getName() + "\" in the class \"" +
                        objectClass.getName() + "\" make errors, make sure it return a ModelView object or use @RestAPI" +
                        " to return all types");
                return;
            }

            // Get the modelView object
            ModelView modelView = (ModelView) functionReturn;


            // Handle session
            HttpSession session = request.getSession();

            // Set the session attributes from modelView to the request
            setSessionsToTheRequest(modelView, session);

            // Set the session attributes from request to controller if the method or the class is annotated with @Session
            if(method.isAnnotationPresent(Session.class) || objectClass.isAnnotationPresent(Session.class)) {
                setSessionsFromRequest(object, request, response);
            }

            // Re-get the modelView with the new session attributes
            modelView = (ModelView) method.invoke(object, parameters.toArray());


            // Check if client authorized to call the method (if the method is annotated with @Auth)
            Object profile = session.getAttribute(Conf.getAuthSessionName());
            if(!User.isAuthorized(method, profile)) {
                response.sendRedirect(getServletContext().getContextPath()+Conf.getAuthRedirections().get("AUTH_REDIRECT_LOGOUT"));
                return;
            }

            // Set the attributes from the modelView to the request
            for (String key : modelView.getData().keySet())
                request.setAttribute(key, modelView.getData().get(key));

            // Return JSON if isJson is true in the modelView
            if(modelView.isJson()) {
                printJson(modelView.getData(), response);
                return;
            }

            // Forward the request if view is set in the modelView
            if (modelView.getView() != null) {
                if (modelView.isRedirect())
                    response.sendRedirect(getServletContext().getContextPath()+modelView.getView());
                else
                    request.getRequestDispatcher(modelView.getView()).forward(request, response);
            }

        } catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException | NoSuchMethodException |
                 InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    // Set mapped function parameters from the request
    public List<Object> setMethodParameters(Method method, HttpServletRequest request) throws ServletException, IOException {
        List<Object> parameters = new ArrayList<>();
        for(Parameter parameter : method.getParameters()) {
            Object paramValue;
            if(parameter.getType() == File.class) {
                // If the request is multipart, get the file from the request
                if(request.getContentType() != null && request.getContentType().startsWith("multipart/"))
                    paramValue = request.getPart(parameter.getName());
                else
                    paramValue = null;
            } else if(parameter.getType().isArray()) {
                paramValue = request.getParameterValues(parameter.getName() + "[]");
            } else {
                paramValue = request.getParameter(parameter.getName());
            }
            if(paramValue == null) // If the parameter is not found in the request, add null
                parameters.add(null);
            else
                parameters.add(Tools.cast(parameter.getType(), paramValue));
        }
        return parameters;
    }

    // Set all attributes of the mapped class from the request
    public Object setAttributeToTheObject(Class<?> objectClass, HttpServletRequest request) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException, ServletException {
        Object object = constructObject(objectClass);
        for(Field field: objectClass.getDeclaredFields()) {
            Class<?> fieldType = field.getType();
            Object attributeValue;
            if(fieldType == File.class) {
                // If the request is multipart, get the file from the request
                if(request.getContentType() != null && request.getContentType().startsWith("multipart/"))
                    attributeValue = request.getPart(field.getName());
                else
                    attributeValue = null;
            } else if(fieldType.isArray())
                attributeValue = request.getParameterValues(field.getName().toLowerCase() + "[]");
            else
                attributeValue = request.getParameter(field.getName().toLowerCase());
            if(attributeValue == null) // If there is not a parameter with the same name as the field
                continue;
            Object param = Tools.cast(fieldType, attributeValue);
            field.setAccessible(true);
            field.set(object, param);
        }
        return object;
    }

    // For singleton classes
    public Object constructObject(Class<?> objectClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if(objectClass.isAnnotationPresent(Singleton.class)) { // If the class is annotated with @Singleton
            if(getInstances().containsKey(objectClass)) { // If the class is already instantiated
                return getInstances().get(objectClass);
            } else {
                Object object = objectClass.getDeclaredConstructor().newInstance();
                getInstances().put(objectClass, object);
                return object;
            }
        }
        // If the class is not annotated with @Singleton create a new instance
        return objectClass.getDeclaredConstructor().newInstance();
    }

    // Set session attributes from the modelView to the session
    public void setSessionsToTheRequest(ModelView modelView, HttpSession session) {
        /* Removing session */
        // If invalidateSession is true, invalidate the session
        if(modelView.isInvalidateSession()) {
            // Iterate over the session attributes and remove them
            Enumeration<String> sessionAttributes = session.getAttributeNames();
            while(sessionAttributes.hasMoreElements())
                session.removeAttribute(sessionAttributes.nextElement());
        }
        // If the removeSession list is not empty
        if(!modelView.getRemoveSessions().isEmpty()) {
            for(String key : modelView.getRemoveSessions())
                session.removeAttribute(key);
        }

        /* Adding session */
        for (String key : modelView.getSession().keySet()) {
            session.setAttribute(key, modelView.getSession().get(key));
        }
    }

    // Set session from the request to the modelView
    public void setSessionsFromRequest(Object object, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Check if the session is new or invalidated
        HttpSession existingSession = request.getSession(false);
        if(existingSession == null || existingSession.isNew()) {
            return;
        }

        // Get the field where sessions attributes are stored
        Field field;
        try {
            field = object.getClass().getDeclaredField("sessions");
        } catch (NoSuchFieldException ex) {
            throw new RuntimeException("FRAMEWORK ERROR - You can't get session attributes if the class `"+ object.getClass().getName()
                    +"` does not have " +
                    "`Hashmap<String, Object> sessions` as a field");
        }
        // Set the field accessible
        field.setAccessible(true);

        // Iterate over the session attributes and add them to the HashMap
        HashMap<String, Object> sessions = new HashMap<>();
        for (Enumeration<String> e = request.getSession().getAttributeNames(); e.hasMoreElements(); ) {
            String key = e.nextElement();
            sessions.put(key, request.getSession().getAttribute(key));
        }

        try {
            field.set(object, sessions);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException("FRAMEWORK ERROR - Cannot have access to the field sessions, be sure that the " +
                    "field is public");
        }
    }



    public void printJson(Object object, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Gson gson = new Gson();
        gson.toJson(object, response.getWriter());
    }

    // Getters and setters
    public HashMap<String, Mapping> getMappingUrls() {
        return mappingUrls;
    }

    public void setMappingUrls(HashMap<String, Mapping> mappingUrls) {
        this.mappingUrls = mappingUrls;
    }

    public HashMap<Class<?>, Object> getInstances() {
        return instances;
    }

    public void setInstances(HashMap<Class<?>, Object> instances) {
        this.instances = instances;
    }
}
