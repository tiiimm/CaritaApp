package dreamers.caritaapp.fragment.home.settings.profile;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import dreamers.caritaapp.adapter.EventsAdapter;
import dreamers.caritaapp.database.MySingleton;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileEventsFragment extends Fragment {

    View root;
    Bundle bundle;

    private ArrayList<Integer> event_ids = new ArrayList<>();
    private ArrayList<Integer> event_points = new ArrayList<>();
    private ArrayList<String> event_titles = new ArrayList<>();
    private ArrayList<String> event_photos = new ArrayList<>();
    private ArrayList<String> event_venues = new ArrayList<>();
    private ArrayList<String> event_dates = new ArrayList<>();

    public ProfileEventsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_profile_events, container, false);

        bundle = getArguments();
        System.out.println(bundle);

        if (bundle.getString("role").matches("Charity")) {
            load_events();
        }

        return root;
    }

    private void load_events() {
        String request = "get_own_events?user_id="+ bundle.getInt("user_id");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, new SplashScreenActivity().url+ request, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray res = new JSONArray(response);
                    for (int i = 0; i<res.length(); i++) {
                        JSONObject event = res.getJSONObject(i);
                        event_ids.add(event.getInt("id"));
                        event_points.add(event.getInt("points"));
                        event_photos.add(event.getString("photo"));
                        event_titles.add(event.getString("title"));
                        if (event.getString("held_on_from").matches(event.getString("held_on_to")))
                            event_dates.add(event.getString("held_on_from"));
                        else
                            event_dates.add(event.getString("held_on_from")+" to "+event.getString("held_on_to"));
                        event_venues.add(event.getString("venue"));
                    }
                    if (event_ids.size()>0){
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
        RecyclerView recyclerView = root.findViewById(R.id.list_events);
        EventsAdapter adapter = new EventsAdapter(getActivity(), event_titles, event_dates, event_venues, event_ids, event_photos, event_points);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
    }
}
