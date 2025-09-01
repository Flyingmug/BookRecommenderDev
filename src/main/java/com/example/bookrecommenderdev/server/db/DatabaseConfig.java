package com.example.bookrecommenderdev.server.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;


public class DatabaseConfig {

  private static final HikariDataSource DS;

  static {
    HikariConfig cfg = new HikariConfig();
    cfg.setJdbcUrl("jdbc:postgresql://localhost:5432/postgres");
    cfg.setUsername("postgres");
    cfg.setPassword("1234");
    cfg.setMaximumPoolSize(20);
    cfg.setMinimumIdle(2);
    cfg.setIdleTimeout(30_000);
    cfg.setConnectionTimeout(3_000);
// Optional health query: cfg.setConnectionTestQuery("SELECT 1");
    DS = new HikariDataSource(cfg);
  }

  /**
   * Consente il modo per ottenere una connessione al database.
   * @return Datasource per l'ottenimento delle connessioni
   */
  public static DataSource getDataSource() {
    return DS;
  }

//  public static void shutdown() {
//    if (DS != null) DS.close();
//  }

  // Costruttore per il Singleton design pattern
  private DatabaseConfig() {}
}
