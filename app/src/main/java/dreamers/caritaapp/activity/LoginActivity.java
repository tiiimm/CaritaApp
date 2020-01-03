package dreamers.caritaapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import dreamers.caritaapp.R;
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

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(i);
            }
        });
    }
}
