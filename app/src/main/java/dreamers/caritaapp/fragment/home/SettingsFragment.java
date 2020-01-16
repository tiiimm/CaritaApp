package dreamers.caritaapp.fragment.home;


import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;
import dreamers.caritaapp.R;
import dreamers.caritaapp.activity.SplashScreenActivity;
import dreamers.caritaapp.database.MySingleton;
import dreamers.caritaapp.database.SessionHandler;
import dreamers.caritaapp.database.User;
import dreamers.caritaapp.fragment.admin.AddUserFragment;
import dreamers.caritaapp.fragment.admin.AdvertisementsFragment;
import dreamers.caritaapp.fragment.admin.CompaniesFragment;
import dreamers.caritaapp.fragment.admin.PhilanthropistsFragment;
import dreamers.caritaapp.fragment.home.home.HomeCharitiesFragment;
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
        TextView text_points = root.findViewById(R.id.text_points);
        TextView text_level = root.findViewById(R.id.text_level);
        LinearLayout view_charity = root.findViewById(R.id.view_charity);
        LinearLayout view_admin = root.findViewById(R.id.view_admin);
        LinearLayout layout_philanthropist = root.findViewById(R.id.layout_philanthropist);
        LinearLayout layout_charity_details = root.findViewById(R.id.layout_charity_details);
        ImageView nav_philanthropists = root.findViewById(R.id.nav_philanthropists);
        ImageView nav_companies = root.findViewById(R.id.nav_companies);
        ImageView nav_advertisements = root.findViewById(R.id.nav_advertisements);
        TextView nav_change_password = root.findViewById(R.id.nav_change_password);

        Glide.with(getActivity()).asBitmap().load(MediaManager.get().url().generate(user.getPhoto())).into(image_profile_picture);

        if (user.getRole().matches("Administrator")) {
            view_admin.setVisibility(View.VISIBLE);
            btn_to_profile.setVisibility(View.GONE);
        }
        if (user.getRole().matches("Company")) {
            btn_to_profile.setVisibility(View.GONE);
        }
        if (user.getRole().matches("Philanthropist")) {
            layout_philanthropist.setVisibility(View.VISIBLE);
            btn_to_profile.setVisibility(View.GONE);
            text_points.setText("Points: "+user.getPoints().toString());
            if (user.getPoints().equals(0)) {
                text_level.setText("Level: None");
            }
            if (user.getPoints()>0 && user.getPoints()<10) {
                text_level.setText("Level 1: Kindhearted");
            }
            if (user.getPoints()>10 && user.getPoints()<30) {
                text_level.setText("Level 2: Hardworker");
            }
            if (user.getPoints()>30 && user.getPoints()<50) {
                text_level.setText("Level 3: Good Samaritan");
            }
            if (user.getPoints()>50 && user.getPoints()<100) {
                text_level.setText("Level 4: Angelic");
            }
        }
        if (user.getRole().matches("Charity")) {
            layout_charity_details.setVisibility(View.VISIBLE);
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
        nav_advertisements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(new SettingsFragment()).commit();
                getFragmentManager().beginTransaction().replace(R.id.fragment2, new AdvertisementsFragment()).commit();
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
                bundle.putString("points", "0");
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
        nav_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Title");

                LinearLayout layout = new LinearLayout(getActivity());
                layout.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                params.setMargins(50, 0, 50, 0);

                final EditText new_password = new EditText(getActivity());
                new_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                new_password.setHint("Enter new password");
                new_password.setLayoutParams(params);
                layout.addView(new_password);

                final EditText old_password = new EditText(getActivity());
                old_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                old_password.setHint("Enter old password");
                old_password.setLayoutParams(params);
                layout.addView(old_password);

                builder.setView(layout);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Boolean valid = true;
                        if (new_password.getText().toString().matches("")) {
                            Toast.makeText(getActivity(), "No changes have been made. New password is empty", Toast.LENGTH_LONG).show();
                            valid = false;
                        }
                        if (new_password.getText().toString().length()<8) {
                            Toast.makeText(getActivity(), "No changes have been made. New password should be at least 8 characters", Toast.LENGTH_LONG).show();
                            valid = false;
                        }
                        if (old_password.getText().toString().matches("")) {
                            Toast.makeText(getActivity(), "No changes have been made. Old password is empty", Toast.LENGTH_LONG).show();
                            valid = false;
                        }
                        if (new_password.getText().toString().matches(old_password.getText().toString())) {
                            Toast.makeText(getActivity(), "No changes have been made. Passwords are same", Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (valid) {
                            change_password(new_password.getText().toString(), old_password.getText().toString());
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        return root;
    }

    private void change_password(String new_password, String old_password) {
        String request = "change_password?user_id="+ user.getID() +"&old_password="+ old_password +"&new_password="+ new_password;
        System.out.println(request);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                new SplashScreenActivity().url+ request,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                        try {
                            JSONObject res = new JSONObject(response);
                            if (res.has("error")) {
                                Toast.makeText(getActivity(), res.getString("error"), Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(getActivity(), "Success!", Toast.LENGTH_SHORT).show();
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
