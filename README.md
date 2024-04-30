<div align="center">

<h3> Weby Framework ☕️ </h3>

A simple Java Framework for Web Applications 👨‍💻

<img src="https://badgen.net/badge/Status/Dev/red?icon=github" alt="Dev">
<img src="https://badgen.net/github/releases/micromatch/micromatch?icon=github" alt="Dev">

</div>

## Features 🐣
1. [x] Singleton class
2. [x] Session
3. [x] Authentification/Authorization with many profiles
4. [x] Return Json (via modelView)
5. [x] Rest API (direct returning object)
6. [x] File upload, input array
7. [x] `app.xml` external configuration file
8. [x] `framework.sh` manager: build, launch and hot reload
9. [x] 🎉 Database integration: see 👉 [Unidao here](https://github.com/mendrika261/S5-UniDao)
10. [x] 🎉 Mini Scaffolding integration: generate CRUD 👉 [See here](https://github.com/mendrika261/S5-Scaffolding)
11. [ ] Error page (404, 500...)
12. [ ] Log system
13. [ ] Full Documentation <br>
    ... and more

## Requirements 📋
| Requirements    | Version           |
|-----------------|-------------------|
| JDK ☕️          | equal or upper 17 |
| Tomcat 🐱       | equal or upper 10 |
| Gson library 📚 | 2.10 (provided ✅) |
| OS 💻           | can run bash script     |

## Installation 🚀
There is 3 versions, get the last released from the [releases page](https://github.com/mendrika261/S4-Java-Framework/releases/tag/v0.1)
- Production version: contains the framework with `framework.sh` manager
- Manual version: contains only `framework.jar`. See manual installation [below](#manual-installation)
- Demo version: production version with a demo project

### First configuration (production version)
- Open `conf.env` file and set all: jdk, tomcat, information about your project like where the project will be created<br>
If it is not in your file get it from [here](https://github.com/mendrika261/S4-Java-Framework/blob/v0.1/conf.env) and change
- Then, give permissions for `framework.sh` the script that will help you to manage the framework
```bash
sudo chmod +x framework.sh
```
- Finally, `init` your repository for a weby project
```bash
./framework.sh --init
```

### Running
- To run your project you can use the script `framework.sh`, make `framework.sh --help` to see all options
```bash
./framework.sh --run
```
> The script will compile your project and run it in tomcat with `auto reload` feature enabled,
for `*.xml` or any configurations changed you might sometimes need to restart tomcat ⚠️

### Manual installation
After downloading the jar file, you can add it to your lib project and use it as a dependency (make Gson in your dependency too).
#### Configure app.xml
Create a file `app.xml` in your project and copy the basic configuration below.
```xml
<?xml version="1.0" encoding="utf-8" ?>

<app>
    <config id="auth">
        <session name="profile" />
        <profiles>
            <profile name="AUTH_PROFILE_ADMIN" value="admin" />
            <profile name="AUTH_PROFILE_USER" value="user" />
        </profiles>
        <redirections>
            <redirect name="AUTH_REDIRECT_LOGIN" value="/" />
            <redirect name="AUTH_REDIRECT_LOGOUT" value="/login" />
        </redirections>
    </config>
</app>
```
#### Configure web.xml by adding the servlet to handle all requests
> You must set PACKAGE_ROOT and CONFIG_FILE parameters
```xml
...
<!-- This is the main servlet of the framework -->
<servlet>
    <servlet-name>FrontServlet</servlet-name>
    <servlet-class>etu2024.framework.servlet.FrontServlet</servlet-class>
    <init-param>
        <!-- The root package of your project -->
        <param-name>PACKAGE_ROOT</param-name>
        <param-value>.../popo/src/main/java/</param-value>
    </init-param>
    <init-param>
        <!-- The path to the configuration file -->
        <param-name>CONFIG_FILE</param-name>
        <param-value>.../popo/src/app.xml</param-value>
    </init-param>
</servlet>
        
<!-- All the requests are processed by FrontServlet -->
<servlet-mapping>
    <servlet-name>FrontServlet</servlet-name>
    <url-pattern>/</url-pattern>
</servlet-mapping>
...
```

## Usage 🧑‍🍳
> Let's learn by examples, it is the best (and fastest) way to learn. ℹ️

### Overview
#### This is a basic controller
```java
import etu2024.framework.annotation.Auth;
import etu2024.framework.core.ModelView;
...
@Url(url = "/") // The url where the controller will be called
public ModelView index() {
    ModelView modelView = new ModelView(); // A class to manage the view

    modelView.setView("home.jsp"); // The page to render
    return modelView;
}
...
```
#### Example of rest controller using [Unidao](https://github.com/mendrika261/S5-UniDao)
```java
@Url(url="/regions", method=Mapping.POST)
@RestAPI
public Region save(@JsonObject Region region) throws DaoException {
    Service service = database.connect();
	Region region = region.save(service);
	service.endConnection();
    return region;
}
```
#### Send data to view
```java
...
// The class where all process will be done
ModelView modelView = new ModelView();
// Add a variable to the view
modelView.addItem("name", "Weby");
// The page to render
modelView.setView("home.jsp");
...
```
#### Get data from view
Attributes and parameters are filled automatically
```java
public class Customer()
    private String name;
    ...
    public ModelView getCustomer(String id) {
        ModelView modelView = new ModelView();
        
        // Access directly to the variable name and id
        
        modelView.setView("home.jsp");
        return modelView;
    }
    ...
```
#### Redirection
```java
...
ModelView modelView = new ModelView();
return modelView.redirect("/home");
...
```

#### See full documentation [Working]() 📖
