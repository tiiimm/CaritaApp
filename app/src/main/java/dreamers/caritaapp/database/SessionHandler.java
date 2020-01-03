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

    public void set_current_user(Integer room_id, Integer id, String time_in, String time_out, Integer session_hours) {
        mEditor.putInt("room_id", room_id);
        mEditor.putInt("id", id);
        mEditor.putString("time_in", time_in);
        mEditor.putString("time_out", time_out);
        mEditor.putInt("session_hours", session_hours);

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
        user.setRoomID(mPreferences.getInt("room_id", 0));
        user.setTimeIn(mPreferences.getString("time_in", ""));
        user.setTimeOut(mPreferences.getString("time_out", ""));
        user.setSessionHours(mPreferences.getInt("session_hours", 0));
        user.setSessionExpiryDate(new Date(mPreferences.getLong("expires", 0)));

        return user;
    }

    public void add_to_cart(String cart) {
        mEditor.putString("cart", cart);
        mEditor.commit();
    }

    public void remove_from_cart(String cart) {
        mEditor.putString("cart", cart);
        mEditor.commit();
    }

    public void clear_cart() {
        mEditor.putString("cart", "");
        mEditor.commit();
    }

    public String get_cart() {
        return mPreferences.getString("cart", "");
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
