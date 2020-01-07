package dreamers.caritaapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.cloudinary.android.MediaManager;

import java.util.HashMap;
import java.util.Map;

import dreamers.caritaapp.R;

public class SplashScreenActivity extends AppCompatActivity implements Animation.AnimationListener {

    Animation animBlink;

    ImageView logo;

    public String url = "http://192.168.1.10:8000/api/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

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
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        Intent i = new Intent(SplashScreenActivity.this, LoginActivity.class);
        startActivity(i);
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
