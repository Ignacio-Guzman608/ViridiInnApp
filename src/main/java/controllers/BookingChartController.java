package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import models.Reservation;
import models.ReservationRoom;
import models.Room;
import repositories.ReservationRepo;
import repositories.ReservationRoomRepo;
import repositories.RoomDAO;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public class BookingChartController {

    @FXML private Button btnPreviousMonth;
    @FXML private Button btnNextMonth;
    @FXML private Button btnNewReservation;
    @FXML private ToggleButton btnFullMonth;
    @FXML private ToggleButton btnFirstFortnight;

    @FXML
    private ToggleButton btnSecondFortnight;
    @FXML private Label lblMonth;
    @FXML private ScrollPane scrollPane;
    @FXML private VBox gridContainer;

    private int currentYear = LocalDate.now().getYear();
    private int currentMonth = LocalDate.now().getMonthValue();
    private enum ViewMode {
        FULL_MONTH,
        FIRST_FORTNIGHT,
        SECOND_FORTNIGHT
    }

    private ViewMode currentViewMode = ViewMode.FULL_MONTH;
    private final RoomDAO roomDAO = new RoomDAO();

    private final ReservationRepo reservationRepo = new ReservationRepo();
    private final ReservationRoomRepo reservationRoomRepo = new ReservationRoomRepo();

    private Reservation selectedReservation;
    private VBox selectedBookingBlock;

    private Integer selectedRoomNumber;
    private LocalDate selectedStartDate;
    private LocalDate selectedEndDate;

    @FXML
    public void initialize() {

        btnPreviousMonth.setOnAction(event -> {

            currentMonth--;

            if (currentMonth < 1) {
                currentMonth = 12;
                currentYear--;
            }

            updateBookingChart();
        });

        btnNextMonth.setOnAction(event -> {

            currentMonth++;

            if (currentMonth > 12) {
                currentMonth = 1;
                currentYear++;
            }

            updateBookingChart();
        });

        btnNewReservation.setOnAction(event -> {

            if (selectedRoomNumber == null
                    || selectedStartDate == null
                    || selectedEndDate == null) {
                return;
            }

            try {
                FXMLLoader loader =
                        new FXMLLoader(getClass().getResource("/views/NewReservation.fxml"));

                Parent root = loader.load();

                NewReservationController controller = loader.getController();

                controller.setSelectedRoom(selectedRoomNumber);
                controller.setSelectedDates(
                        selectedStartDate,
                        selectedEndDate
                );

                Stage stage = new Stage();
                stage.setTitle("Nueva Reserva");
                stage.setScene(new Scene(root));
                stage.show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ToggleGroup viewGroup = new ToggleGroup();

        btnFullMonth.setToggleGroup(viewGroup);
        btnFirstFortnight.setToggleGroup(viewGroup);
        btnSecondFortnight.setToggleGroup(viewGroup);

        btnFullMonth.setSelected(true);

        btnFullMonth.setOnAction(event -> {
            currentViewMode = ViewMode.FULL_MONTH;
            createBookingGrid();
        });

        btnFirstFortnight.setOnAction(event -> {
            currentViewMode = ViewMode.FIRST_FORTNIGHT;
            createBookingGrid();
        });

        btnSecondFortnight.setOnAction(event -> {
            currentViewMode = ViewMode.SECOND_FORTNIGHT;
            createBookingGrid();

        });

        createBookingGrid();
    }

    private void updateBookingChart() {
        createBookingGrid();
    }

    private void createBookingGrid() {

        gridContainer.getChildren().clear();

        YearMonth yearMonth = YearMonth.of(currentYear, currentMonth);

        int daysInMonth = yearMonth.lengthOfMonth();

        int startDay;
        int endDay;

        switch (currentViewMode) {

            case FIRST_FORTNIGHT -> {
                startDay = 1;
                endDay = 15;
            }

            case SECOND_FORTNIGHT -> {
                startDay = 16;
                endDay = daysInMonth;
            }

            default -> {
                startDay = 1;
                endDay = daysInMonth;
            }
        }

        int visibleDays = endDay - startDay + 1;

        lblMonth.setText(yearMonth.getMonth().toString() + " " + currentYear
        );

        HBox headerRow = new HBox();

        boolean isFortnight = currentViewMode != ViewMode.FULL_MONTH;

        double availableWidth = scrollPane.getWidth();
        double roomColumnWidth = 55;

        double dayWidth;

        if (isFortnight && availableWidth > 0) {
            int numberOfDays = endDay - startDay + 1;
            dayWidth = (availableWidth - roomColumnWidth) / numberOfDays;
        } else {
            dayWidth = 35;
        }

        Label roomHeader = new Label("ROOM");

        roomHeader.setPrefWidth(55);
        roomHeader.setPrefHeight(45);
        roomHeader.getStyleClass().add("grid-header");

        headerRow.getChildren().add(roomHeader);

        for (int day = startDay; day <= endDay; day++) {

            LocalDate date = yearMonth.atDay(day);

            String dayOfWeek = switch (date.getDayOfWeek()) {
                case MONDAY -> "MON";
                case TUESDAY -> "TUE";
                case WEDNESDAY -> "WED";
                case THURSDAY -> "THU";
                case FRIDAY -> "FRI";
                case SATURDAY -> "SAT";
                case SUNDAY -> "SUN";
            };

            VBox dayBox = new VBox();

            dayBox.setPrefWidth(dayWidth);
            dayBox.setMinWidth(dayWidth);
            dayBox.setMaxWidth(dayWidth);
            dayBox.setAlignment(javafx.geometry.Pos.CENTER);
            dayBox.getStyleClass().add("grid-header");

            Label dayName = new Label(dayOfWeek);
            dayName.setStyle("-fx-font-size: 9px;");

            Label dayNumber = new Label(String.valueOf(day));
            dayNumber.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");

            dayBox.getChildren().addAll(dayName, dayNumber);

            headerRow.getChildren().add(dayBox);
        }

        gridContainer.getChildren().add(headerRow);

        List<Room> rooms = roomDAO.listActive();

        List<Reservation> reservations = reservationRepo.getReservations();

        List<ReservationRoom> reservationRooms = reservationRoomRepo.getAll();

        for (Room room : rooms) {

            HBox roomRow = new HBox();
            roomRow.setUserData(room.getNumber());
            roomRow.setMaxWidth(Double.MAX_VALUE);
            roomRow.getStyleClass().add("room-row");
            roomRow.setPrefHeight(22);
            roomRow.setMinHeight(22);
            roomRow.setMaxHeight(22);

            Label roomLabel =
                    new Label(String.valueOf(room.getNumber()));

            roomLabel.setPrefHeight(22);
            roomLabel.setMinHeight(22);
            roomLabel.setMaxHeight(22);
            roomLabel.getStyleClass().add("room-label");

            roomRow.getChildren().add(roomLabel);

            for (int day = startDay; day <= endDay; ) {

                LocalDate currentDate = yearMonth.atDay(day);

                Reservation currentReservation = null;

                for (Reservation reservation : reservations) {

                    if (reservation.getIdReservationStatus() == 3) {
                        continue;
                    }

                    boolean reservationUsesRoom = reservationRooms.stream()
                            .anyMatch(rr ->
                                    rr.getIdReservation() == reservation.getIdReservation()
                                            && rr.getRoomNumber() == room.getNumber()
                            );

                    if (!reservationUsesRoom) {
                        continue;
                    }

                    boolean occupied =
                            !currentDate.isBefore(reservation.getCheckIn())
                                    && currentDate.isBefore(reservation.getCheckOut());

                    if (occupied) {
                        currentReservation = reservation;
                        break;
                    }
                }

                if (currentReservation != null) {

                    int reservationStartDay =
                            Math.max(startDay, currentReservation.getCheckIn().getDayOfMonth());

                    int reservationEndDay =
                            Math.min(endDay, currentReservation.getCheckOut().getDayOfMonth() - 1);

                    int numberOfDays =
                            reservationEndDay - day + 1;

                    double blockWidth =
                            (dayWidth * numberOfDays) - 8;

                    VBox bookingBlock = new VBox();

                    bookingBlock.setPrefWidth(blockWidth);
                    bookingBlock.setMinWidth(blockWidth);
                    bookingBlock.setMaxWidth(blockWidth);

                    bookingBlock.setPrefHeight(16);
                    bookingBlock.setMinHeight(16);
                    bookingBlock.setMaxHeight(16);

                    bookingBlock.setAlignment(javafx.geometry.Pos.CENTER);

                    bookingBlock.getStyleClass().add("booking-block");

                    Reservation reservationToSelect = currentReservation;

                    String statusText = switch (reservationToSelect.getIdReservationStatus()) {
                        case 1 -> "Pendiente";
                        case 2 -> "Confirmada";
                        case 3 -> "Cancelada";
                        case 4 -> "Finalizada";
                        default -> "Desconocido";
                    };

                    String tooltipText =
                            "Reserva #" + reservationToSelect.getIdReservation() + "\n" +
                                    "Habitación: " + room.getNumber() + "\n" +
                                    "Check-in: " + reservationToSelect.getCheckIn() + "\n" +
                                    "Check-out: " + reservationToSelect.getCheckOut() + "\n" +
                                    "Huéspedes: " + reservationToSelect.getNumberOfGuests() + "\n" +
                                    "Tarifa: $" + reservationToSelect.getTotalRate() + "\n" +
                                    "Estado: " + statusText;

                    Tooltip tooltip = new Tooltip(tooltipText);
                    tooltip.setShowDelay(javafx.util.Duration.millis(350));
                    Tooltip.install(bookingBlock, tooltip);

                    bookingBlock.setOnMouseClicked(event -> {

                        if (selectedBookingBlock != null) {
                            selectedBookingBlock.getStyleClass().remove("selected-block");
                        }

                        if (selectedReservation == reservationToSelect) {
                            selectedReservation = null;
                            selectedBookingBlock = null;
                            return;
                        }

                        selectedReservation = reservationToSelect;
                        selectedBookingBlock = bookingBlock;

                        bookingBlock.getStyleClass().add("selected-block");
                    });

                    switch (currentReservation.getIdReservationStatus()) {
                        case 1 -> bookingBlock.getStyleClass().add("pending-block");
                        case 2 -> bookingBlock.getStyleClass().add("confirmed-block");
                        case 4 -> bookingBlock.getStyleClass().add("finished-block");
                    }

                    HBox reservationContainer = new HBox();

                    reservationContainer.setPrefWidth(dayWidth * numberOfDays);
                    reservationContainer.setMinWidth(dayWidth * numberOfDays);
                    reservationContainer.setMaxWidth(dayWidth * numberOfDays);

                    reservationContainer.setPrefHeight(22);
                    reservationContainer.setMinHeight(22);
                    reservationContainer.setMaxHeight(22);
                    reservationContainer.setAlignment(javafx.geometry.Pos.CENTER);

                    reservationContainer.getStyleClass().add("reservation-container");

                    reservationContainer.getChildren().add(bookingBlock);

                    roomRow.getChildren().add(reservationContainer);

                    day += numberOfDays;

                } else {

                    VBox emptyCell = new VBox();
                    emptyCell.setUserData(currentDate);

                    emptyCell.setPrefWidth(dayWidth);
                    emptyCell.setMinWidth(dayWidth);
                    emptyCell.setMaxWidth(dayWidth);

                    emptyCell.setPrefHeight(22);
                    emptyCell.setMinHeight(22);
                    emptyCell.setMaxHeight(22);

                    emptyCell.getStyleClass().add("booking-cell");

                    boolean isSelected =
                            selectedRoomNumber != null
                                    && selectedStartDate != null
                                    && room.getNumber() == selectedRoomNumber
                                    && !currentDate.isBefore(selectedStartDate)
                                    && (selectedEndDate == null
                                    || !currentDate.isAfter(selectedEndDate));

                    if (isSelected) {
                        emptyCell.getStyleClass().add("selected-cell");
                    }

                    emptyCell.setOnMouseClicked(event -> {

                        if (selectedBookingBlock != null) {
                            selectedBookingBlock.getStyleClass().remove("selected-block");
                            selectedBookingBlock = null;
                            selectedReservation = null;
                        }

                        LocalDate selectedDate = currentDate;

                        if (selectedStartDate == null || selectedRoomNumber == null) {

                            selectedRoomNumber = room.getNumber();
                            selectedStartDate = selectedDate;
                            selectedEndDate = null;

                        } else if (selectedRoomNumber != room.getNumber()) {

                            clearDateSelection();

                            selectedRoomNumber = room.getNumber();
                            selectedStartDate = selectedDate;

                        } else if (selectedEndDate == null) {

                            if (selectedDate.isBefore(selectedStartDate)) {
                                selectedEndDate = selectedStartDate;
                                selectedStartDate = selectedDate;
                            } else {
                                selectedEndDate = selectedDate;
                            }

                        } else {

                            clearDateSelection();

                            selectedRoomNumber = room.getNumber();
                            selectedStartDate = selectedDate;
                        }

                        updateDateSelection();
                    });

                    roomRow.getChildren().add(emptyCell);

                    day++;
                }
            }

            gridContainer.getChildren().add(roomRow);
        }
    }
    private void clearDateSelection() {

        selectedRoomNumber = null;
        selectedStartDate = null;
        selectedEndDate = null;

        for (var roomRow : gridContainer.getChildren()) {

            if (roomRow instanceof HBox hBox) {

                for (var node : hBox.getChildren()) {

                    node.getStyleClass().remove("selected-cell");
                }
            }
        }
    }

    private void updateDateSelection() {

        // Remove previous selection
        for (var rowNode : gridContainer.getChildren()) {

            if (!(rowNode instanceof HBox roomRow)) {
                continue;
            }

            for (var node : roomRow.getChildren()) {

                if (node instanceof VBox cell
                        && cell.getStyleClass().contains("booking-cell")) {

                    cell.getStyleClass().remove("selected-cell");
                }
            }
        }

        if (selectedRoomNumber == null || selectedStartDate == null) {
            return;
        }

        for (var rowNode : gridContainer.getChildren()) {

            if (!(rowNode instanceof HBox roomRow)) {
                continue;
            }

            Object roomData = roomRow.getUserData();

            if (!(roomData instanceof Integer roomNumber)) {
                continue;
            }

            if (roomNumber != selectedRoomNumber) {
                continue;
            }

            for (var node : roomRow.getChildren()) {

                if (!(node instanceof VBox cell)) {
                    continue;
                }

                if (!cell.getStyleClass().contains("booking-cell")) {
                    continue;
                }

                Object dateData = cell.getUserData();

                if (!(dateData instanceof LocalDate cellDate)) {
                    continue;
                }

                boolean insideSelection;

                if (selectedEndDate == null) {
                    insideSelection = cellDate.equals(selectedStartDate);
                } else {
                    insideSelection =
                            !cellDate.isBefore(selectedStartDate)
                                    && !cellDate.isAfter(selectedEndDate);
                }

                if (insideSelection) {
                    cell.getStyleClass().add("selected-cell");
                }
            }

            return;
        }
    }
}