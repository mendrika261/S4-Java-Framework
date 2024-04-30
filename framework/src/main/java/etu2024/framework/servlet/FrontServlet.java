package etu2024.framework.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import etu2024.framework.annotation.*;
import etu2024.framework.core.File;
import etu2024.framework.core.ModelView;
import etu2024.framework.utility.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.Object;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@MultipartConfig
public class FrontServlet extends HttpServlet {
  HashMap<MappingUrl, Mapping> mappingUrls; // The mapping urls
  HashMap<Class<?>, Object>
      instances; // The instances of the classes for singleton

  @Override
  public void init() throws ServletException {
    super.init();
    // Get the mapping urls from the package root set in the web.xml
    setMappingUrls(
        Mapping.getAnnotatedUrlMethod(getInitParameter("PACKAGE_ROOT")));
    setInstances(new HashMap<>());
    Conf.CONFIG_FILE = getInitParameter("CONFIG_FILE");
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // Send the request to the processRequest method
    processRequest(request, response, Mapping.GET);
  }

  @Override
  protected void doPost(HttpServletRequest request,
                        HttpServletResponse response)
      throws ServletException, IOException {
    // Send the request to the processRequest method
    processRequest(request, response, Mapping.POST);
  }

  @Override
  protected void doPut(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // Send the request to the processRequest method
    processRequest(request, response, Mapping.PUT);
  }

  @Override
  protected void doDelete(HttpServletRequest request,
                          HttpServletResponse response)
      throws ServletException, IOException {
    // Send the request to the processRequest method
    processRequest(request, response, Mapping.DELETE);
  }

  private void processRequest(HttpServletRequest request,
                              HttpServletResponse response,
                              String requestMethod)
      throws IOException, ServletException {
    // Get the url from the request
    String request_url =
        request.getRequestURL().toString().split(request.getContextPath())[1];

    if (request_url.contains(".")) {
      // send to default servlet
      getServletContext().getNamedDispatcher("default").forward(request,
                                                                response);
      return;
    }

    // Get the mapping from the url
    Mapping mapping = getMappingUrl(request_url, requestMethod);

    // If the mapping is null, send a 404 error
    if (mapping == null) {
      response.sendError(
          404, "FRAMEWORK ERROR - The method for the url " + request_url +
                   " is not found, make sure it's annotated with @Url");
      return;
    }

    try {
      // Get the class from the mapping
      Class<?> objectClass = Class.forName(mapping.getClassName());

      // 1. Create the object and set the parameters from the request to the
      // object
      Object object = setAttributeToTheObject(objectClass, request);

      // 2. set the parameters corresponding to method parameters
      Method method = Tools.getMethodByName(objectClass, mapping.getMethod());
      List<Object> parameters = setMethodParameters(method, mapping, request);

      HttpSession session = request.getSession();
      Object profile = session.getAttribute(Conf.getAuthSessionName());

      // Set the session attributes from request to project.controller if the
      // method or the class is annotated with @Session
      if (method.isAnnotationPresent(Session.class) ||
          objectClass.isAnnotationPresent(Session.class)) {
        setSessionsFromRequest(object, request, response);
      }

      // @RestAPI function directly return JSON
      if (method.isAnnotationPresent(RestAPI.class)) {
        // Check if client authorized to call the method (if the method is
        // annotated with @Auth)
        if (!User.isAuthorized(method, profile)) {
          printJson("Not allowed to access this page", response);
        } else {
          // Set the session attributes from request to project.controller if
          // the method or the class is annotated with @Session
          if (method.isAnnotationPresent(Session.class) ||
              objectClass.isAnnotationPresent(Session.class))
            setSessionsFromRequest(object, request, response);
          printJson(method.invoke(object, parameters.toArray()), response);
        }
        return;
      }

      if (method.isAnnotationPresent(Xml.class)) {
        // Check if client authorized to call the method (if the method is
        // annotated with @Auth)
        if (!User.isAuthorized(method, profile)) {
          printXml("Not allowed to access this page", response);
        } else {
          // Set the session attributes from request to project.controller if
          // the method or the class is annotated with @Session
          if (method.isAnnotationPresent(Session.class) ||
              objectClass.isAnnotationPresent(Session.class))
            setSessionsFromRequest(object, request, response);
          printXml(method.invoke(object, parameters.toArray()), response);
        }
        return;
      }

      // Check if the method return a ModelView object
      Object functionReturn = method.invoke(object, parameters.toArray());
      if (functionReturn == null ||
          functionReturn.getClass() != ModelView.class) {
        response.sendError(
            500,
            "FRAMEWORK ERROR - The method \"" + method.getName() +
                "\" in the class \"" + objectClass.getName() +
                "\" make errors, make sure it return a ModelView object or use @RestAPI"
                + " to return all types");
        return;
      }

      // Get the modelView object
      ModelView modelView = (ModelView)functionReturn;

      // Handle session

      // Set the session attributes from modelView to the request
      setSessionsToTheRequest(modelView, session);

      // Set the session attributes from request to project.controller if the
      // method or the class is annotated with @Session
      if (method.isAnnotationPresent(Session.class) ||
          objectClass.isAnnotationPresent(Session.class)) {
        setSessionsFromRequest(object, request, response);
      }

      // Set the attributes from the modelView to the request
      for (String key : modelView.getData().keySet())
        request.setAttribute(key, modelView.getData().get(key));

      // Re-get the modelView with the new session attributes
      // ModelView modelView2 = (ModelView) method.invoke(object,
      // params.toArray());

      // Check if client authorized to call the method (considering change in
      // the method)
      if (!User.isAuthorized(method, profile)) {
        if (modelView.isJson())
          printJson("Not allowed to access this page", response);
        else
          response.sendRedirect(
              request.getContextPath() +
              Conf.getAuthRedirections().get("AUTH_REDIRECT_LOGOUT"));
        return;
      }

      // Return JSON if isJson is true in the modelView
      if (modelView.isJson()) {
        printJson(modelView.getData(), response);
        return;
      }

      // Forward the request if view is set in the modelView
      if (modelView.getView() != null) {
        if (modelView.isRedirect()) {
          response.sendRedirect(request.getContextPath() + modelView.getView());
        } else
          request.getRequestDispatcher(modelView.getView())
              .forward(request, response);
      }

    } catch (ClassNotFoundException | InvocationTargetException |
             IllegalAccessException | NoSuchMethodException |
             InstantiationException e) {
      throw new RuntimeException(e);
    }
  }

