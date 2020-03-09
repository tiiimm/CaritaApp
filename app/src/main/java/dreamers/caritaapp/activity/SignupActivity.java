package dreamers.caritaapp.activity;

import androidx.annotation.NonNull;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dreamers.caritaapp.R;
import dreamers.caritaapp.database.MySingleton;
import dreamers.caritaapp.database.SessionHandler;

public class SignupActivity extends AppCompatActivity {

    SessionHandler sessionHandler;
    EditText text_fullname;
    EditText text_email;
    EditText text_username;
    EditText text_password;

    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        sessionHandler = new SessionHandler(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        TextView btn_login = findViewById(R.id.btn_login);
        Button btn_signup = findViewById(R.id.btn_signup);
        Button btn_signup_google = findViewById(R.id.btn_signup_google);

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
        btn_signup_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 1);
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
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 1) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String password = "12345678";
            String request = "google_signup?email="+ account.getEmail() +"&password="+ password +"&name="+ account.getDisplayName() +"&password_confirmation="+ password +"&username="+ account.getDisplayName() +"&photo=carita/profile_picture&google_id="+ account.getId();
            System.out.println(request);
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
            new SplashScreenActivity().url+ request,
            new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    System.out.println(response);
                    try {
                        JSONObject res = new JSONObject(response);
                        if (res.has("errors")){
                            JSONArray errors = new JSONArray(res.getString("errors"));
                            for (int i = 0; i < errors.length(); i++) {
                                switch (errors.get(i).toString().split(" ")[1]) {
                                    case "email":
                                        Toast.makeText(SignupActivity.this, "Email is already in use", Toast.LENGTH_SHORT).show();
                                        break;
                                    case "username":
                                        Toast.makeText(SignupActivity.this, "Username is already in use", Toast.LENGTH_SHORT).show();
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                        else {
                            String organization = "";
                            String photo = res.getString("photo");
                            if (res.has("charity")) {
                                JSONObject charity = new JSONObject(res.getString("charity"));
                                organization = charity.getString("organization");
                                photo = charity.getString("photo");
                                if (!charity.getString("status").matches("Active")) {
                                    Toast.makeText(SignupActivity.this, "Wait for admin to approve your account!!", Toast.LENGTH_LONG).show();
                                    return;
                                }
                            }
                            if (res.has("company")) {
                                JSONObject company = new JSONObject(res.getString("company"));
                                if (!company.getString("status").matches("Active")) {
                                    Toast.makeText(SignupActivity.this, "Wait for admin to approve your account!!", Toast.LENGTH_LONG).show();
                                    return;
                                }
                            }

                            sessionHandler.set_current_user(res.getInt("id"), res.getString("name"), res.getString("email"), res.getString("username"), res.getInt("points"), photo, res.getString("role"), organization, res.getString("google_id"));
                            Toast.makeText(SignupActivity.this,
                                    "Successful!", Toast.LENGTH_LONG).show();

                            if (res.getString("role").split(" ")[0].matches("Temporary")) {
                                Intent i = new Intent(SignupActivity.this, SetUpActivity.class);
                                i.putExtra("role", res.getString("role"));
                                startActivity(i);
                            }
                            else if (res.getString("role").matches("null") || res.getString("role").matches("")) {
                                Intent i = new Intent(SignupActivity.this, SetUpActivity.class);
                                startActivity(i);
                            }
                            else if (res.getString("role").matches("Company")) {
                                Intent i = new Intent(SignupActivity.this, CompanyActivity.class);
                                startActivity(i);
                            }
                            else {
                                Intent i = new Intent(SignupActivity.this, HomeActivity.class);
                                startActivity(i);
                            }
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
        } catch (ApiException e) {
        }
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
                                sessionHandler.set_current_user(res.getInt("id"), res.getString("name"), res.getString("email"), res.getString("username"), res.getInt("points"), res.getString("photo"), "", "", "");
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

    @Override
    public void onBackPressed() {
    }
}
