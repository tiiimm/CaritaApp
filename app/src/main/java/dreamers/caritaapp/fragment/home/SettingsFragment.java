package dreamers.caritaapp.fragment.home;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;

import de.hdodenhof.circleimageview.CircleImageView;
import dreamers.caritaapp.R;
import dreamers.caritaapp.database.SessionHandler;
import dreamers.caritaapp.database.User;
import dreamers.caritaapp.fragment.admin.CompaniesFragment;
import dreamers.caritaapp.fragment.admin.PhilanthropistsFragment;
import dreamers.caritaapp.fragment.home.settings.OwnAchievementsFragment;
import dreamers.caritaapp.fragment.home.settings.ProfileFragment;
import dreamers.caritaapp.fragment.home.settings.OwnEventsFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    View root;
    SessionHandler sessionHandler;
    User user;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_settings, container, false);
        sessionHandler = new SessionHandler(getActivity());
        user = sessionHandler.getUserDetails();

        CircleImageView image_profile_picture = root.findViewById(R.id.image_profile_picture);
        TextView text_username = root.findViewById(R.id.text_username);
        TextView btn_to_profile = root.findViewById(R.id.btn_to_profile);
        ImageView btn_achievements = root.findViewById(R.id.btn_achievements);
        ImageView btn_events = root.findViewById(R.id.btn_events);
        LinearLayout view_charity = root.findViewById(R.id.view_charity);
        LinearLayout layout_admin = root.findViewById(R.id.layout_admin);
        TextView nav_philanthropists = root.findViewById(R.id.nav_philanthropists);
        TextView nav_companies = root.findViewById(R.id.nav_companies);

        Glide.with(getActivity()).asBitmap().load(MediaManager.get().url().generate(user.getPhoto())).into(image_profile_picture);

        if (user.getRole().matches("Administrator")) {
            layout_admin.setVisibility(View.VISIBLE);
        }
        if (user.getRole().matches("Charity")) {
            text_username.setText(user.getOrganization());
        }
        else {
            text_username.setText("@"+user.getUsername());
            view_charity.setVisibility(View.GONE);
        }

        nav_philanthropists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(new SettingsFragment()).commit();
                getFragmentManager().beginTransaction().replace(R.id.fragment2, new PhilanthropistsFragment()).commit();
            }
        });
        nav_companies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(new SettingsFragment()).commit();
                getFragmentManager().beginTransaction().replace(R.id.fragment2, new CompaniesFragment()).commit();
            }
        });
        btn_to_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                ProfileFragment profileFragment = new ProfileFragment();

                bundle.putInt("user_id", user.getID());
                if (user.getRole().matches("Charity")) {
                    bundle.putString("name", user.getOrganization());
                    bundle.putString("username", "");
                }
                else {
                    bundle.putString("name", user.getName());
                    bundle.putString("username", "@"+user.getUsername());
                }
                bundle.putString("photo", user.getPhoto());
                bundle.putString("role", user.getRole());
                profileFragment.setArguments(bundle);

                getFragmentManager().beginTransaction().remove(new SettingsFragment()).commit();
                getFragmentManager().beginTransaction().replace(R.id.fragment2, profileFragment).commit();
            }
        });
        btn_achievements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(new SettingsFragment()).commit();
                getFragmentManager().beginTransaction().replace(R.id.fragment2, new OwnAchievementsFragment()).commit();
            }
        });
        btn_events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(new SettingsFragment()).commit();
                getFragmentManager().beginTransaction().replace(R.id.fragment2, new OwnEventsFragment()).commit();
            }
        });

        return root;
    }

}
