package auth;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LoginGuard {

    private LoginGuard() {}

    /**
     * Devuelve TRUE si la acción puede continuar.
     * - Login desactivado → TRUE
     * - Ya logueado → TRUE
     * - Si no → abre dialog y devuelve el resultado
     */
    public static boolean requireAccess() {
        LoginManager lm = LoginManager.getInstance();

        if (!lm.isLoginEnabled()) return true;
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
        if (!requireAccess()) return false;
        return LoginManager.getInstance().hasRole(role);
    }

    private static boolean showLoginDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    LoginGuard.class.getResource("/views/Login.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Iniciar Sesión");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            Object result = stage.getUserData();
            return result instanceof Boolean && (Boolean) result;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
