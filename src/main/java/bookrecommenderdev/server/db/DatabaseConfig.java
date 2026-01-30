package bookrecommenderdev.server.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;


public class DatabaseConfig {

  private static HikariDataSource DS;

  public static void setConfig(String host, String user, String password) {
    HikariConfig cfg = new HikariConfig();
    cfg.setJdbcUrl("jdbc:postgresql://" + host + "/bookrecommenderdev");
    cfg.setUsername(user);
    cfg.setPassword(password);

    cfg.setMaximumPoolSize(20);
    cfg.setMinimumIdle(2);
    cfg.setIdleTimeout(30_000);
    cfg.setConnectionTimeout(3_000);

    DS = new HikariDataSource(cfg);
  }

  public static DataSource getDataSource() {
    return DS;
  }

  private DatabaseConfig() {}
}
