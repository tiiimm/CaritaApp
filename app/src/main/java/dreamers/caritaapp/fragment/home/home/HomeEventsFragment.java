package dreamers.caritaapp.fragment.home.home;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import dreamers.caritaapp.adapter.EventsAdapter;
import dreamers.caritaapp.database.MySingleton;
import dreamers.caritaapp.database.SessionHandler;
import dreamers.caritaapp.database.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeEventsFragment extends Fragment {

    View root;
    SessionHandler sessionHandler;
    User user;

    private ArrayList<Integer> event_ids = new ArrayList<>();
    private ArrayList<Integer> event_points = new ArrayList<>();
    private ArrayList<String> event_titles = new ArrayList<>();
    private ArrayList<String> event_photos = new ArrayList<>();
    private ArrayList<String> event_venues = new ArrayList<>();
    private ArrayList<String> event_dates = new ArrayList<>();
    private ArrayList<Integer> search_ids = new ArrayList<>();
    private ArrayList<Integer> search_points = new ArrayList<>();
    private ArrayList<String> search_titles = new ArrayList<>();
    private ArrayList<String> search_photos = new ArrayList<>();
    private ArrayList<String> search_venues = new ArrayList<>();
    private ArrayList<String> search_dates = new ArrayList<>();

    public HomeEventsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home_events, container, false);
        sessionHandler = new SessionHandler(getActivity());
        user = sessionHandler.getUserDetails();

        load_events();

        final EditText text_search = root.findViewById(R.id.text_search);
        text_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search_ids.clear();
                search_points.clear();
                search_titles.clear();
                search_photos.clear();
                search_venues.clear();
                search_dates.clear();
                int x;
                for (x = 0; x < event_ids.size(); x++) {
                    if (
                        event_titles.get(x).toLowerCase().contains(text_search.getText().toString().toLowerCase()) ||
                        event_venues.get(x).toLowerCase().contains(text_search.getText().toString().toLowerCase()) ||
                        event_dates.get(x).toLowerCase().contains(text_search.getText().toString().toLowerCase())
                    ) {
                        search_ids.add(event_ids.get(x));
                        search_points.add(event_points.get(x));
                        search_titles.add(event_titles.get(x));
                        search_photos.add(event_photos.get(x));
                        search_venues.add(event_venues.get(x));
                        search_dates.add(event_dates.get(x));
                    }
                    try {
                        if (
                            event_points.get(x).equals(Integer.parseInt(text_search.getText().toString()))
                        ) {
                            search_ids.add(event_ids.get(x));
                            search_points.add(event_points.get(x));
                            search_titles.add(event_titles.get(x));
                            search_photos.add(event_photos.get(x));
                            search_venues.add(event_venues.get(x));
                            search_dates.add(event_dates.get(x));
                        }
                    }
                    catch (Exception e) {

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

    private void load_events() {
        String request = "get_events";
        System.out.println(request);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, new SplashScreenActivity().url+ request, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
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
        System.out.println("hoy");
        RecyclerView recyclerView = root.findViewById(R.id.list_events);
        EventsAdapter adapter = new EventsAdapter(getActivity(), event_titles, event_dates, event_venues, event_ids, event_photos, event_points);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void initSearchView(){
        System.out.println("hoy");
        RecyclerView recyclerView = root.findViewById(R.id.list_events);
        EventsAdapter adapter = new EventsAdapter(getActivity(), search_titles, search_dates, search_venues, search_ids, search_photos, search_points);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}
