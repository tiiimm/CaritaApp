package dreamers.caritaapp.fragment.admin;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import dreamers.caritaapp.adapter.PhilanthropistsAdapter;
import dreamers.caritaapp.database.MySingleton;
import dreamers.caritaapp.database.SessionHandler;
import dreamers.caritaapp.database.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhilanthropistsFragment extends Fragment {

    View root;
    SessionHandler sessionHandler;
    User user;

    private ArrayList<Integer> philanthropist_ids = new ArrayList<>();
    private ArrayList<String> philanthropist_names = new ArrayList<>();
    private ArrayList<String> philanthropist_points = new ArrayList<>();
    private ArrayList<String> philanthropist_usernames = new ArrayList<>();
    private ArrayList<String> philanthropist_photos = new ArrayList<>();

    public PhilanthropistsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_philanthropists, container, false);
        sessionHandler = new SessionHandler(getActivity());
        user = sessionHandler.getUserDetails();

        load_philanthropists();

        FloatingActionButton btn_add = root.findViewById(R.id.btn_add);

//        btn_add.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getFragmentManager().beginTransaction().remove(new PhilanthropistsFragment()).commit();
//                getFragmentManager().beginTransaction().replace(R.id.fragment_company, new UploadPhilanthropistFragment()).commit();
//            }
//        });

        return root;
    }

    private void load_philanthropists() {
        String request = "get_philanthropists";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, new SplashScreenActivity().url+ request, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                try {
                    JSONArray res = new JSONArray(response);
                    for (int i = 0; i<res.length(); i++) {
                        JSONObject philanthropist = res.getJSONObject(i);
                        philanthropist_ids.add(philanthropist.getInt("id"));

                        JSONObject user = philanthropist.getJSONObject("user");
                        philanthropist_names.add(user.getString("name"));
                        philanthropist_usernames.add("@"+user.getString("username"));
                        philanthropist_photos.add(user.getString("photo"));
                        philanthropist_points.add(user.getString("points"));
                    }
                    if (philanthropist_ids.size()>0){
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
        RecyclerView recyclerView = root.findViewById(R.id.list_philanthropists);
        PhilanthropistsAdapter adapter = new PhilanthropistsAdapter(getActivity(), philanthropist_names, philanthropist_usernames, philanthropist_points, philanthropist_ids, philanthropist_photos);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}
