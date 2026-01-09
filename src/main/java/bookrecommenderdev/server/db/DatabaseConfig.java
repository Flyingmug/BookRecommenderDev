package bookrecommenderdev.server.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;


public class DatabaseConfig {

  private static final HikariDataSource DS;

  static {
    HikariConfig cfg = new HikariConfig();
    cfg.setJdbcUrl("jdbc:postgresql://localhost:5432/bookrecommenderdev");
    cfg.setUsername("postgres");
    cfg.setPassword("11111111");
    cfg.setMaximumPoolSize(20);
    cfg.setMinimumIdle(2);
    cfg.setIdleTimeout(30_000);
    cfg.setConnectionTimeout(3_000);
    DS = new HikariDataSource(cfg);
  }

  public static DataSource getDataSource() {
    return DS;
  }

//  public static void shutdown() {
//    if (DS != null) DS.close();
//  }

  // Costruttore per il Singleton design pattern
  private DatabaseConfig() {}
}
