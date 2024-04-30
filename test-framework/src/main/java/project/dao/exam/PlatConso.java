package project.dao.exam;

import database.core.DBConnection;
import database.core.GenericDAO;
import database.exception.object.NotIdentifiedInDatabaseException;
import project.dao.DaoConfig;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PlatConso extends GenericDAO {
    LocalDate localDate;
    String platId;
    String empId;
    String file;

    public PlatConso() {
    }

    public PlatConso(LocalDate localDate, String platId, String empId, String file) {
        setLocalDate(localDate);
        setPlatId(platId);
        setEmpId(empId);
        setFile(file);
    }

    public void save() throws SQLException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        DBConnection dbConnection = DaoConfig.DATABASE.createConnection();
        super.save(dbConnection);
        dbConnection.commit();
        dbConnection.close();
    }

    public List<PlatConsoView> getAll() throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NotIdentifiedInDatabaseException {
        DBConnection dbConnection = DaoConfig.DATABASE.createConnection();
        List<Object> platconsos = super.getAll(dbConnection);
        List<PlatConsoView> plats = new ArrayList<>();
        for (Object platconso : platconsos) {
            PlatConso act = (PlatConso) platconso;
            PlatConsoView platConsoView = new PlatConsoView();

            platConsoView.setId(act.getId());
            platConsoView.setFile(act.getFile());
            platConsoView.setDate(String.valueOf(act.getLocalDate()));

            platConsoView.setPlat(((Plat)new Plat().getById(dbConnection, act.getPlatId())).getLibelle());

            Emp emp = (Emp) new Emp().getById(dbConnection, act.getEmpId());
            platConsoView.setNom(emp.getNom());
            platConsoView.setPrenom(emp.getPrenom());


            plats.add(platConsoView);
        }
        dbConnection.close();
        return plats;
    }

    public Plat getPlat() {
        try {
            DBConnection dbConnection = DaoConfig.DATABASE.createConnection();
            Plat plat = (Plat) new Plat().getById(dbConnection, getPlatId());
            dbConnection.close();
            return plat;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Emp getEmp() {
        try {
            DBConnection dbConnection = DaoConfig.DATABASE.createConnection();
            Emp emp = (Emp) new Emp().getById(dbConnection, getEmpId());
            dbConnection.close();
            return emp;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    public String getPlatId() {
        return platId;
    }

    public void setPlatId(String platId) {
        this.platId = platId;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
