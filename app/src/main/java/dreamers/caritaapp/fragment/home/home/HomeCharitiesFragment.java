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
import dreamers.caritaapp.adapter.CharitiesAdapter;
import dreamers.caritaapp.database.MySingleton;
import dreamers.caritaapp.database.SessionHandler;
import dreamers.caritaapp.database.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeCharitiesFragment extends Fragment {

    View root;
    SessionHandler sessionHandler;
    User user;

    private ArrayList<Integer> charity_ids = new ArrayList<>();
    private ArrayList<Integer> charity_user_ids = new ArrayList<>();
    private ArrayList<String> charity_names = new ArrayList<>();
    private ArrayList<String> charity_statuses = new ArrayList<>();
    private ArrayList<String> charity_photos = new ArrayList<>();
    private ArrayList<String> charity_addresses = new ArrayList<>();
    private ArrayList<String> charity_contacts = new ArrayList<>();
    private ArrayList<Integer> charity_points = new ArrayList<>();
    private ArrayList<Integer> search_ids = new ArrayList<>();
    private ArrayList<Integer> search_user_ids = new ArrayList<>();
    private ArrayList<String> search_names = new ArrayList<>();
    private ArrayList<String> search_statuses = new ArrayList<>();
    private ArrayList<String> search_photos = new ArrayList<>();
    private ArrayList<String> search_addresses = new ArrayList<>();
    private ArrayList<String> search_contacts = new ArrayList<>();
    private ArrayList<Integer> search_points = new ArrayList<>();

    public HomeCharitiesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home_charities, container, false);
        sessionHandler = new SessionHandler(getActivity());
        user = sessionHandler.getUserDetails();

        if (user.getRole().matches("Administrator"))
            load_charities();
        else load_active_charities();

        final EditText text_search = root.findViewById(R.id.text_search);
        text_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int x;
                search_ids.clear();
                search_user_ids.clear();
                search_names.clear();
                search_statuses.clear();
                search_photos.clear();
                search_addresses.clear();
                search_contacts.clear();
                search_points.clear();
                for (x = 0; x < charity_ids.size(); x++) {
                    if (
                        charity_names.get(x).toLowerCase().contains(text_search.getText().toString().toLowerCase()) ||
                        charity_addresses.get(x).toLowerCase().contains(text_search.getText().toString().toLowerCase()) ||
                        charity_statuses.get(x).toLowerCase().contains(text_search.getText().toString().toLowerCase()) ||
                        charity_contacts.get(x).toLowerCase().contains(text_search.getText().toString().toLowerCase())
                    ) {
                        search_ids.add(charity_ids.get(x));
                        search_user_ids.add(charity_user_ids.get(x));
                        search_names.add(charity_names.get(x));
                        search_statuses.add(charity_statuses.get(x));
                        search_photos.add(charity_photos.get(x));
                        search_addresses.add(charity_addresses.get(x));
                        search_contacts.add(charity_contacts.get(x));
                        search_points.add(charity_points.get(x));
                    }
                    try {
                        if (charity_points.get(x).equals(Integer.parseInt(text_search.getText().toString()))) {
                            search_ids.add(charity_ids.get(x));
                            search_user_ids.add(charity_user_ids.get(x));
                            search_names.add(charity_names.get(x));
                            search_statuses.add(charity_statuses.get(x));
                            search_photos.add(charity_photos.get(x));
                            search_addresses.add(charity_addresses.get(x));
                            search_contacts.add(charity_contacts.get(x));
                            search_points.add(charity_points.get(x));
                        }
                    }
                    catch (Exception e){

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

    private void load_active_charities() {
        String request = "get_active_charities";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, new SplashScreenActivity().url+ request, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray res = new JSONArray(response);
                    for (int i = 0; i<res.length(); i++) {
                        JSONObject charity = res.getJSONObject(i);
                        charity_ids.add(charity.getInt("id"));
                        charity_user_ids.add(charity.getInt("user_id"));
                        charity_photos.add(charity.getString("photo"));
                        charity_names.add(charity.getString("organization"));
                        charity_statuses.add(charity.getString("status"));
                        charity_addresses.add(charity.getString("address"));
                        charity_contacts.add(charity.getString("contact_number"));
                        charity_points.add(charity.getInt("points"));
                    }
                    if (charity_ids.size()>0){
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

    private void load_charities() {
        String request = "get_charities";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, new SplashScreenActivity().url+ request, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray res = new JSONArray(response);
                    for (int i = 0; i<res.length(); i++) {
                        JSONObject charity = res.getJSONObject(i);
                        charity_ids.add(charity.getInt("id"));
                        charity_user_ids.add(charity.getInt("user_id"));
                        charity_photos.add(charity.getString("photo"));
                        charity_names.add(charity.getString("organization"));
                        charity_statuses.add(charity.getString("status"));
                        charity_addresses.add(charity.getString("address"));
                        charity_contacts.add(charity.getString("contact_number"));
                        charity_points.add(charity.getInt("points"));
                    }
                    if (charity_ids.size()>0){
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
        RecyclerView recyclerView = root.findViewById(R.id.list_charities);
        CharitiesAdapter adapter = new CharitiesAdapter(getActivity(), charity_names, charity_addresses, charity_contacts, charity_ids, charity_photos, charity_user_ids, charity_points, charity_statuses, user.getRole());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void initSearchView(){
        RecyclerView recyclerView = root.findViewById(R.id.list_charities);
        CharitiesAdapter adapter = new CharitiesAdapter(getActivity(), search_names, search_addresses, search_contacts, search_ids, search_photos, search_user_ids, search_points, search_statuses, user.getRole());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}
