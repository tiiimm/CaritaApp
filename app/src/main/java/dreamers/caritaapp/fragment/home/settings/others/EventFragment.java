package dreamers.caritaapp.fragment.home.settings.others;


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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dreamers.caritaapp.R;
import dreamers.caritaapp.activity.SplashScreenActivity;
import dreamers.caritaapp.database.MySingleton;
import dreamers.caritaapp.database.SessionHandler;
import dreamers.caritaapp.database.User;

public class EventFragment extends Fragment implements RewardedVideoAdListener {

    View root;
    SessionHandler sessionHandler;
    User user;
    Bundle bundle;
    RewardedVideoAd mRewardedVideoAd;

    ImageView image_event;
    ImageView image_advertisement;
    VideoView video_advertisement;
    TextView text_title;
    TextView text_description;
    TextView text_date;
    TextView text_venue;
    TextView text_open_until;

    RelativeLayout layout_advertisement;
    LinearLayout layout_event;

    List<Integer> ads = new ArrayList<>();
    ArrayList<String> ads_content = new ArrayList<>();
    ArrayList<String> ads_type = new ArrayList<>();
    Integer ad;
    Integer position;

    public EventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_event, container, false);
        sessionHandler = new SessionHandler(getActivity());
        user = sessionHandler.getUserDetails();

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(getActivity());
        mRewardedVideoAd.setRewardedVideoAdListener(this);

        image_event = root.findViewById(R.id.image_event);
        text_title = root.findViewById(R.id.text_title);
        text_description = root.findViewById(R.id.text_description);
        text_date = root.findViewById(R.id.text_date);
        text_venue = root.findViewById(R.id.text_venue);
        text_open_until = root.findViewById(R.id.text_open_until);
        video_advertisement = root.findViewById(R.id.video_advertisement);
        image_advertisement = root.findViewById(R.id.image_advertisement);
        layout_advertisement = root.findViewById(R.id.layout_advertisement);
        layout_event = root.findViewById(R.id.layout_event);

        ads.add(0);
        ads_content.add("");
        ads_type.add("");

        load_ads();

        bundle = getArguments();

        Button btn_donate = root.findViewById(R.id.btn_donate);

        btn_donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (ad == 0) {
                if (mRewardedVideoAd.isLoaded()) {
                    mRewardedVideoAd.show();
                }
            }
            else {
                layout_event.setVisibility(View.GONE);
                layout_advertisement.setVisibility(View.VISIBLE);

                donate("App\\CharityEvent", "Company", ads.get(position));
                if (ads_type.get(position).matches("video")) {
                    video_advertisement.start();
                }
            }
            }
        });

        configure();

        return root;
    }

    private void load_ads() {
        String request = "get_advertisements";
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
        System.out.println("position" + position);
        ad = ads.get(position);
        System.out.println("ad" + ad);
        if (ad == 0) {
            loadRewardedVideoAd();
        }
        else {
            load_selected_add(position);
        }
    }

    private void load_selected_add(Integer position) {
        System.out.println(ads_type.get(position));
        if (ads_type.get(position).matches("video")) {
            video_advertisement.setVisibility(View.VISIBLE);
            image_advertisement.setVisibility(View.GONE);
            video_advertisement.setVideoURI(Uri.parse(MediaManager.get().url().resourceType("video").generate(ads_content.get(position))));
        }
        else if (ads_type.get(position).matches("image")) {
            image_advertisement.setVisibility(View.VISIBLE);
            video_advertisement.setVisibility(View.GONE);
            Glide.with(getActivity())
                    .asBitmap()
                    .load(MediaManager.get().url().generate(ads_content.get(position)))
                    .into(image_advertisement);
        }
    }

    private void donate(String type, String ad_type, Integer ad_id) {
        String request = "donate?user_id="+ user.getID() +"&type="+ type +"&watch_id="+ bundle.getString("event_id") +"&ad_id="+ ad_id +"&ad_type="+ ad_type;
        System.out.println(request);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, new SplashScreenActivity().url+ request, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
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

    private void configure() {
        if (!bundle.getString("event_id").matches("")) {
            Glide.with(getActivity()).asBitmap().load(MediaManager.get().url().generate(bundle.getString("photo"))).into(image_event);
            text_title.setText(bundle.getString("title"));
            text_description.setText(bundle.getString("description"));
            text_date.setText(bundle.getString("date"));
            text_venue.setText(bundle.getString("venue"));
            text_open_until.setText(bundle.getString("open_until"));
        }
    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                new AdRequest.Builder().build());
    }

    @Override
    public void onRewardedVideoAdLoaded() {

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        pick_ads();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        donate("App\\CharityEvent", "Google", 0);
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
