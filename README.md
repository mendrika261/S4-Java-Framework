# Get Started
- Copy the `framework.jar` to your libs folder (WEB-INF/lib)
- Add following code to your `web.xml` and replace the `param-value` with your java project path
> DON'T FORGET TO PUT '/' AT THE END OF THE PATH
```
    <servlet>
        <servlet-name>FrontServlet</servlet-name>
        <servlet-class>etu2024.framework.servlet.FrontServlet</servlet-class>
        <init-param>
            <param-name>PACKAGE_ROOT</param-name>
            <param-value>-/IdeaProjects/S4-Java-Framework/test-framework/src/main/java/</param-value>
        </init-param>
    </servlet>

    <servlet-mapping>
        <servlet-name>FrontServlet</servlet-name>
        <url-pattern/>
        <url-pattern>/</url-pattern>
    </servlet-mapping>

    <servlet-mapping>
        <servlet-name>default</servlet-name>
        <url-pattern>*.html</url-pattern>
        <url-pattern>*.css</url-pattern>
    </servlet-mapping>
```
## Your project minimal structure
```
|-java
|---(Package and java source here)

|-webapp
|---WEB-INF
|------lib
|---------framework.jar
|------web.xml
```
## Modify the launch.sh path
- Open the `launch.sh` and modify the `project_path` and the tomcat webapps `tomcat_webapps`


# Usage

## Controller
- A controller should annotate the `@etu2024.framework.annotation.Url`
- And must return a `etu2024.framework.core.ModelView;` that requires a view (can be .jsp, .html...)
- If a data is get from the view it will assign in the class attribute if exists
- Alternative, the controller method can also take arguments with needed to pass data
- Eg:
```
    public class Emp {
        String name;
        Date creation;
    
        @Url(url = "/")
        public ModelView get_all_emp() {
            ModelView modelView = new ModelView("test.jsp");
            /* Access the data from view using the getter */
            modelView.addItem("name", getName());
            return modelView;
        }
        
        /* Controller with parameter */
        @Url(url = "/find")
        public ModelView get(int id) {
            ModelView modelView = new ModelView("test.jsp");
            modelView.addItem("id", id);
            return modelView;
        }
        ...
```
- Then access on the browser with `url`(String) given in the annotation

## Limitation
- Type date must be from `java.sql.Date`