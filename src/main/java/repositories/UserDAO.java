package repositories;

import auth.PasswordUtils;
import models.User;

import java.sql.*;

public class UserDAO {

  public User findByUsername(String username) {
    String sql = "SELECT * FROM User WHERE username = ? AND active = TRUE";
    try (Connection conn = ConexionDB.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {

      ps.setString(1, username);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return new User(
              rs.getInt("idUser"),
              rs.getString("username"),
              rs.getString("passwordHash"),
              rs.getString("salt"),
              rs.getString("fullName"),
              rs.getString("role"),
              rs.getBoolean("active"));
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  public User authenticate(String username, String password) {
    User user = findByUsername(username);
    if (user == null)
      return null;
    if (!PasswordUtils.verify(password, user.getSalt(), user.getPasswordHash())) {
      return null;
    }
    return user;
  }
}
