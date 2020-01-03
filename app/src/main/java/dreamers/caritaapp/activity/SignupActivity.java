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

import java.util.ArrayList;

import dreamers.caritaapp.R;
import dreamers.caritaapp.database.MySingleton;
import dreamers.caritaapp.database.SessionHandler;

public class SignupActivity extends AppCompatActivity {

    SessionHandler session;
    EditText text_fullname;
    EditText text_email;
    EditText text_username;
    EditText text_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        session = new SessionHandler(this);

        TextView btn_login = findViewById(R.id.btn_login);
        Button btn_signup = findViewById(R.id.btn_signup);

        text_fullname = findViewById(R.id.text_fullname);
        text_email = findViewById(R.id.text_email);
        text_username = findViewById(R.id.text_username);
        text_password = findViewById(R.id.text_password);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullname = text_fullname.getText().toString();
                String email = text_email.getText().toString();
                String username = text_username.getText().toString();
                String password = text_password.getText().toString();
                Boolean valid = true;

                if (fullname.matches("")) {
                    text_fullname.setError("Required");
                    valid = false;
                }
                if (email.matches("")) {
                    text_email.setError("Required");
                    valid = false;
                }
                if (username.matches("")) {
                    text_username.setError("Required");
                    valid = false;
                }
                if (password.matches("")) {
                    text_password.setError("Required");
                    valid = false;
                }

                if (valid)
                    register(fullname, email, username, password);

//                Intent i = new Intent(SignupActivity.this, SetUpActivity.class);
//                startActivity(i);
            }
        });
    }

    private void register(String fullname, String email, String username, String password) {
        String request = "register?email="+ email +"&password="+ password +"&name="+ fullname +"&password_confirmation="+ password +"&username="+ username;
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                new SplashScreenActivity().url+ request,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject res = new JSONObject(response);
                            if (res.has("errors")){
                                JSONArray errors = new JSONArray(res.getString("errors"));
                                for (int i = 0; i < errors.length(); i++) {
                                    switch (errors.get(i).toString().split(" ")[1]) {
                                        case "email":
                                            text_email.setError(errors.get(i).toString());
                                            break;
                                        case "fullname":
                                            text_fullname.setError(errors.get(i).toString());
                                            break;
                                        case "username":
                                            text_username.setError(errors.get(i).toString());
                                            break;
                                        case "password":
                                            text_password.setError(errors.get(i).toString());
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            }
                            else {
                                session.set_current_user(res.getInt("id"), res.getString("name"), res.getString("email"), res.getString("username"), res.getInt("points"), res.getString("photo"), "");
                                Toast.makeText(SignupActivity.this,
                                        "Successful!", Toast.LENGTH_LONG).show();
                                Intent i = new Intent(SignupActivity.this, SetUpActivity.class);
                                startActivity(i);
                            }
                        } catch (JSONException e) {
                            Toast.makeText(SignupActivity.this,
                                    "Something went wrong", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                    System.out.println(error);
                    Toast.makeText(SignupActivity.this,
                            "Something went wrong", Toast.LENGTH_LONG).show();
            }
        });
        MySingleton.getInstance(SignupActivity.this).addToRequestQueue(stringRequest);
    }
}
