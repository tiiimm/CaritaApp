package dreamers.caritaapp.fragment.home.settings;


import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import de.hdodenhof.circleimageview.CircleImageView;
import dreamers.caritaapp.R;
import dreamers.caritaapp.activity.SplashScreenActivity;
import dreamers.caritaapp.database.MySingleton;
import dreamers.caritaapp.database.SessionHandler;
import dreamers.caritaapp.database.User;
import dreamers.caritaapp.fragment.home.SettingsFragment;
import dreamers.caritaapp.fragment.home.settings.profile.ProfileAboutFragment;
import dreamers.caritaapp.fragment.home.settings.profile.ProfileAchievementsFragment;
import dreamers.caritaapp.fragment.home.settings.profile.ProfileEventsFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements RewardedVideoAdListener {

    View root;
    SessionHandler sessionHandler;
    User user;
    RewardedVideoAd mRewardedVideoAd;
    Bundle bundle;
    ImageView image_bio;
    VideoView video_bio;
    ImageView image_advertisement;
    VideoView video_advertisement;
    CircleImageView image_profile;
    RelativeLayout layout_advertisement;
    LinearLayout layout_profile;

    List<Integer> ads = new ArrayList<>();
    ArrayList<String> ads_content = new ArrayList<>();
    ArrayList<String> ads_type = new ArrayList<>();
    Integer ad;
    Integer position;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_profile, container, false);
        sessionHandler = new SessionHandler(getActivity());
        user = sessionHandler.getUserDetails();

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(getActivity());
        mRewardedVideoAd.setRewardedVideoAdListener(this);

        ImageView btn_back = root.findViewById(R.id.arrow_back);
        TextView text_name = root.findViewById(R.id.text_name);
        TextView text_username = root.findViewById(R.id.text_username);
        final TextView nav_profile_about = root.findViewById(R.id.nav_profile_about);
        final TextView nav_profile_achievements = root.findViewById(R.id.nav_profile_achievements);
        final TextView nav_profile_events = root.findViewById(R.id.nav_profile_events);
        image_bio = root.findViewById(R.id.image_charity_bio);
        image_profile = root.findViewById(R.id.image_profile_picture);
        video_bio = root.findViewById(R.id.video_charity_bio);
        video_advertisement = root.findViewById(R.id.video_advertisement);
        image_advertisement = root.findViewById(R.id.image_advertisement);
        Button btn_donate = root.findViewById(R.id.btn_donate);
        LinearLayout view_charity = root.findViewById(R.id.view_charity);
        layout_advertisement = root.findViewById(R.id.layout_advertisement);
        layout_profile = root.findViewById(R.id.layout_profile);
        final ProfileAboutFragment profileAboutFragment = new ProfileAboutFragment();

        ads.add(0);
        ads_content.add("");
        ads_type.add("");

        load_ads();

        bundle = getArguments();
        if (user.getID() != bundle.getInt("user_id")) {
            btn_donate.setVisibility(View.VISIBLE);
        }

        text_name.setText(bundle.getString("name"));
        text_username.setText(bundle.getString("username"));

        if (bundle.getString("role").matches("Charity")) {
            profileAboutFragment.setArguments(bundle);

            getFragmentManager().beginTransaction().replace(R.id.fragment4, profileAboutFragment).commit();
            configure();
        }
        else {
            view_charity.setVisibility(View.GONE);
            Glide.with(getActivity())
                    .asBitmap()
                    .load(MediaManager.get().url().generate("carita/default_bio"))
                    .into(image_bio);
            image_bio.setVisibility(View.VISIBLE);
            video_bio.setVisibility(View.GONE);
        }

        Glide.with(getActivity())
                .asBitmap()
                .load(MediaManager.get().url().generate(bundle.getString("photo")))
                .into(image_profile);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(new ProfileFragment()).commit();
                getFragmentManager().beginTransaction().replace(R.id.fragment2, new SettingsFragment()).commit();
            }
        });
        nav_profile_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nav_profile_about.setTextColor(Color.RED);
                nav_profile_achievements.setTextColor(Color.GRAY);
                nav_profile_events.setTextColor(Color.GRAY);

                profileAboutFragment.setArguments(bundle);

                getFragmentManager().beginTransaction().replace(R.id.fragment4, profileAboutFragment).commit();
            }
        });
        nav_profile_achievements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nav_profile_achievements.setTextColor(Color.RED);
                nav_profile_about.setTextColor(Color.GRAY);
                nav_profile_events.setTextColor(Color.GRAY);

                ProfileAchievementsFragment profileAchievementsFragment = new ProfileAchievementsFragment();
                profileAchievementsFragment.setArguments(bundle);

                getFragmentManager().beginTransaction().replace(R.id.fragment4, profileAchievementsFragment).commit();
            }
        });
        nav_profile_events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nav_profile_events.setTextColor(Color.RED);
                nav_profile_about.setTextColor(Color.GRAY);
                nav_profile_achievements.setTextColor(Color.GRAY);

                ProfileEventsFragment profileEventsFragment = new ProfileEventsFragment();
                profileEventsFragment.setArguments(bundle);

                getFragmentManager().beginTransaction().replace(R.id.fragment4, profileEventsFragment).commit();
            }
        });
        btn_donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ad == 0) {
                    if (mRewardedVideoAd.isLoaded()) {
                        mRewardedVideoAd.show();
                    }
                }
                else {
                    layout_profile.setVisibility(View.GONE);
                    layout_advertisement.setVisibility(View.VISIBLE);

                    donate("App\\Charity", "Company", ads.get(position));
                    if (ads_type.get(position).matches("video")) {
                        video_advertisement.start();
                    }
                }
            }
        });

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

    private void configure() {
        String request = "get_profile?user_id="+ bundle.getInt("user_id");
        System.out.println(request);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, new SplashScreenActivity().url+ request, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject res = new JSONObject(response);
                    if (res.getString("bio_path_type").matches("image")) {
                        Glide.with(getActivity())
                            .asBitmap()
                            .load(MediaManager.get().url().generate(res.getString("bio_path")))
                            .into(image_bio);
                        image_bio.setVisibility(View.VISIBLE);
                        video_bio.setVisibility(View.GONE);
                    }
                    else if (res.getString("bio_path_type").matches("video")) {
                        video_bio.setVideoURI(Uri.parse(MediaManager.get().url().resourceType("video").generate(res.getString("bio_path"))));
                        video_bio.setVisibility(View.VISIBLE);
                        image_bio.setVisibility(View.GONE);
                        video_bio.start();
                    }
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

    private void donate(String type, String ad_type, Integer ad_id) {
        String request = "donate?user_id="+ user.getID() +"&type="+ type +"&watch_id="+ bundle.getInt("user_id") +"&ad_id="+ ad_id +"&ad_type="+ ad_type;
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
        donate("App\\Charity", "Google", 0);
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
