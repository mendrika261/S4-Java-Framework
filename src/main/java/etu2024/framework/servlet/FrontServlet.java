package etu2024.framework.servlet;

import etu2024.framework.Mapping;
import etu2024.framework.Url;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.HashMap;

public class FrontServlet extends HttpServlet {
    HashMap<String, Mapping> mappingUrls;

    @Override
    public void init() throws ServletException {
        super.init();
        setMappingUrls(Mapping.getAnnotatedUrlMethod(Mapping.BASE_SOURCE));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String request_url = request.getRequestURL().toString().split(request.getContextPath())[1];
        response.getWriter().println(request_url);

        for (String key : getMappingUrls().keySet()) {
            response.getWriter().println("\n" + key + " :"
                    + "\n\tClass: " + getMappingUrls().get(key).getClassName()
                    + "\n\tMethod: " + getMappingUrls().get(key).getMethod());
        }
    }

    @Url(url = "/test")
    public void test(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.getWriter().println("test");
    }

    // Getters and setters
    public HashMap<String, Mapping> getMappingUrls() {
        return mappingUrls;
    }

    public void setMappingUrls(HashMap<String, Mapping> mappingUrls) {
        this.mappingUrls = mappingUrls;
    }
}