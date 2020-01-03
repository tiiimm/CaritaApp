package dreamers.caritaapp.database;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;

public class User {
    Integer id;
    String name;
    String email;
    String username;
    Integer points;
    String photo;
    String role;
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

    public void setName(String Name) {
        this.name = Name;
    }

    public String getName() {
        return name;
    }

    public void setEmail(String Email) {
        this.email = Email;
    }

    public String getEmail() {
        return email;
    }

    public void setUsername(String Username) {
        this.username = Username;
    }

    public String getUsername() {
        return username;
    }

    public void setPoints(Integer Points) {
        this.points = Points;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPhoto(String Photo) {
        this.photo = Photo;
    }

    public String getPhoto() {
        return photo;
    }

    public void setRole(String Role) {
        this.role = Role;
    }

    public String getRole() {
        return role;
    }
}
