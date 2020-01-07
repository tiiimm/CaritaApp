package dreamers.caritaapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dreamers.caritaapp.R;
import dreamers.caritaapp.database.MySingleton;
import dreamers.caritaapp.database.SessionHandler;
import dreamers.caritaapp.database.User;

public class LoginActivity extends AppCompatActivity {

    SessionHandler session;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        session = new SessionHandler(this);
        user = session.getUserDetails();

        try {
            if (user.getRole().matches("")) {
                Intent i = new Intent(LoginActivity.this, SetUpActivity.class);
                startActivity(i);
            }
            else {
                Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(i);
            }
        }
        catch (Exception e) {

        }

        TextView btn_signup = findViewById(R.id.btn_signup);

        final EditText text_email = findViewById(R.id.text_email);
        final EditText text_password = findViewById(R.id.text_password);

        Button btn_login = findViewById(R.id.btn_login);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(i);
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = text_email.getText().toString();
                String password = text_password.getText().toString();
                Boolean valid = true;

                if (email.matches("")) {
                    text_email.setError("Required");
                    valid = false;
                }
                if (password.matches("")) {
                    text_password.setError("Required");
                    valid = false;
                }

                if (valid)
                    login(email, password);
            }
        });
    }

    private void login(String email, String password) {
        String request = "login?email="+ email +"&password="+ password;
        System.out.println(request);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, new SplashScreenActivity().url+ request, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject res = new JSONObject(response);
                    if (res.has("error")){
                        Toast.makeText(LoginActivity.this, res.getString("error"), Toast.LENGTH_SHORT).show();
                    }
                    else {
                        String organization = "";
                        String photo = res.getString("photo");
                        if (res.has("charity")) {
                            JSONObject charity = new JSONObject(res.getString("charity"));
                            organization = charity.getString("organization");
                            photo = charity.getString("photo");
                        }
                        session.set_current_user(res.getInt("id"), res.getString("name"), res.getString("email"), res.getString("username"), res.getInt("points"), photo, res.getString("role"), organization);
                        Toast.makeText(LoginActivity.this,
                                "Successful!", Toast.LENGTH_LONG).show();
                        if (res.getString("role") == "null") {
                            Intent i = new Intent(LoginActivity.this, SetUpActivity.class);
                            startActivity(i);
                        }
                        else {
                            Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(i);
                        }
                    }
                } catch (JSONException e) {
                    Toast.makeText(LoginActivity.this,
                            "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
                Toast.makeText(LoginActivity.this,
                    "Something went wrong", Toast.LENGTH_LONG).show();
            }
        });
        MySingleton.getInstance(LoginActivity.this).addToRequestQueue(stringRequest);
    }
}
