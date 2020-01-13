package dreamers.caritaapp.fragment.company;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import dreamers.caritaapp.R;
import dreamers.caritaapp.activity.SplashScreenActivity;
import dreamers.caritaapp.adapter.AdvertisementsAdapter;
import dreamers.caritaapp.database.MySingleton;
import dreamers.caritaapp.database.SessionHandler;
import dreamers.caritaapp.database.User;
import dreamers.caritaapp.fragment.home.SettingsFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class CompanyAdvertisements extends Fragment {

    View root;
    SessionHandler sessionHandler;
    User user;

    private ArrayList<Integer> advertisement_ids = new ArrayList<>();
    private ArrayList<String> advertisement_names = new ArrayList<>();
    private ArrayList<String> advertisement_billing_dates = new ArrayList<>();
    private ArrayList<String> advertisement_statuses = new ArrayList<>();

    public CompanyAdvertisements() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_company_advertisements, container, false);
        sessionHandler = new SessionHandler(getActivity());
        user = sessionHandler.getUserDetails();

        load_advertisements();

        FloatingActionButton btn_add = root.findViewById(R.id.btn_add);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(new CompanyAdvertisements()).commit();
                getFragmentManager().beginTransaction().replace(R.id.fragment_company, new UploadAdvertisementFragment()).commit();
            }
        });

        return root;
    }

    private void load_advertisements() {
        String request = "get_own_advertisements?user_id="+ user.getID();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, new SplashScreenActivity().url+ request, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray res = new JSONArray(response);
                    for (int i = 0; i<res.length(); i++) {
                        JSONObject advertisement = res.getJSONObject(i);
                        advertisement_ids.add(advertisement.getInt("id"));
                        advertisement_names.add(advertisement.getString("name"));
                        advertisement_statuses.add(advertisement.getString("status"));
                        advertisement_billing_dates.add(advertisement.getString("billing_date"));
                    }
                    if (advertisement_ids.size()>0){
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
        RecyclerView recyclerView = root.findViewById(R.id.list_advertisements);
        AdvertisementsAdapter adapter = new AdvertisementsAdapter(getActivity(), advertisement_names, advertisement_statuses, advertisement_billing_dates, advertisement_ids);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
    }
}
