package project.dao.exam;

import database.core.GenericDAO;

public class PlatConsoView extends GenericDAO {
  String id;
  String date;
  String prenom;
  String nom;
  String plat;
  String file;

  public PlatConsoView() {}

  @Override
  public String getId() {
    return id;
  }

  @Override
  public void setId(String id) {
    this.id = id;
  }

  public String getDate() { return date; }

  public void setDate(String date) { this.date = date; }

  public String getPrenom() { return prenom; }

  public void setPrenom(String prenom) { this.prenom = prenom; }

  public String getNom() { return nom; }

  public void setNom(String nom) { this.nom = nom; }

  public String getPlat() { return plat; }

  public void setPlat(String plat) { this.plat = plat; }

  public String getFile() { return file; }

  public void setFile(String file) { this.file = file; }
}
