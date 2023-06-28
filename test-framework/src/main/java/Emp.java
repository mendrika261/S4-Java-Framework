import etu2024.framework.annotation.Auth;
import etu2024.framework.annotation.RestAPI;
import etu2024.framework.annotation.Session;
import etu2024.framework.core.File;
import etu2024.framework.core.ModelView;
import etu2024.framework.annotation.Url;

import java.sql.Date;

public class Emp {
    String name;
    Date creation;
    File file;

    @Url(url = "/")
    @Auth(profiles = {"admin", "user"})
    public ModelView get_all_emp() {
        ModelView modelView = new ModelView("test.jsp");
        if(getFile() != null)
            if(getFile().isReady())
                modelView.addItem("name", getFile());
        return modelView;
    }

    @Url(url = "/login")
    public ModelView login() {
        ModelView modelView = new ModelView("test.html");
        if(modelView.loginAdmin())
            modelView.setView("test.jsp");
        return modelView;
    }

    @Url(url = "/logout")
    public ModelView logout() {
        ModelView modelView = new ModelView("test.html");
        if(modelView.logout())
            modelView.setView("test.jsp");
        return modelView;
    }

    @RestAPI
    @Session
    @Url(url = "/find")
    public Emp get(Double id) {
        Emp emp = new Emp();
        emp.setCreation(new Date(0));
        emp.name = "";
        return emp;
    }

    public Date getCreation() {
        return creation;
    }

    public void setCreation(Date creation) {
        this.creation = creation;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
