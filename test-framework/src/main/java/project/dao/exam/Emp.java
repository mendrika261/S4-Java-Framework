package project.dao.exam;

import database.core.DBConnection;
import database.core.GenericDAO;
import database.exception.object.NotIdentifiedInDatabaseException;
import project.dao.DaoConfig;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Emp extends GenericDAO {
    String nom;
    String prenom;

    public Emp() {
    }

    public List<Emp> getAll() throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        DBConnection dbConnection = DaoConfig.DATABASE.createConnection();
        List<Object> emps = super.getAll(dbConnection);
        List<Emp> empList = new ArrayList<>();
        for (Object emp : emps) {
            empList.add((Emp) emp);
        }
        dbConnection.close();
        return empList;
    }


    public String getId() {
        try {
            return super.getId();
        } catch (NotIdentifiedInDatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
}
