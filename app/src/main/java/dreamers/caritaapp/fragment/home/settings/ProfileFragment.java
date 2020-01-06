package dreamers.caritaapp.fragment.home.settings;


import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import dreamers.caritaapp.R;
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

        text_name.setText(user.getName());
        text_username.setText("@"+user.getUsername());

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

                getFragmentManager().beginTransaction().replace(R.id.fragment4,new ProfileAboutFragment()).addToBackStack(null).commit();
            }
        });
        nav_profile_achievements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nav_profile_achievements.setTextColor(Color.RED);
                nav_profile_about.setTextColor(Color.GRAY);
                nav_profile_events.setTextColor(Color.GRAY);

                getFragmentManager().beginTransaction().replace(R.id.fragment4,new ProfileAchievementsFragment()).addToBackStack(null).commit();
            }
        });
        nav_profile_events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nav_profile_events.setTextColor(Color.RED);
                nav_profile_about.setTextColor(Color.GRAY);
                nav_profile_achievements.setTextColor(Color.GRAY);

                getFragmentManager().beginTransaction().replace(R.id.fragment4,new ProfileEventsFragment()).addToBackStack(null).commit();
            }
        });

        return root;
    }

}
