package models;

public class ReservationRoom {

  private int idReservation;
  private int idRoom;
  private int roomNumber;

  public ReservationRoom() {
  }

  public ReservationRoom(int idReservation, int idRoom) {
    this.idReservation = idReservation;
    this.idRoom = idRoom;
  }

  public int getIdReservation() {
    return idReservation;
  }

  public void setIdReservation(int idReservation) {
    this.idReservation = idReservation;
  }

  public int getIdRoom() {
    return idRoom;
  }

  public void setIdRoom(int idRoom) {
    this.idRoom = idRoom;
  }

  public void setRoomNumber(int roomNumber) {
    this.roomNumber = roomNumber;
  }

  public int getRoomNumber() {
    return roomNumber;
  }

  @Override
  public String toString() {
    return "ReservationRoom{" +
        "idReservation=" + idReservation +
        ", idRoom=" + idRoom +
        '}';
  }
}
