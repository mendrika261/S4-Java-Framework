package etu2024.framework.servlet;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(asyncSupported = true, urlPatterns = {"/*"})
public class CORSInterceptor implements Filter {

  @Override
  public void doFilter(ServletRequest servletRequest,
                       ServletResponse servletResponse, FilterChain filterChain)
      throws IOException, ServletException {
    HttpServletResponse httpResponse = ((HttpServletResponse)servletResponse);

    // Cors Parameters
    httpResponse.setHeader("Access-Control-Allow-Origin", "*");
    httpResponse.setHeader("Access-Control-Allow-Methods",
                           "POST, GET, PUT, DELETE, OPTIONS");
    httpResponse.setHeader(
        "Access-Control-Allow-Headers",
        "Content-Type, Access-Control-Allow-Headers,"
            +
            "Authorization, Origin, Access-Control-Request-Method, ngrok-skip-browser-warning");

    filterChain.doFilter(servletRequest, servletResponse);
  }
}
