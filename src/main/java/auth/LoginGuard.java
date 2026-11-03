package auth;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class LoginGuard {

  private LoginGuard() {
  }

  // Compatibilidad: si no hay owner, sigue funcionando (APPLICATION_MODAL)
  public static boolean requireAccess() {
    return requireAccess(null);
  }

  public static boolean requireAccess(Window owner) {
    LoginManager lm = LoginManager.getInstance();

    if (!lm.isLoginEnabled())
      return true;
    if (lm.isAuthenticated()) {
      lm.touch();
      return true;
    }

    return showLoginDialog(owner);
  }

  public static boolean requireRole(String role) {
    return requireRole(null, role);
  }

  public static boolean requireRole(Window owner, String role) {
    if (!requireAccess(owner))
      return false;
    return LoginManager.getInstance().hasRole(role);
  }

  private static boolean showLoginDialog(Window owner) {
    try {
      FXMLLoader loader = new FXMLLoader(
          LoginGuard.class.getResource("/views/Login.fxml"));
      Parent root = loader.load();

      Stage stage = new Stage();
      stage.setTitle("Iniciar Sesión");
      stage.setScene(new Scene(root));
      stage.setResizable(false);

      // ⚠️ ORDEN: initOwner → initModality → show
      if (owner != null) {
        stage.initOwner(owner);
        stage.initModality(Modality.WINDOW_MODAL); // bloquea SOLO la ventana padre
      } else {
        // Sin owner, WINDOW_MODAL se degrada a NONE → usamos APPLICATION_MODAL
        stage.initModality(Modality.APPLICATION_MODAL);
      }

      // Cerrar con la X cuenta como cancelado
      stage.setOnCloseRequest(e -> stage.setUserData(false));

      // Centrar SOBRE el padre (centerOnScreen centra sobre la pantalla)
      stage.setOnShown(e -> {
        if (owner != null) {
          stage.setX(owner.getX() + (owner.getWidth() - stage.getWidth()) / 2);
          stage.setY(owner.getY() + (owner.getHeight() - stage.getHeight()) / 2);
        } else {
          stage.centerOnScreen();
        }
      });

      stage.showAndWait();

      Object result = stage.getUserData();
      return result instanceof Boolean && (Boolean) result;

    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
}
