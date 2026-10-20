package controllers;

import models.Staff;
import repositories.StaffDAO;
import utils.Alerts;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;

public class StaffHistoryController {

  @FXML private TableView<Staff> tableInactive;
  @FXML private TableColumn<Staff, String> colName;
  @FXML private TableColumn<Staff, String> colDni;
  @FXML private TableColumn<Staff, String> colPosition;

  @FXML private Button btnReactivate;
  @FXML private TextField txtSearch;
  @FXML private Label lblFooter;

  private final StaffDAO staffDAO = new StaffDAO();
  private ObservableList<Staff> inactiveList;
  private FilteredList<Staff> filteredList;

  // Estilos para que el botón mantenga su forma sin perder la opacidad
  private final String STYLE_BTN_DISABLED = "-fx-background-color: #a5b8b0; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10 24; -fx-background-radius: 6; -fx-opacity: 1.0; -fx-cursor: default;";
  private final String STYLE_BTN_ENABLED  = "-fx-background-color: #2E6D3D; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10 24; -fx-background-radius: 6; -fx-opacity: 1.0; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 4, 0, 0, 2);";

  @FXML
  public void initialize() {
    // Configurar columnas
    colName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFullName()));
    colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));
    colPosition.setCellValueFactory(new PropertyValueFactory<>("positionName"));

    // Cargar datos
    cargarInactivos();

    // Lógica del buscador
    txtSearch.textProperty().addListener((obs, oldVal, newVal) -> filtrarTabla(newVal));

    // Lógica de selección (Botón gris a verde)
    tableInactive.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
      if (newSelection != null) {
        btnReactivate.setDisable(false);
        btnReactivate.setStyle(STYLE_BTN_ENABLED);
      } else {
        btnReactivate.setDisable(true);
        btnReactivate.setStyle(STYLE_BTN_DISABLED);
      }
    });

    // Estado inicial del botón
    btnReactivate.setStyle(STYLE_BTN_DISABLED);

    // Configurar acción del botón
    btnReactivate.setOnAction(e -> reactivarSeleccionado());
  }

  private void cargarInactivos() {
    try {
      inactiveList = FXCollections.observableArrayList(staffDAO.findInactive());
      filteredList = new FilteredList<>(inactiveList, s -> true);
      tableInactive.setItems(filteredList);
      actualizarContador();
    } catch (SQLException e) {
      mostrarAlerta("No se pudo cargar el listado de inactivos: " + e.getMessage());
    }
  }

  private void filtrarTabla(String texto) {
    if (filteredList == null) return;
    String filtro = texto == null ? "" : texto.toLowerCase().trim();

    filteredList.setPredicate(staff -> {
      if (filtro.isEmpty()) return true;
      return staff.getFullName().toLowerCase().contains(filtro) ||
              staff.getDni().toLowerCase().contains(filtro) ||
              (staff.getPositionName() != null && staff.getPositionName().toLowerCase().contains(filtro));
    });
    actualizarContador();
    tableInactive.getSelectionModel().clearSelection(); // Limpiar selección al buscar
  }

  private void actualizarContador() {
    if (filteredList != null) {
      lblFooter.setText("Mostrando " + filteredList.size() + " empleados inactivos");
    }
  }

  private void reactivarSeleccionado() {
    Staff seleccionado = tableInactive.getSelectionModel().getSelectedItem();
    if (seleccionado == null) {
      mostrarAlerta("Seleccioná un empleado de la lista primero.");
      return;
    }

    try {
      staffDAO.reactivate(seleccionado.getId());
      cargarInactivos(); // Refresca la lista
      mostrarAlerta("El personal ha sido reincorporado satisfactoriamente.");
    } catch (SQLException e) {
      mostrarAlerta("No se pudo reactivar: " + e.getMessage());
    }
  }

  private void mostrarAlerta(String mensaje) {
    Alerts.info("Personal", mensaje);
  }
}