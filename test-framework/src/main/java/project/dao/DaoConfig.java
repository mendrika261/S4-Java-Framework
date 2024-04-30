package project.dao;

import database.core.Database;
import database.provider.PostgreSQL;

public class DaoConfig {
  public static Database DATABASE =
      new PostgreSQL("localhost", "5432", "dao", "mendrika", "");
}
