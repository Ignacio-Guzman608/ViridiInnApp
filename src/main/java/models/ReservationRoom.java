package models;

public class ReservationRoom {

    private int idReservation;
    private int idRoom;

    public ReservationRoom() {}

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

    @Override
    public String toString() {
        return "ReservationRoom{" +
                "idReservation=" + idReservation +
                ", idRoom=" + idRoom +
                '}';
    }
}
