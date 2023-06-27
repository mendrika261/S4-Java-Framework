package etu2024.framework.servlet;

import etu2024.framework.core.File;
import etu2024.framework.core.ModelView;
import etu2024.framework.utility.Mapping;
import etu2024.framework.utility.Tools;
import jakarta.servlet.*;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

@MultipartConfig
public class FrontServlet extends HttpServlet {
    HashMap<String, Mapping> mappingUrls;
    HashMap<Class<?>, Object> instances;

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
            if (request.getMethod().equals("POST")) {
                Enumeration<String> parameterNames = request.getParameterNames();
                while (parameterNames.hasMoreElements()) {
                    String paramName = parameterNames.nextElement();
                    System.out.println("Parameter Name: " + paramName);
                }
                System.out.println("Request1: " + request.getParameter("file"));
                Part part = request.getPart("file");
                System.out.println("Request2: " + request.getPart("file").toString());
            }

            // Get the class from the mapping
            Class<?> objectClass = Class.forName(mapping.getClassName());

            // 1. Create the object and set the parameters from the request to the object
            Object object = setAttributeToTheObject(objectClass, request);

            // 2. set the parameters corresponding to method parameters
            Method method = Tools.getMethodByName(objectClass, mapping.getMethod());
            List<Object> parameters = setMethodParameters(method, request);

            // 3. Call the method from the mapping
            ModelView modelView = (ModelView) method.invoke(object, parameters.toArray());

            // Set the attributes from the modelView to the request
            for (String key : modelView.getData().keySet())
                request.setAttribute(key, modelView.getData().get(key));

            // Forward the request to the view
            request.getRequestDispatcher(modelView.getView()).forward(request, response);

        /*} catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException | NoSuchMethodException |
                 InstantiationException e) {
            response.sendError(404, "FRAMEWORK ERROR - The controller function " + mapping.getMethod() + " in " +
                    mapping.getClassName() + " is not found");
        } catch (NullPointerException e) {
            // If the controller function doesn't return a ModelView, send a 500 error
            response.sendError(500, "FRAMEWORK ERROR - The controller function " + mapping.getMethod() + " in " +
                    mapping.getClassName() + " must return a ModelView");
        }*/
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
                if(request.getContentType() != null && request.getContentType().startsWith("multipart/"))
                    paramValue = request.getPart(parameter.getName());
                else
                    paramValue = null;
            } else if(parameter.getType().isArray()) {
                paramValue = request.getParameterValues(parameter.getName() + "[]");
            } else {
                paramValue = request.getParameter(parameter.getName());
            }
            if(paramValue == null)
                parameters.add(null);
            else
                parameters.add(Tools.cast(parameter.getType(), paramValue));
        }
        return parameters;
    }

    // For singleton classes
    public Object constructObject(Class<?> objectClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return objectClass.getDeclaredConstructor().newInstance();
    }

    // Set all attributes of the mapped class from the request
    public Object setAttributeToTheObject(Class<?> objectClass, HttpServletRequest request) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException, ServletException {
        Object object = constructObject(objectClass);
        /* for(Method method: objectClass.getDeclaredMethods()) {
            // Test if the method is a setter
            if (method.getName().startsWith("set")) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                // Test if the setter has one parameter
                if (parameterTypes.length == 1) {
                    // Get the type of the parameter
                    Class<?> parameterType = parameterTypes[0];
                    Object attributeValue;
                    if(parameterType.isArray())
                        attributeValue = request.getParameterValues(method.getName().substring(3)
                                .toLowerCase() + "[]");
                    else
                        attributeValue = request.getParameter(method.getName().substring(3).toLowerCase());
                    // Test if attribute value
                    if(attributeValue == null)
                        continue;
                    // Cast the parameter to the right type
                    Object param = Tools.cast(parameterType, attributeValue);
                    // Call the setter
                    method.invoke(object, param);
                }
            }
        }*/
        for(Field field: objectClass.getDeclaredFields()) {
            Class<?> fieldType = field.getType();
            Object attributeValue;
            if(fieldType == File.class) {
                if(request.getContentType() != null && request.getContentType().startsWith("multipart/"))
                    attributeValue = request.getPart(field.getName());
                else
                    attributeValue = null;
            } else if(fieldType.isArray())
                attributeValue = request.getParameterValues(field.getName().toLowerCase() + "[]");
            else
                attributeValue = request.getParameter(field.getName().toLowerCase());
            if(attributeValue == null)
                continue;
            Object param = Tools.cast(fieldType, attributeValue);
            field.setAccessible(true);
            field.set(object, param);
        }
        return object;
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
