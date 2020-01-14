package dreamers.caritaapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import dreamers.caritaapp.R;
import dreamers.caritaapp.database.SessionHandler;
import dreamers.caritaapp.fragment.company.CompanyAdvertisements;
import dreamers.caritaapp.fragment.home.SettingsFragment;

public class CompanyActivity extends AppCompatActivity {

    SessionHandler sessionHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company);
        sessionHandler = new SessionHandler(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        final GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

//        final ImageView nav_points = findViewById(R.id.nav_points);
        final ImageView nav_advertisements = findViewById(R.id.nav_advertisements);
        final ImageView nav_settings = findViewById(R.id.nav_settings);
        ImageView nav_logout = findViewById(R.id.nav_logout);

        nav_advertisements.setImageResource(R.drawable.drawable_charities_selected);
//        nav_points.setImageResource(R.drawable.drawable_points);
        nav_settings.setImageResource(R.drawable.drawable_settings);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_company,new CompanyAdvertisements()).commit();

        nav_advertisements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nav_advertisements.setImageResource(R.drawable.drawable_charities_selected);
//                nav_points.setImageResource(R.drawable.drawable_points);
                nav_settings.setImageResource(R.drawable.drawable_settings);
                getSupportFragmentManager().getFragments().clear();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_company,new CompanyAdvertisements()).commit();
            }
        });
////        nav_points.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                nav_points.setImageResource(R.drawable.drawable_points_selected);
//                nav_advertisements.setImageResource(R.drawable.drawable_charities);
//                nav_settings.setImageResource(R.drawable.drawable_settings);
//                getSupportFragmentManager().getFragments().clear();
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_company,new PointsFragment()).commit();
//            }
//        });
        nav_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nav_settings.setImageResource(R.drawable.drawable_settings_selected);
                nav_advertisements.setImageResource(R.drawable.drawable_charities);
//                nav_points.setImageResource(R.drawable.drawable_points);
                getSupportFragmentManager().getFragments().clear();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_company,new SettingsFragment()).commit();
            }
        });
        nav_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionHandler.logoutUser();
                mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        System.out.println("Signed out");
                    }
                });
                Intent i = new Intent(CompanyActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
    }
}
