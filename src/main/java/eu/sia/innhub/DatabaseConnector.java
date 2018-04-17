package eu.sia.innhub;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.stream.Stream;
import java.sql.*;

class DatabaseConnector {

  void writeIstatEntries(Stream<IstatEntry> istatEntries, String table) throws ClassNotFoundException, SQLException {
    final Config conf = ConfigFactory.load().getConfig("db");

    Class.forName(this.getDBDriver(conf));

    final Connection con = this.getConnection(conf);

    PreparedStatement truncateStatement = con.prepareStatement("TRUNCATE " + table);
    truncateStatement.execute();
    truncateStatement.close();

    PreparedStatement ps = con.prepareStatement("INSERT INTO " + table + " VALUES (?, ?)");

    istatEntries.forEach(istatEntry -> {
      try {
        ps.setDate(1, Date.valueOf(istatEntry.date));
        ps.setDouble(2, istatEntry.value.doubleValue());
        ps.addBatch();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    });

    ps.executeBatch();
    ps.close();
    con.close();
  }

  private Connection getConnection(Config conf) throws SQLException {
    return DriverManager.getConnection(
      "jdbc:mysql://" + conf.getString("address") + ":" + conf.getInt("port") + "/" + conf.getString("schema"),
      conf.getString("user"),
      conf.getString("password")
    );
  }

  private String getDBDriver(Config conf) {
    String driver;
    switch (conf.getString("type")) {
      case "vertica":
        driver = "com.vertica.jdbc.Driver";
        break;
      case "mysql":
        driver = "com.mysql.cj.jdbc.Driver";
        break;
      default:
        throw new IllegalArgumentException("Configure Vertica or MySQL DB");
    }
    return driver;
  }

}
