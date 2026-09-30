package controllers;

import auth.LoginManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

  @FXML
  private TextField txtUsername;
  @FXML
  private PasswordField txtPassword;
  @FXML
  private Label lblError;
  @FXML
  private Button btnLogin;
  @FXML
  private Button btnCancel;

  @FXML
  public void initialize() {

    btnLogin.setOnAction(e -> attemptLogin());

    btnCancel.setOnAction(e -> {
      Stage stage = (Stage) btnCancel.getScene().getWindow();
      stage.setUserData(false);
      stage.close();
    });

    txtPassword.setOnAction(e -> attemptLogin());

    // Ocultar el label de error al arrancar
    lblError.setVisible(false);
    lblError.setManaged(false);
  }

  private void attemptLogin() {

    String user = txtUsername.getText().trim();
    String pass = txtPassword.getText();

    if (user.isEmpty() || pass.isEmpty()) {
      showError("Completá usuario y contraseña");
      return;
    }

    boolean ok = LoginManager.getInstance().login(user, pass);

    if (!ok) {
      showError("Usuario o contraseña incorrectos");
      txtPassword.clear();
      return;
    }

    Stage stage = (Stage) btnLogin.getScene().getWindow();
    stage.setUserData(true);
    stage.close();
  }

  private void showError(String message) {
    lblError.setText(message);
    lblError.setVisible(true);
    lblError.setManaged(true);
  }

}
