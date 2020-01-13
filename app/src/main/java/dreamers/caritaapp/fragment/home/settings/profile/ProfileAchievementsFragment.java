package dreamers.caritaapp.fragment.home.settings.profile;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import dreamers.caritaapp.R;
import dreamers.caritaapp.activity.SplashScreenActivity;
import dreamers.caritaapp.adapter.AchievementsAdapter;
import dreamers.caritaapp.database.MySingleton;
import dreamers.caritaapp.database.SessionHandler;
import dreamers.caritaapp.database.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileAchievementsFragment extends Fragment {

    View root;
    Bundle bundle;

    private ArrayList<Integer> achievement_ids = new ArrayList<>();
    private ArrayList<String> achievement_titles = new ArrayList<>();
    private ArrayList<String> achievement_photos = new ArrayList<>();
    private ArrayList<String> achievement_venues = new ArrayList<>();
    private ArrayList<String> achievement_dates = new ArrayList<>();
    private ArrayList<Integer> search_ids = new ArrayList<>();
    private ArrayList<String> search_titles = new ArrayList<>();
    private ArrayList<String> search_photos = new ArrayList<>();
    private ArrayList<String> search_venues = new ArrayList<>();
    private ArrayList<String> search_dates = new ArrayList<>();

    public ProfileAchievementsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_profile_achievements, container, false);

        bundle = getArguments();

        if (bundle.getString("role").matches("Charity")) {
            load_achievements();
        }

        final EditText text_search = root.findViewById(R.id.text_search);
        text_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search_ids.clear();
                search_titles.clear();
                search_photos.clear();
                search_venues.clear();
                search_dates.clear();
                int x;
                for (x = 0; x < achievement_ids.size(); x++) {
                    if (
                        search_titles.get(x).toLowerCase().contains(text_search.getText().toString().toLowerCase()) ||
                        search_venues.get(x).toLowerCase().contains(text_search.getText().toString().toLowerCase()) ||
                        search_dates.get(x).toLowerCase().contains(text_search.getText().toString().toLowerCase())
                    ) {
                        search_ids.add(achievement_ids.get(x));
                        search_titles.add(achievement_titles.get(x));
                        search_photos.add(achievement_photos.get(x));
                        search_venues.add(achievement_venues.get(x));
                        search_dates.add(achievement_dates.get(x));
                    }
                }
                initSearchView();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return root;
    }

    private void load_achievements() {
        String request = "get_own_achievements?user_id="+ bundle.getInt("user_id");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, new SplashScreenActivity().url+ request, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray res = new JSONArray(response);
                    for (int i = 0; i<res.length(); i++) {
                        JSONObject achievement = res.getJSONObject(i);
                        achievement_ids.add(achievement.getInt("id"));
                        achievement_photos.add(achievement.getString("photo"));
                        achievement_titles.add(achievement.getString("title"));
                        if (achievement.getString("held_on_from").matches(achievement.getString("held_on_to")))
                            achievement_dates.add(achievement.getString("held_on_from"));
                        else
                            achievement_dates.add(achievement.getString("held_on_from")+" to "+achievement.getString("held_on_to"));
                        achievement_venues.add(achievement.getString("venue"));
                    }
                    if (achievement_ids.size()>0){
                        initRecyclerView();
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

    private void initRecyclerView(){
        RecyclerView recyclerView = root.findViewById(R.id.list_achievements);
        AchievementsAdapter adapter = new AchievementsAdapter(getActivity(), achievement_titles, achievement_dates, achievement_venues, achievement_ids, achievement_photos);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
    }

    private void initSearchView(){
        RecyclerView recyclerView = root.findViewById(R.id.list_achievements);
        AchievementsAdapter adapter = new AchievementsAdapter(getActivity(), search_titles, search_dates, search_venues, search_ids, search_photos);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
    }
}
