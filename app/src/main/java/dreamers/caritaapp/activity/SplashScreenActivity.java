package dreamers.caritaapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.cloudinary.android.MediaManager;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.HashMap;
import java.util.Map;

import dreamers.caritaapp.R;
import dreamers.caritaapp.database.SessionHandler;
import dreamers.caritaapp.database.User;

public class SplashScreenActivity extends AppCompatActivity implements Animation.AnimationListener {

    SessionHandler session;
    User user;

    Animation animBlink;

    ImageView logo;

    public String url = "http://192.168.42.224:8000/api/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        session = new SessionHandler(this);
        user = session.getUserDetails();

        logo = findViewById(R.id.image_logo);

        animBlink = AnimationUtils.loadAnimation(this,
                R.anim.blink);

        animBlink.setAnimationListener(this);

        logo.startAnimation(animBlink);


        try {
            Map config = new HashMap();
            config.put("cloud_name", "tim0923");
            config.put("api_key", "654321224285841");
            config.put("api_secret", "8-rsCikyiUiLlD82_wTF6cyoYYo");
            MediaManager.init(this, config);
        }
        catch (Exception e){
            System.out.println(e);
        }

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                try {
                    System.out.println(user.getRole());
                    if (user.getRole().matches("") || user.getRole().matches("null")) {
                        Intent i = new Intent(SplashScreenActivity.this, SetUpActivity.class);
                        startActivity(i);
                    }
                    else {
                        Intent i = new Intent(SplashScreenActivity.this, HomeActivity.class);
                        startActivity(i);
                    }
                }
                catch (Exception e) {
                    Intent i = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    startActivity(i);
                }
            }
        });
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        // Take any action after completing the animation

        // check for blink animation
        if (animation == animBlink) {
        }

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void onAnimationStart(Animation animation) {

    }
}
