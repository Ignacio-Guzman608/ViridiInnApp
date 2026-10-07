package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import utils.StyleManager;

import java.io.IOException;

public class ConfigurationController {

  @FXML
  private Button btnProducts;

  @FXML
  private Button btnServices;

  // 🔑 Referencias a las ventanas hijas
  private Stage productsStage;
  private Stage servicesStage;

  @FXML
  public void initialize() {
    btnProducts.setOnAction(e -> openProducts());
    btnServices.setOnAction(e -> openServices());
  }

  private void openProducts() {

    // 🔑 Si ya está abierta, traerla al frente y salir
    if (productsStage != null && productsStage.isShowing()) {
      productsStage.toFront();
      productsStage.requestFocus();
      return;
    }

    try {

      FXMLLoader loader = new FXMLLoader(
          getClass().getResource("/views/Product.fxml"));

      productsStage = new Stage();
      productsStage.setScene(new Scene(loader.load()));
      StyleManager.applyStyles(productsStage);
      productsStage.setTitle("Productos");

      // 🔑 Al cerrarse: limpiar referencia
      productsStage.setOnHidden(evt -> productsStage = null);

      productsStage.show(); // show() está bien si querés ventana no-modal

    } catch (IOException e) {
      productsStage = null;
      e.printStackTrace();
    }
  }

  private void openServices() {

    // 🔑 Si ya está abierta, traerla al frente y salir
    if (servicesStage != null && servicesStage.isShowing()) {
      servicesStage.toFront();
      servicesStage.requestFocus();
      return;
    }

    try {

      FXMLLoader loader = new FXMLLoader(
          getClass().getResource("/views/Service.fxml"));

      servicesStage = new Stage();
      servicesStage.setScene(new Scene(loader.load()));
      StyleManager.applyStyles(servicesStage);
      servicesStage.setTitle("Servicios");

      // 🔑 Al cerrarse: limpiar referencia
      servicesStage.setOnHidden(evt -> servicesStage = null);

      servicesStage.show();

    } catch (Exception e) {
      servicesStage = null;
      e.printStackTrace();
    }
  }
}
