package dreamers.caritaapp.database;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;

public class User {
    Integer id;
    Integer room_id;
    String time_in;
    String time_out;
    Integer session_hours;
    Date sessionExpiryDate;

    public void setSessionExpiryDate(Date sessionExpiryDate) {
        this.sessionExpiryDate = sessionExpiryDate;
    }

    public Date getSessionExpiryDate() {
        return sessionExpiryDate;
    }

    public void setID(Integer ID) {
        this.id = ID;
    }

    public Integer getID() {
        return id;
    }

    public void setRoomID(Integer RoomID) {
        this.room_id = RoomID;
    }

    public Integer getRoomID() {
        return room_id;
    }

    public void setTimeIn(String TimeIn) {
        this.time_in = TimeIn;
    }

    public String getTimeIn() {
        return time_in;
    }

    public void setTimeOut(String TimeOut) {
        this.time_out = TimeOut;
    }

    public String getTimeOut() {
        return time_out;
    }

    public void setSessionHours(Integer SessionHours) {
        this.session_hours = SessionHours;
    }

    public Integer getSessionHours() {
        return session_hours;
    }
}
