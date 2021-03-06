package dreamers.caritaapp.fragment.home;


import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import dreamers.caritaapp.R;
import dreamers.caritaapp.database.SessionHandler;
import dreamers.caritaapp.database.User;
import dreamers.caritaapp.fragment.home.home.HomeAchievementsFragment;
import dreamers.caritaapp.fragment.home.home.HomeCharitiesFragment;
import dreamers.caritaapp.fragment.home.home.HomeEventsFragment;

public class HomeFragment extends Fragment {

    View root;
    SessionHandler sessionHandler;
    User user;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);
        sessionHandler = new SessionHandler(getActivity());
        user = sessionHandler.getUserDetails();

        final TextView nav_charities = root.findViewById(R.id.nav_home_charities);
        final TextView nav_events = root.findViewById(R.id.nav_home_events);
        final TextView nav_achievements = root.findViewById(R.id.nav_home_achievements);
        TextView text_username = root.findViewById(R.id.text_username);
        TextView text_points = root.findViewById(R.id.text_points);

        if (user.getRole().matches("Charity")) {
            text_username.setText("Welcome, "+user.getOrganization()+"!");
        }
        else {
            text_username.setText("Welcome, @"+user.getUsername()+"!");
        }

        if (user.getRole().matches("Philanthropist"))
            text_points.setText("Points: "+user.getPoints());
        else text_points.setVisibility(View.GONE);

        getFragmentManager().beginTransaction().add(R.id.fragment3,new HomeCharitiesFragment()).commit();

        nav_charities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nav_charities.setBackgroundResource(R.drawable.background_selected);
                nav_charities.setTextColor(Color.WHITE);
                nav_events.setBackgroundResource(0);
                nav_events.setTextColor(Color.BLACK);
                nav_achievements.setBackgroundResource(0);
                nav_achievements.setTextColor(Color.BLACK);
                getFragmentManager().beginTransaction().replace(R.id.fragment3,new HomeCharitiesFragment()).commit();
            }
        });
        nav_events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nav_events.setBackgroundResource(R.drawable.background_selected);
                nav_events.setTextColor(Color.WHITE);
                nav_charities.setBackgroundResource(0);
                nav_charities.setTextColor(Color.BLACK);
                nav_achievements.setBackgroundResource(0);
                nav_achievements.setTextColor(Color.BLACK);
                getFragmentManager().beginTransaction().replace(R.id.fragment3,new HomeEventsFragment()).commit();
            }
        });
        nav_achievements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nav_achievements.setBackgroundResource(R.drawable.background_selected);
                nav_achievements.setTextColor(Color.WHITE);
                nav_charities.setBackgroundResource(0);
                nav_charities.setTextColor(Color.BLACK);
                nav_events.setBackgroundResource(0);
                nav_events.setTextColor(Color.BLACK);
                getFragmentManager().beginTransaction().replace(R.id.fragment3,new HomeAchievementsFragment()).commit();
            }
        });

        return root;
    }
}
