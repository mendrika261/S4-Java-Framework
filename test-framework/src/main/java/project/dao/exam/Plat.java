package project.dao.exam;

import database.core.DBConnection;
import database.core.GenericDAO;
import database.exception.object.NotIdentifiedInDatabaseException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import project.dao.DaoConfig;

public class Plat extends GenericDAO {
  String libelle;

  public Plat() {}

  public List<Plat> getAll()
      throws SQLException, InvocationTargetException, NoSuchMethodException,
             InstantiationException, IllegalAccessException {
    DBConnection dbConnection = DaoConfig.DATABASE.createConnection();
    List<Object> plats = super.getAll(dbConnection);
    List<Plat> platList = new ArrayList<>();
    for (Object plat : plats) {
      platList.add((Plat)plat);
    }
    dbConnection.close();
    return platList;
  }

  public String getId() {
    try {
      return super.getId();
    } catch (NotIdentifiedInDatabaseException e) {
      throw new RuntimeException(e);
    }
  }

  public String getLibelle() { return libelle; }

  public void setLibelle(String libelle) { this.libelle = libelle; }
}
