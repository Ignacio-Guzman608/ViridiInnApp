package auth;

public class HashGenerator {
  public static void main(String[] args) {

    // ============================
    // 👇 CAMBIÁ ACÁ EL USUARIO
    // ============================
    String username = "admin";
    String password = "admin123";
    String fullName = "Administrador";
    String role = "ADMIN";
    // ============================

    String salt = PasswordUtils.generateSalt();
    String hash = PasswordUtils.hash(password, salt);

    System.out.println("================================");
    System.out.println("Usuario  : " + username);
    System.out.println("Password : " + password);
    System.out.println("Salt     : " + salt);
    System.out.println("Hash     : " + hash);
    System.out.println("================================");
    System.out.println();
    System.out.println("-- SQL para pegar en MySQL:");
    System.out.println();
    System.out.println("UPDATE User SET passwordHash = '" + hash
        + "', salt = '" + salt + "' WHERE username = '" + username + "';");
  }
}