  // Set mapped function parameters from the request
  public List<Object> setMethodParameters(Method method, Mapping mapping,
                                          HttpServletRequest request)
      throws ServletException, IOException {
    List<Object> parameters = new ArrayList<>();
    for (Parameter parameter : method.getParameters()) {
      Object paramValue = null;
      if (request != null) {
        if (parameter.isAnnotationPresent(FormObject.class)) {
          Class<?> parameterClass = parameter.getType();
          try {
            parameters.add(setAttributeToTheObject(parameterClass, request));
          } catch (Exception ignored) {
            parameters.add(null);
          }
          continue;
        } else if (parameter.isAnnotationPresent(JsonObject.class)) {
          Class<?> parameterClass = parameter.getType();
          try {
            Gson gson = new Gson();
            BufferedReader reader = request.getReader();
            parameters.add(gson.fromJson(reader, parameterClass));
          } catch (Exception ignored) {
            parameters.add(null);
          }
          continue;
        } else if (parameter.getType() == File.class) {
          // If the request is multipart, get the file from the request
          if (request.getContentType() != null &&
              request.getContentType().startsWith("multipart/"))
            paramValue = request.getPart(parameter.getName());
          else
            paramValue = null;
        } else if (parameter.getType().isArray()) {
          paramValue = request.getParameterValues(parameter.getName() + "[]");
        } else {
          if (mapping.getParams().containsKey(parameter.getName()))
            paramValue = mapping.getParams().get(parameter.getName());
          else
            paramValue = request.getParameter(parameter.getName());
        }
      }
      if (paramValue ==
          null) // If the parameter is not found in the request, add null
        parameters.add(null);
      else
        parameters.add(Tools.cast(parameter.getType(), paramValue));
    }
    System.out.println("PARAMETERS");
    for (int i = 0; i < parameters.size(); i++) {
      System.out.println(parameters.get(i));
    }
    return parameters;
  }

  // Set all attributes of the mapped class from the request
  public Object setAttributeToTheObject(Class<?> objectClass,
                                        HttpServletRequest request)
      throws NoSuchMethodException, InvocationTargetException,
             InstantiationException, IllegalAccessException, IOException,
             ServletException {
    Object object = constructObject(objectClass);
    for (Field field : objectClass.getDeclaredFields()) {
      Class<?> fieldType = field.getType();
      Object attributeValue;
      if (fieldType == File.class) {
        // If the request is multipart, get the file from the request
        if (request.getContentType() != null &&
            request.getContentType().startsWith("multipart/"))
          attributeValue = request.getPart(field.getName());
        else
          attributeValue = null;
      } else if (fieldType.isArray())
        attributeValue = request.getParameterValues(field.getName() + "[]");
      else
        attributeValue = request.getParameter(field.getName());
      if (attributeValue ==
          null) // If there is not a parameter with the same name as the field
        continue;
      Object param = Tools.cast(fieldType, attributeValue);
      field.setAccessible(true);
      field.set(object, param);
    }
    return object;
  }

