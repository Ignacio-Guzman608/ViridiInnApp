package repositories;

import java.sql.*;

public class SettingsDAO {

  public String get(String key, String defaultValue) {
    String sql = "SELECT settingValue FROM Settings WHERE settingKey = ?";
    try (Connection conn = ConexionDB.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {

      ps.setString(1, key);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next())
          return rs.getString("settingValue");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return defaultValue;
  }

  public void set(String key, String value) {
    String sql = """
            INSERT INTO Settings (settingKey, settingValue) VALUES (?, ?)
            ON DUPLICATE KEY UPDATE settingValue = VALUES(settingValue)
        """;
    try (Connection conn = ConexionDB.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {

      ps.setString(1, key);
      ps.setString(2, value);
      ps.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public boolean getBoolean(String key, boolean defaultValue) {
    return Boolean.parseBoolean(get(key, String.valueOf(defaultValue)));
  }

  public int getInt(String key, int defaultValue) {
    try {
      return Integer.parseInt(get(key, String.valueOf(defaultValue)));
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }
}
