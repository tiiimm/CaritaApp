package dreamers.caritaapp.fragment.home.settings;


import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;

import org.json.JSONException;
import org.json.JSONObject;

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
public class ProfileFragment extends Fragment {

    View root;
    SessionHandler sessionHandler;
    User user;
    ImageView image_bio;
    ImageView image_profile;
    VideoView video_bio;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_profile, container, false);
        sessionHandler = new SessionHandler(getActivity());
        user = sessionHandler.getUserDetails();

        ImageView btn_back = root.findViewById(R.id.arrow_back);
        TextView text_name = root.findViewById(R.id.text_name);
        TextView text_username = root.findViewById(R.id.text_username);
        final TextView nav_profile_about = root.findViewById(R.id.nav_profile_about);
        final TextView nav_profile_achievements = root.findViewById(R.id.nav_profile_achievements);
        final TextView nav_profile_events = root.findViewById(R.id.nav_profile_events);
        image_bio = root.findViewById(R.id.image_charity_bio);
        image_profile = root.findViewById(R.id.image_profile_picture);
        video_bio = root.findViewById(R.id.video_charity_bio);

        Glide.with(getActivity())
                .asBitmap()
                .load(MediaManager.get().url().generate(user.getPhoto()))
                .into(image_profile);

        if (user.getRole().matches("Philanthropist")) {
            text_name.setText(user.getName());
            text_username.setText("@"+user.getUsername());
        }
        else if (user.getRole().matches("Charity")) {
            text_name.setText(user.getOrganization());
            text_username.setText("");
        }

        configure();

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

                getFragmentManager().beginTransaction().replace(R.id.fragment4,new ProfileAboutFragment()).commit();
            }
        });
        nav_profile_achievements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nav_profile_achievements.setTextColor(Color.RED);
                nav_profile_about.setTextColor(Color.GRAY);
                nav_profile_events.setTextColor(Color.GRAY);

                getFragmentManager().beginTransaction().replace(R.id.fragment4,new ProfileAchievementsFragment()).commit();
            }
        });
        nav_profile_events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nav_profile_events.setTextColor(Color.RED);
                nav_profile_about.setTextColor(Color.GRAY);
                nav_profile_achievements.setTextColor(Color.GRAY);

                getFragmentManager().beginTransaction().replace(R.id.fragment4,new ProfileEventsFragment()).commit();
            }
        });

        return root;
    }

    private void configure() {
        String request = "get_profile?user_id="+ user.getID();
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
}