  // For singleton classes
  public Object constructObject(Class<?> objectClass)
      throws NoSuchMethodException, InvocationTargetException,
             InstantiationException, IllegalAccessException {
    if (objectClass.isAnnotationPresent(
            Singleton.class)) { // If the class is annotated with @Singleton
      if (getInstances().containsKey(
              objectClass)) { // If the class is already instantiated
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
  public void setSessionsToTheRequest(ModelView modelView,
                                      HttpSession session) {
    /* Removing session */
    // If invalidateSession is true, invalidate the session
    if (modelView.isInvalidateSession()) {
      // Iterate over the session attributes and remove them
      Enumeration<String> sessionAttributes = session.getAttributeNames();
      while (sessionAttributes.hasMoreElements())
        session.removeAttribute(sessionAttributes.nextElement());
    }
    // If the removeSession list is not empty
    if (!modelView.getRemoveSessions().isEmpty()) {
      for (String key : modelView.getRemoveSessions())
        session.removeAttribute(key);
    }

    /* Adding session */
    for (String key : modelView.getSession().keySet()) {
      session.setAttribute(key, modelView.getSession().get(key));
    }
  }

  // Set session from the request to the modelView
  public void setSessionsFromRequest(Object object, HttpServletRequest request,
                                     HttpServletResponse response)
      throws IOException {
    // Check if the session is new or invalidated
    HttpSession existingSession = request.getSession(false);
    if (existingSession == null || existingSession.isNew()) {
      return;
    }

    // Get the field where sessions attributes are stored
    Field field;
    try {
      field = object.getClass().getDeclaredField("sessions");
    } catch (NoSuchFieldException ex) {
      throw new RuntimeException(
          "FRAMEWORK ERROR - You can't get session attributes if the class `" +
          object.getClass().getName() + "` does not have "
          + "`Hashmap<String, Object> sessions` as a field");
    }
    // Set the field accessible
    field.setAccessible(true);

    // Iterate over the session attributes and add them to the HashMap
    HashMap<String, Object> sessions = new HashMap<>();
    for (Enumeration<String> e = request.getSession().getAttributeNames();
         e.hasMoreElements();) {
      String key = e.nextElement();
      sessions.put(key, request.getSession().getAttribute(key));
    }

    try {
      field.set(object, sessions);
    } catch (IllegalAccessException ex) {
      throw new RuntimeException(
          "FRAMEWORK ERROR - Cannot have access to the field sessions, be sure that the "
          + "field is public");
    }
  }

  public void printJson(Object object, HttpServletResponse response)
      throws IOException {
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    Gson gson =
        new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class,
                                 new LocalDateTimeAdapter())
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
            .create();
    gson.toJson(object, response.getWriter());
  }

  public String printXml(Object object, HttpServletResponse response)
      throws IOException {
    StringBuilder content = new StringBuilder();
    if (object.getClass().isArray()) {
      for (Object obj : (Object[])object) {
        content.append(printXml(obj, response));
      }
    } else {
      StringBuilder part = new StringBuilder();
      part.append("<").append(object.getClass().getSimpleName()).append(">\n");
      for (Field field : object.getClass().getDeclaredFields()) {
        field.setAccessible(true);
        try {
          part.append("<").append(field.getName()).append(">");
          part.append(field.get(object));
          part.append("</").append(field.getName()).append(">");
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
      }
      part.append("</").append(object.getClass().getSimpleName()).append(">\n");
      return part.toString();
    }

    response.setContentType("application/xml");

    response.setCharacterEncoding("UTF-8");
    response.getWriter().println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    response.getWriter().println("<xml>");
    response.getWriter().println(content);
    response.getWriter().println("</xml>");
    return content.toString();
  }

  private Mapping getMappingUrl(String url, String requestMethod) {
    for (MappingUrl key : getMappingUrls().keySet()) {
      Pattern pattern = Pattern.compile(
          "^" + key.getUrl().replaceAll("\\{[a-zA-Z0-9]+}", "([a-zA-Z0-9]+)") +
          "$");
      Matcher matcher = pattern.matcher(url);
      if (matcher.find() && key.getMethod().equalsIgnoreCase(requestMethod)) {
        System.out.println("MATCHED: " + key.getUrl() + " - " +
                           key.getMethod());
        Mapping mapping = getMappingUrls().get(key);
        for (int i = 0; i < mapping.getParams().size(); i++) {
          mapping.getParams().put(
              mapping.getParams().keySet().toArray()[i].toString(),
              matcher.group(i + 1));
        }
        return mapping;
      }
    }
    return null;
  }

  // Getters and setters
  public HashMap<MappingUrl, Mapping> getMappingUrls() { return mappingUrls; }

  public void setMappingUrls(HashMap<MappingUrl, Mapping> mappingUrls) {
    this.mappingUrls = mappingUrls;
  }

  public HashMap<Class<?>, Object> getInstances() { return instances; }

  public void setInstances(HashMap<Class<?>, Object> instances) {
    this.instances = instances;
  }
}
