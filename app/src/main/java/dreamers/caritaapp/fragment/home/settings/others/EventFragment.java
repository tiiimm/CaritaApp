package dreamers.caritaapp.fragment.home.settings.others;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import dreamers.caritaapp.R;
import dreamers.caritaapp.activity.AdvertisementActivity;
import dreamers.caritaapp.activity.SplashScreenActivity;
import dreamers.caritaapp.database.MySingleton;
import dreamers.caritaapp.database.SessionHandler;
import dreamers.caritaapp.database.User;
import dreamers.caritaapp.fragment.home.settings.ProfileFragment;

public class EventFragment extends Fragment implements RewardedVideoAdListener {

    View root;
    SessionHandler sessionHandler;
    User user;
    Bundle bundle;
    RewardedVideoAd mRewardedVideoAd;

    Button btn_donate;
    ImageView image_event;
    TextView text_title;
    TextView text_description;
    TextView text_date;
    TextView text_venue;
    TextView text_open_until;
    TextView text_points;

    LinearLayout layout_event;

    List<Integer> ads = new ArrayList<>();
    ArrayList<String> ads_content = new ArrayList<>();
    ArrayList<String> ads_type = new ArrayList<>();
    Integer ad;
    Integer position;
    Integer watch_count;
    Integer user_points;

    ProgressDialog progressDialog;

    public EventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_event, container, false);
        sessionHandler = new SessionHandler(getActivity());
        user = sessionHandler.getUserDetails();
        user_points = user.getPoints();

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(getActivity());
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");

        loadRewardedVideoAd();

        image_event = root.findViewById(R.id.image_event);
        text_title = root.findViewById(R.id.text_title);
        text_description = root.findViewById(R.id.text_description);
        text_date = root.findViewById(R.id.text_date);
        text_venue = root.findViewById(R.id.text_venue);
        text_open_until = root.findViewById(R.id.text_open_until);
        text_points = root.findViewById(R.id.text_points);
        layout_event = root.findViewById(R.id.layout_event);

        ads.add(0);
        ads_content.add("");
        ads_type.add("");

        load_ads();

        bundle = getArguments();

        btn_donate = root.findViewById(R.id.btn_donate);

        btn_donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (watch_count>=10) {
                    Toast.makeText(getActivity(), "You're reached your watch limit for the day", Toast.LENGTH_LONG).show();
                    return;
                }
                if (ads.get(position).equals(0)) {
                    if (mRewardedVideoAd.isLoaded()) {
                        mRewardedVideoAd.show();
                    }
                }
                else {
                    Intent i = new Intent(getActivity(), AdvertisementActivity.class);
                    i.putExtra("ad_id", ads.get(position).toString());
                    i.putExtra("ad_content", ads_content.get(position));
                    i.putExtra("ad_type", ads_type.get(position));
                    i.putExtra("watch_id", bundle.getString("event_id"));
                    i.putExtra("watch_type", "CharityEvent");
                    i.putExtra("bundle", bundle);

                    getFragmentManager().beginTransaction().remove(new EventFragment()).commit();
                    startActivity(i);
                }
            }
        });

        configure();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        pick_ads();
        user = sessionHandler.getUserDetails();
        System.out.println(user_points);
        System.out.println(user.getPoints());
        Integer blah = user.getPoints() - user_points;
        if (!blah.equals(0)) {
            Integer pts = Integer.parseInt(bundle.getString("points"));
            pts+=blah;
            text_points.setText("Supports Received: "+pts);
        }
    }

    private void configure() {
        if (!bundle.getString("event_id").matches("")) {
            Glide.with(getActivity()).asBitmap().load(MediaManager.get().url().generate(bundle.getString("photo"))).into(image_event);
            text_title.setText(bundle.getString("title"));
            text_description.setText(bundle.getString("description"));
            text_points.setText("Supports Received: "+bundle.getString("points"));
            text_date.setText(bundle.getString("date"));
            text_venue.setText(bundle.getString("venue"));
            text_open_until.setText("Accepting Donations Until: "+bundle.getString("open_until"));

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date strDate = null;
            try {
                System.out.println(bundle.getString("open_until"));
                strDate = simpleDateFormat.parse(bundle.getString("open_until"));
                System.out.println(strDate);
                if (System.currentTimeMillis() > strDate.getTime()) {
                    btn_donate.setVisibility(View.GONE);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (bundle.getString("user_id").matches(String.valueOf(user.getID()))) {
                btn_donate.setVisibility(View.GONE);
            }
            get_watch_count();
        }
    }

    private void get_watch_count() {
        String request = "watch_count?user_id="+ user.getID();
        System.out.println(request);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, new SplashScreenActivity().url+ request, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject res = new JSONObject(response);
                    watch_count = res.getInt("watch_count");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
                Toast.makeText(getActivity(),
                        "Something went wrong", Toast.LENGTH_LONG).show();
            }
        });
        MySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    private void load_ads() {
        String request = "get_active_advertisements";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, new SplashScreenActivity().url+ request, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray res = new JSONArray(response);
                    for (int i = 0; i<res.length(); i++) {
                        JSONObject advertisement = res.getJSONObject(i);
                        ads.add(advertisement.getInt("id"));
                        ads_content.add(advertisement.getString("advertisement"));
                        ads_type.add(advertisement.getString("advertisement_type"));
                    }
                    pick_ads();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
                Toast.makeText(getActivity(),
                        "Something went wrong", Toast.LENGTH_LONG).show();
            }
        });
        MySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    private void pick_ads() {
        Random rand = new Random();
        position = rand.nextInt(ads.size());
        System.out.println("HALULUUKHS "+position);
    }

    private void donate() {
        String request = "donate?user_id="+ user.getID() +"&type=App\\CharityEvent&watch_id="+ bundle.getString("event_id") +"&ad_id=0&ad_type=Google";
        System.out.println(request);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, new SplashScreenActivity().url+ request, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                try {
                    JSONObject res = new JSONObject(response);
                    sessionHandler.donate(res.getInt("points"));
                    Integer points = Integer.parseInt(text_points.getText().toString().split(" ")[2]);
                    points++;
                    text_points.setText("Supports Received: "+points);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
                Toast.makeText(getActivity(),
                        "Something went wrong", Toast.LENGTH_LONG).show();
            }
        });
        MySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    private void loadRewardedVideoAd() {
        progressDialog.show();
        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                new AdRequest.Builder().build());
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        progressDialog.dismiss();
    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        loadRewardedVideoAd();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        loadRewardedVideoAd();
        donate();
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }

    @Override
    public void onRewardedVideoCompleted() {

    }
}
