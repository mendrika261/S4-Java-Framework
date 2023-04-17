package etu2024.framework.servlet;

import etu2024.framework.Mapping;
import etu2024.framework.ModelView;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class FrontServlet extends HttpServlet {
    HashMap<String, Mapping> mappingUrls;

    @Override
    public void init() throws ServletException {
        super.init();
        setMappingUrls(Mapping.getAnnotatedUrlMethod(getInitParameter("PACKAGE_ROOT")));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String request_url = request.getRequestURL().toString().split(request.getContextPath())[1];

        Mapping mapping = getMappingUrls().get(request_url);
        if(mapping == null) {
            response.sendError(404, "FRAMEWORK ERROR - The method for the url " + request_url +
                    " is not found, make sure it's annotated with @Url");
            return;
        }
        try {
            ModelView modelView = (ModelView) Class.forName(mapping.getClassName()).getMethod(mapping.getMethod()).invoke(null);

            for(String key: modelView.getData().keySet())
                request.setAttribute(key, modelView.getData().get(key));

            request.getRequestDispatcher(modelView.getView()).forward(request, response);
        } catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (NullPointerException e) {
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
