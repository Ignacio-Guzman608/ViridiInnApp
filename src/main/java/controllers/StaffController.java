package controllers;

import models.Staff;
import models.StaffStatus;
import repositories.StaffDAO;
import utils.Alerts;
import utils.StyleManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import java.time.format.DateTimeFormatter;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.sql.SQLException;

public class StaffController {

  // ---------- Tabla principal ----------
  @FXML private TableView<Staff> tableStaff;
  @FXML private TableColumn<Staff, String> colFullName;
  @FXML private TableColumn<Staff, String> colPosition;
  @FXML private TableColumn<Staff, String> colPhone;

  // ---------- Buscador y filtro ----------
  @FXML private TextField txtSearch;

  // ---------- Panel de detalle ----------
  @FXML private Label lblDetailName;
  @FXML private Label lblDetailPosition;
  @FXML private Label lblDetailStatus;
  @FXML private Label lblDetailDni;
  @FXML private Label lblDetailBirthDate;
  @FXML private Label lblDetailPhone;
  @FXML private Label lblDetailEmail;
  @FXML private Label lblDetailAddress;
  @FXML private Label lblDetailHireDate;
  @FXML private Label lblDetailShift;
  @FXML private Label lblDetailSalary;

  // ---------- Botones del panel ----------
  @FXML private Button btnEdit;
  @FXML private Button btnDeactivate;

  private final StaffDAO staffDAO = new StaffDAO();
  private ObservableList<Staff> staffList;
  private FilteredList<Staff> filteredStaffList;

  // Variables con los estilos exactos de tus capturas (Letra 14px y grosor 600)
  private final String STYLE_EDIT_DISABLED = "-fx-background-color: #f2f4f5; -fx-text-fill: #a0aab2; -fx-font-size: 14px; -fx-font-weight: 600; -fx-padding: 10px 20px; -fx-background-radius: 6px; -fx-opacity: 1.0; -fx-cursor: default;";
  private final String STYLE_EDIT_ENABLED  = "-fx-background-color: #e8ebed; -fx-text-fill: #4a525a; -fx-font-size: 14px; -fx-font-weight: 600; -fx-padding: 10px 20px; -fx-background-radius: 6px; -fx-opacity: 1.0; -fx-cursor: hand;";

  private final String STYLE_DEL_DISABLED  = "-fx-background-color: #fdf5f6; -fx-text-fill: #e5a5a5; -fx-font-size: 14px; -fx-font-weight: 600; -fx-padding: 10px 20px; -fx-background-radius: 6px; -fx-opacity: 1.0; -fx-cursor: default;";
  private final String STYLE_DEL_ENABLED   = "-fx-background-color: #f8d7da; -fx-text-fill: #c0392b; -fx-font-size: 14px; -fx-font-weight: 600; -fx-padding: 10px 20px; -fx-background-radius: 6px; -fx-opacity: 1.0; -fx-cursor: hand;";

  @FXML
  public void initialize() {
    configurarColumnas();
    cargarDatos();
    configurarBuscador();
    configurarSeleccionDeFila();

    // Forzamos el estado inicial deshabilitado visualmente
    mostrarDetalle(null);

    // Forzar que arranquen con el estilo puesto
    btnEdit.setStyle(STYLE_EDIT_DISABLED);
    btnDeactivate.setStyle(STYLE_DEL_DISABLED);
  }

