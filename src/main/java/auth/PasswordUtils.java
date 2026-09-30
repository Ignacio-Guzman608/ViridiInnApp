package auth;

public final class PasswordUtils {

  private PasswordUtils() {
  }

  /**
   * MODO DESARROLLO: no hashea, devuelve el password tal cual.
   * ⚠️ Cambiar a SHA-256/BCrypt antes de producción.
   */
  public static String generateSalt() {
    return "no-salt";
  }

  public static String hash(String password, String salt) {
    return password; // ← texto plano
  }

  public static boolean verify(String password, String salt, String expectedHash) {
    return password != null && password.equals(expectedHash);
  }
}
