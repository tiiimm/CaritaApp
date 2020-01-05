package dreamers.caritaapp.database;

import android.content.Context;
import android.content.SharedPreferences;

//import com.example.caritaandroid.models.User;

import java.util.Date;

public class SessionHandler {
    private Context mContext;
    private SharedPreferences.Editor mEditor;
    private SharedPreferences mPreferences;

    public SessionHandler(Context mContext) {
        this.mContext = mContext;
        mPreferences = mContext.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        this.mEditor = mPreferences.edit();
    }

    public void set_current_user(Integer id, String name, String email, String username, Integer points, String photo, String role) {
        mEditor.putInt("id", id);
        mEditor.putString("name", name);
        mEditor.putString("email", email);
        mEditor.putString("username", username);
        mEditor.putInt("points", points);
        mEditor.putString("photo", photo);
        mEditor.putString("role", role);

        Date date = new Date();
        long millis = date.getTime() + (7 * 24 * 60 * 60 * 1000);
        mEditor.putLong("expires", millis);
        mEditor.commit();
    }

    public User getUserDetails() {
        if (!isLoggedIn()) {
            return null;
        }
        User user = new User();
        user.setID(mPreferences.getInt("id", 0));
        user.setName(mPreferences.getString("name", ""));
        user.setEmail(mPreferences.getString("email", ""));
        user.setUsername(mPreferences.getString("username", ""));
        user.setPoints(mPreferences.getInt("points", 0));
        user.setPhoto(mPreferences.getString("photo", ""));
        user.setRole(mPreferences.getString("role", ""));
        user.setSessionExpiryDate(new Date(mPreferences.getLong("expires", 0)));

        return user;
    }

    public void set_up_charity() {
        mEditor.putString("role", "Charity");
        mEditor.commit();
    }

    public boolean isLoggedIn() {
        Date currentDate = new Date();

        long millis = mPreferences.getLong("expires", 0);
        if (millis == 0) {
            return false;
        }
        Date expiryDate = new Date(millis);

        return currentDate.before(expiryDate);
    }

    public void logoutUser(){
        mEditor.clear();
        mEditor.commit();
    }
}