  private void configurarColumnas() {
    colFullName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFullName()));
    colPosition.setCellValueFactory(new PropertyValueFactory<>("positionName"));
    colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
  }

  private void cargarDatos() {
    try {
      staffList = FXCollections.observableArrayList(staffDAO.listAll());
      filteredStaffList = new FilteredList<>(staffList, s -> true);
      tableStaff.setItems(filteredStaffList);
      aplicarFiltros();
    } catch (SQLException e) {
      mostrarAlerta("No se pudo cargar la lista de personal: " + e.getMessage());
    }
  }

  private void configurarBuscador() {
    txtSearch.textProperty().addListener((obs, oldValue, newValue) -> aplicarFiltros());
  }

  private void aplicarFiltros() {
    String texto = txtSearch.getText() == null ? "" : txtSearch.getText().toLowerCase().trim();

    filteredStaffList.setPredicate(staff -> {
      boolean esActivo = staff.getStatus() == StaffStatus.ACTIVE;
      boolean coincideTexto = texto.isEmpty()
              || staff.getFullName().toLowerCase().contains(texto)
              || staff.getDni().toLowerCase().contains(texto)
              || (staff.getPositionName() != null && staff.getPositionName().toLowerCase().contains(texto))
              || (staff.getPhone() != null && staff.getPhone().toLowerCase().contains(texto))
              || (staff.getEmail() != null && staff.getEmail().toLowerCase().contains(texto))
              || (staff.getCity() != null && staff.getCity().toLowerCase().contains(texto));

      return esActivo && coincideTexto;
    });

    tableStaff.getSelectionModel().clearSelection();
  }

  private void configurarSeleccionDeFila() {
    tableStaff.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> mostrarDetalle(newSelection));
  }

  private void mostrarDetalle(Staff staff) {
    if (staff == null) {
      // Limpiar textos
      lblDetailName.setText("Nombre Completo");
      lblDetailPosition.setText("--");
      lblDetailStatus.setText("--");
      lblDetailDni.setText("--");
      lblDetailBirthDate.setText("--");
      lblDetailPhone.setText("--");
      lblDetailEmail.setText("--");
      lblDetailAddress.setText("--");
      lblDetailHireDate.setText("--");
      lblDetailShift.setText("--");
      lblDetailSalary.setText("--");

      // Aplicar estilos deshabilitados
      btnEdit.setDisable(true);
      btnEdit.setStyle(STYLE_EDIT_DISABLED);
      btnDeactivate.setDisable(true);
      btnDeactivate.setStyle(STYLE_DEL_DISABLED);
      return;
    }

    // Cargar textos
    lblDetailName.setText(staff.getFullName());
    lblDetailPosition.setText(staff.getPositionName());
    lblDetailStatus.setText(staff.getStatus().toString());
    lblDetailDni.setText(staff.getDni());
    DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    lblDetailBirthDate.setText(staff.getBirthDate() != null ? staff.getBirthDate().format(df) : "-");
    lblDetailPhone.setText(staff.getPhone());
    lblDetailEmail.setText(staff.getEmail());
    lblDetailAddress.setText(staff.getStreet() + " " + staff.getAddressNumber() + ", " + staff.getCity());
    lblDetailHireDate.setText(staff.getHireDate() != null ? staff.getHireDate().format(df) : "-");
    String turnoTexto = staff.getShiftName() != null ? staff.getShiftName() : "Sin turno asignado";
    if (staff.getShiftStart() != null && staff.getShiftEnd() != null) {
      turnoTexto += " (" + staff.getShiftStart() + " - " + staff.getShiftEnd() + ")";
    }
    lblDetailShift.setText(turnoTexto);
    lblDetailSalary.setText(staff.getSalary() != null ? "$ " + staff.getSalary() : "-");

    // Aplicar estilos habilitados
    btnEdit.setDisable(false);
    btnEdit.setStyle(STYLE_EDIT_ENABLED);
    btnDeactivate.setDisable(false);
    btnDeactivate.setStyle(STYLE_DEL_ENABLED);
  }

  @FXML private void handleNewStaff() { abrirFormulario(null); }

  @FXML
  private void handleEdit() {
    Staff seleccionado = tableStaff.getSelectionModel().getSelectedItem();
    if (seleccionado == null) return;
    abrirFormulario(seleccionado);
  }

  private void abrirFormulario(Staff staffAEditar) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/StaffForm.fxml"));
      Parent root = loader.load();

      StaffFormController formController = loader.getController();
      if (staffAEditar != null) formController.setStaff(staffAEditar);

      Stage stage = new Stage();
      stage.setTitle(staffAEditar == null ? "Nuevo Personal" : "Editar Personal");
      stage.setScene(new Scene(root));
      StyleManager.applyStyles(stage);
      stage.initOwner(tableStaff.getScene().getWindow());
      stage.initModality(Modality.APPLICATION_MODAL);

      javafx.stage.Window previousOwner = StyleManager.getOwner();
      stage.setOnShown(e -> StyleManager.setOwner(stage));
      stage.setOnHidden(e -> {
        StyleManager.setOwner(previousOwner);
        cargarDatos();
        aplicarFiltros();
      });
      stage.showAndWait();
    } catch (IOException e) {
      mostrarAlerta("No se pudo abrir el formulario: " + e.getMessage());
    }
  }

  @FXML
  private void handleDeactivate() {
    Staff seleccionado = tableStaff.getSelectionModel().getSelectedItem();
    if (seleccionado == null) return;

    boolean confirmado = Alerts.confirm(
            "Inactivar personal",
            "¿Seguro que querés inactivar a " + seleccionado.getFullName() + "?",
            "El empleado pasará al listado de inactivos y podrá reactivarse después.");

    if (!confirmado) return;

    try {
      staffDAO.deactivate(seleccionado.getId());
      cargarDatos();
      aplicarFiltros();
      Alerts.info("Baja lógica registrada", "El registro del personal fue inactivado satisfactoriamente.\n\n");
    } catch (SQLException e) {
      mostrarAlerta("No se pudo inactivar: " + e.getMessage());
    }
  }

  @FXML
  private void handleViewHistory() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/StaffHistory.fxml"));
      Parent root = loader.load();
      Stage stage = new Stage();
      stage.setTitle("Personal Inactivo");
      stage.setScene(new Scene(root));
      StyleManager.applyStyles(stage);
      stage.initOwner(tableStaff.getScene().getWindow());
      stage.initModality(Modality.WINDOW_MODAL);
      stage.setOnHidden(e -> {
        cargarDatos();
        aplicarFiltros();
      });
      stage.show();
    } catch (IOException e) {
      mostrarAlerta("No se pudo abrir el listado de inactivos: " + e.getMessage());
    }
  }

  private void mostrarAlerta(String mensaje) {
    Alerts.info("Personal", mensaje);
  }
}