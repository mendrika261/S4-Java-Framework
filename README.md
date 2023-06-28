<div align="center">

# Weby Framework ☕️
A simple Java Framework for Web Applications 👨‍💻

</div>

## Features 🐣
DONE
1. [x] Singleton class
2. [x] Session
3. [x] Auth with many profiles
4. [x] Return Json (via modelView)
5. [x] Rest API (direct returning object)
6. [x] File upload or input array
7. [x] `app.xml` external configuration file
8. [x] `framework.sh` manager and auto-reload

**TODO**
10. [ ] Database integration
11. [ ] Error reporting (404 error page...)
12. [ ] Log system
13. [ ] Security improvements
14. [ ] Full Documentation <br>
    ... and more

## Requirements 📋
| Requirements    | Version           |
|-----------------|-------------------|
| JDK ☕️          | equal or upper 17 |
| Tomcat 🐱       | equal or upper 10 |
| Gson library 📚 | 2.10 (provided ✅) |

## Installation 🚀
Get the last released version from the [releases page]() 
- Demo version contains examples of use and some test
- Production version contains only the framework

### First configuration
- Open `conf.env` file and set all: jdk, tomcat, information about your project like where the project will be created<br>
If it is not in your file get it from [here]() and change
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
#### Send data to the view
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
#### Get data from the view
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

#### See full documentation [here]() 📖
Report bugs [here]() 🐛 and contribute [here]() 🤝

