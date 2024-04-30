package project.controller.exam;

import database.exception.object.NotIdentifiedInDatabaseException;
import etu2024.framework.annotation.Auth;
import etu2024.framework.annotation.Url;
import etu2024.framework.annotation.Xml;
import etu2024.framework.core.File;
import etu2024.framework.core.ModelView;
import project.dao.exam.Emp;
import project.dao.exam.Plat;
import project.dao.exam.PlatConso;
import project.dao.exam.PlatConsoView;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.time.LocalDate;

public class PlatConsoController {
    LocalDate date;
    String plat_id;
    String emp_id;
    File file;

    @Auth
    @Url(url="/")
    public ModelView insertion() throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        ModelView modelView = new ModelView("/exam/insertion.jsp");
        modelView.addItem("empList", new Emp().getAll());
        modelView.addItem("platList", new Plat().getAll());

        modelView.addItem("localDate", getDate());
        modelView.addItem("platId", getPlat_id());
        modelView.addItem("empId", getEmp_id());
        modelView.addItem("file", getFile());

        if(getFile()!=null & getDate()!=null & getPlat_id()!=null & getEmp_id()!=null) {
            getFile().uploadToFile("/Users/mendrika/IdeaProjects/S4-Java-Framework/test-framework/src/main/webapp/upload/");
            PlatConso platConso = new PlatConso(getDate(), getPlat_id(), getEmp_id(), getFile().getName());
            platConso.save();
            modelView.addItem("message", "Insertion réussie");
        }

        return modelView;
    }

    @Url(url="/liste")
    public ModelView liste() throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NotIdentifiedInDatabaseException {
        ModelView modelView = new ModelView("/exam/liste.jsp");
        modelView.addItem("platConsoList", new PlatConso().getAll());
        return modelView;
    }

    @Url(url="/xml")
    @Xml
    public PlatConsoView[] xml() throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NotIdentifiedInDatabaseException {
        return new PlatConso().getAll().toArray(new PlatConsoView[0]);
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getPlat_id() {
        return plat_id;
    }

    public void setPlat_id(String plat_id) {
        this.plat_id = plat_id;
    }

    public String getEmp_id() {
        return emp_id;
    }

    public void setEmp_id(String emp_id) {
        this.emp_id = emp_id;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
