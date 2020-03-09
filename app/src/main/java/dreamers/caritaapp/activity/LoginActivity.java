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
import dreamers.caritaapp.database.User;

public class LoginActivity extends AppCompatActivity {

    SessionHandler sessionHandler;
    User user;

    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sessionHandler = new SessionHandler(this);
        user = sessionHandler.getUserDetails();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        TextView btn_signup = findViewById(R.id.btn_signup);

        final EditText text_email = findViewById(R.id.text_email);
        final EditText text_password = findViewById(R.id.text_password);

        Button btn_login = findViewById(R.id.btn_login);
        Button btn_login_google = findViewById(R.id.btn_login_google);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(i);
            }
        });
        btn_login_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 1);
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
                                                Toast.makeText(LoginActivity.this, "Email is already in use", Toast.LENGTH_SHORT).show();
                                                break;
                                            case "username":
                                                Toast.makeText(LoginActivity.this, "Username is already in use", Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(LoginActivity.this, "Wait for admin to approve your account!!", Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                    }
                                    if (res.has("company")) {
                                        JSONObject company = new JSONObject(res.getString("company"));
                                        if (!company.getString("status").matches("Active")) {
                                            Toast.makeText(LoginActivity.this, "Wait for admin to approve your account!!", Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                    }

                                    sessionHandler.set_current_user(res.getInt("id"), res.getString("name"), res.getString("email"), res.getString("username"), res.getInt("points"), photo, res.getString("role"), organization, res.getString("google_id"));
                                    Toast.makeText(LoginActivity.this,
                                            "Successful!", Toast.LENGTH_LONG).show();

                                    if (res.getString("role").split(" ")[0].matches("Temporary")) {
                                        Intent i = new Intent(LoginActivity.this, SetUpActivity.class);
                                        i.putExtra("role", res.getString("role"));
                                        startActivity(i);
                                    }
                                    else if (res.getString("role").matches("null") || res.getString("role").matches("")) {
                                        Intent i = new Intent(LoginActivity.this, SetUpActivity.class);
                                        startActivity(i);
                                    }
                                    else if (res.getString("role").matches("Company")) {
                                        Intent i = new Intent(LoginActivity.this, CompanyActivity.class);
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
        } catch (ApiException e) {
        }
    }

    private void login(String email, String password) {
        String request = "login?email="+ email +"&password="+ password;
        System.out.println(request);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, new SplashScreenActivity().url+ request, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                try {
                    JSONObject res = new JSONObject(response);
                    if (res.has("error")){
                        Toast.makeText(LoginActivity.this, res.getString("error"), Toast.LENGTH_LONG).show();
                    }
                    else {
                        String organization = "";
                        String photo = res.getString("photo");
                        if (res.has("charity")) {
                            JSONObject charity = new JSONObject(res.getString("charity"));
                            organization = charity.getString("organization");
                            photo = charity.getString("photo");
                            if (charity.getString("status").matches("Pending")) {
                                Toast.makeText(LoginActivity.this, "Wait for admin to approve your account!!", Toast.LENGTH_LONG).show();
                                return;
                            }
                            if (charity.getString("status").matches("Inactive")) {
                                Toast.makeText(LoginActivity.this, "Sorry. Admin has rejected your registration!! Try registering a new account", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                        if (res.has("company")) {
                            JSONObject company = new JSONObject(res.getString("company"));
                            if (company.getString("status").matches("Pending")) {
                                Toast.makeText(LoginActivity.this, "Wait for admin to approve your account!!", Toast.LENGTH_LONG).show();
                                return;
                            }
                            if (company.getString("status").matches("Inactive")) {
                                Toast.makeText(LoginActivity.this, "Sorry. Admin has rejected your registration!! Try registering a new account", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                        sessionHandler.set_current_user(res.getInt("id"), res.getString("name"), res.getString("email"), res.getString("username"), res.getInt("points"), photo, res.getString("role"), organization, res.getString("google_id"));
                        Toast.makeText(LoginActivity.this,
                                "Successful!", Toast.LENGTH_LONG).show();

                        if (res.getString("role").split(" ")[0].matches("Temporary")) {
                            Intent i = new Intent(LoginActivity.this, SetUpActivity.class);
                            i.putExtra("role", res.getString("role"));
                            startActivity(i);
                        }
                        else if (res.getString("role").matches("null") || res.getString("role").matches("")) {
                            Intent i = new Intent(LoginActivity.this, SetUpActivity.class);
                            startActivity(i);
                        }
                        else if (res.getString("role").matches("Company")) {
                            Intent i = new Intent(LoginActivity.this, CompanyActivity.class);
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

    @Override
    public void onBackPressed() {
    }
}
