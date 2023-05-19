package etu2024.framework.servlet;

import etu2024.framework.Mapping;
import etu2024.framework.ModelView;
import etu2024.framework.Tools;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class FrontServlet extends HttpServlet {
    HashMap<String, Mapping> mappingUrls;

    @Override
    public void init() throws ServletException {
        super.init();
        // Get the mapping urls from the package root set in the web.xml
        setMappingUrls(Mapping.getAnnotatedUrlMethod(getInitParameter("PACKAGE_ROOT")));
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
        response.getWriter().println(getMappingUrls().size());

        // Get the mapping from the url
        Mapping mapping = getMappingUrls().get(request_url);

        // If the mapping is null, send a 404 error
        if(mapping == null) {
            response.sendError(404, "FRAMEWORK ERROR - The method for the url " + request_url +
                    " is not found, make sure it's annotated with @Url");
            return;
        }

        // If the mapping is not null, 1. get the class and set the parameters corresponding to each setter
        try {
            // Get the class from the mapping
            Class<?> objectClass =  Class.forName(mapping.getClassName());
            Object object = objectClass.getDeclaredConstructor().newInstance();

            // Set the parameters from the request to the object
            for(Method method: objectClass.getDeclaredMethods()) {
                String attributeName = request.getParameter(method.getName().substring(3).toLowerCase());
                // Test if the method is a setter
                if (method.getName().startsWith("set") && attributeName != null) {
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    // Test if the setter has one parameter
                    if (parameterTypes.length == 1) {
                        // Get the type of the parameter
                        Class<?> parameterType = parameterTypes[0];
                        // Cast the parameter to the right type
                        Object param = Tools.cast(parameterType, attributeName);
                        // Call the setter
                        method.invoke(object, param);
                    }
                }
            }

            // 2. set the parameters corresponding to method parameters
            Method method = Tools.getMethodByName(objectClass, mapping.getMethod());
            List<Object> parameters = new ArrayList<>();
            for(Parameter parameter : method.getParameters()) {
                String attributeName = request.getParameter(parameter.getName());
                parameters.add(Tools.cast(parameter.getType(), attributeName));
            }

            // 3. Call the method from the mapping
            ModelView modelView = (ModelView) method.invoke(object, parameters.toArray());

            // Set the attributes from the modelView to the request
            for(String key: modelView.getData().keySet())
                request.setAttribute(key, modelView.getData().get(key));

            // Forward the request to the view
            request.getRequestDispatcher(modelView.getView()).forward(request, response);

        } catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException | NoSuchMethodException |
                 InstantiationException e) {
            response.sendError(404, "FRAMEWORK ERROR - The controller function " + mapping.getMethod() + " in " +
                    mapping.getClassName() + " is not found");
        } catch (NullPointerException e) {
            // If the controller function doesn't return a ModelView, send a 500 error
            response.sendError(500, "FRAMEWORK ERROR - The controller function " + mapping.getMethod() + " in " +
                    mapping.getClassName() + " must return a ModelView");
        }
    }

    // Getters and setters
    public HashMap<String, Mapping> getMappingUrls() {
        return mappingUrls;
    }

    public void setMappingUrls(HashMap<String, Mapping> mappingUrls) {
        this.mappingUrls = mappingUrls;
    }
}
