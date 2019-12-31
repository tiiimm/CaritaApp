package dreamers.caritaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreenActivity extends AppCompatActivity implements Animation.AnimationListener {

    Animation animBlink;

    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        logo = findViewById(R.id.image_logo);

        animBlink = AnimationUtils.loadAnimation(this,
                R.anim.blink);

        animBlink.setAnimationListener(this);

        logo.startAnimation(animBlink);


//        try {
//            Map config = new HashMap();
//            config.put("cloud_name", "tim0923");
//            MediaManager.init(this, config);
//        }
//        catch (Exception e){
//            System.out.println(e);
//        }
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
