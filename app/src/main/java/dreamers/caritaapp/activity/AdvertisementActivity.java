package dreamers.caritaapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import dreamers.caritaapp.R;
import dreamers.caritaapp.database.MySingleton;
import dreamers.caritaapp.database.SessionHandler;
import dreamers.caritaapp.database.User;
import dreamers.caritaapp.fragment.home.settings.ProfileFragment;
import dreamers.caritaapp.fragment.home.settings.others.EventFragment;
import dreamers.caritaapp.fragment.home.settings.profile.ProfileAchievementsFragment;

public class AdvertisementActivity extends AppCompatActivity {

    SessionHandler sessionHandler;
    User user;

    ImageView image_advertisement;
    VideoView video_advertisement;
    RelativeLayout layout_advertisement;

    Bundle bundle;
    String ad_id;
    String ad_content;
    String ad_type;
    String watch_id;
    String watch_type;
    CountDownTimer countDownTimer;

    long milli;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertisement);
        sessionHandler = new SessionHandler(this);
        user = sessionHandler.getUserDetails();

        video_advertisement = findViewById(R.id.video_advertisement);
        image_advertisement = findViewById(R.id.image_advertisement);
        layout_advertisement = findViewById(R.id.layout_advertisement);
        FloatingActionButton btn_exit_ad = findViewById(R.id.btn_exit_ad);

        btn_exit_ad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();
                finish();
            }
        });

        start_timer(15000);

        configure();
    }

    private void start_timer(long leftmilli) {
        countDownTimer = new CountDownTimer(leftmilli, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                System.out.println(millisUntilFinished);
                milli = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                donate();
                System.out.println("FINISHED!");
            }
        };
    }

    private void configure() {
        try {
            Intent i = getIntent();
            bundle = i.getBundleExtra("bundle");
            ad_id = i.getStringExtra("ad_id");
            ad_content = i.getStringExtra("ad_content");
            ad_type = i.getStringExtra("ad_type");
            watch_id = i.getStringExtra("watch_id");
            watch_type = i.getStringExtra("watch_type");
            if (ad_type.matches("video")) {
                video_advertisement.setVisibility(View.VISIBLE);
                image_advertisement.setVisibility(View.GONE);
                video_advertisement.setVideoURI(Uri.parse(MediaManager.get().url().resourceType("video").generate(ad_content)));
                video_advertisement.start();
                countDownTimer.start();
            }
            else if (ad_type.matches("image")) {
                image_advertisement.setVisibility(View.VISIBLE);
                video_advertisement.setVisibility(View.GONE);
                Glide.with(AdvertisementActivity.this)
                        .asBitmap()
                        .load(MediaManager.get().url().generate(ad_content))
                        .into(image_advertisement);
                countDownTimer.start();
            }
        }
        catch (Exception e) {}
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        countDownTimer.cancel();
        finish();
    }

    private void donate() {
        String request = "donate?user_id="+ user.getID() +"&type=App\\"+ watch_type +"&watch_id="+ watch_id +"&ad_id="+ ad_id +"&ad_type=Company";
        System.out.println(request);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, new SplashScreenActivity().url+ request, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                try {
                    JSONObject res = new JSONObject(response);
                    sessionHandler.donate(res.getInt("points"));
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
                Toast.makeText(AdvertisementActivity.this,
                        "Something went wrong", Toast.LENGTH_LONG).show();
            }
        });
        MySingleton.getInstance(AdvertisementActivity.this).addToRequestQueue(stringRequest);
    }
}
