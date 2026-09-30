package auth;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LoginGuard {

  private LoginGuard() {
  }

  /**
   * Devuelve TRUE si la acción puede continuar.
   * - Login desactivado → TRUE
   * - Ya logueado → TRUE
   * - Si no → abre dialog y devuelve el resultado
   */
  public static boolean requireAccess() {
    LoginManager lm = LoginManager.getInstance();

    if (!lm.isLoginEnabled())
      return true;
    if (lm.isAuthenticated()) {
      lm.touch();
      return true;
    }

    return showLoginDialog();
  }

  /**
   * Para acciones que requieren un rol específico (ej: ADMIN)
   */
  public static boolean requireRole(String role) {
    if (!requireAccess())
      return false;
    return LoginManager.getInstance().hasRole(role);
  }

  private static boolean showLoginDialog() {
    try {
      FXMLLoader loader = new FXMLLoader(
          LoginGuard.class.getResource("/views/Login.fxml"));
      Parent root = loader.load();

      Scene scene = new Scene(root);

      // 👇 Tu CSS global
      var css = LoginGuard.class.getResource("/styles/styles.css");
      if (css != null) {
        scene.getStylesheets().add(css.toExternalForm());
      }

      Stage stage = new Stage();
      stage.setTitle("Iniciar Sesión");

      stage.initModality(Modality.APPLICATION_MODAL);

      // 👇 Tamaño fijo (equivalente al prefWidth/prefHeight del FXML)
      stage.setWidth(380);
      stage.setHeight(420);

      stage.sizeToScene(); // 👈 se ajusta al contenido del FXML
      stage.setScene(scene);

      // 👇 Centrada en pantalla (no pegada a la esquina)
      stage.centerOnScreen();

      stage.showAndWait();

      Object result = stage.getUserData();
      return result instanceof Boolean && (Boolean) result;

    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
}
