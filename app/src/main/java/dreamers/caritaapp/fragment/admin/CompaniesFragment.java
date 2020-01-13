package dreamers.caritaapp.fragment.admin;


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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import dreamers.caritaapp.R;
import dreamers.caritaapp.activity.SplashScreenActivity;
import dreamers.caritaapp.adapter.CompaniesAdapter;
import dreamers.caritaapp.database.MySingleton;
import dreamers.caritaapp.database.SessionHandler;
import dreamers.caritaapp.database.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class CompaniesFragment extends Fragment {

    View root;
    SessionHandler sessionHandler;
    User user;

    private ArrayList<Integer> company_ids = new ArrayList<>();
    private ArrayList<String> company_names = new ArrayList<>();
    private ArrayList<String> company_photos = new ArrayList<>();
    private ArrayList<Integer> search_ids = new ArrayList<>();
    private ArrayList<String> search_names = new ArrayList<>();
    private ArrayList<String> search_photos = new ArrayList<>();

    public CompaniesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_companies, container, false);
        sessionHandler = new SessionHandler(getActivity());
        user = sessionHandler.getUserDetails();

        load_companies();

        FloatingActionButton btn_add = root.findViewById(R.id.btn_add);
        final EditText text_search = root.findViewById(R.id.text_search);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddUserFragment addUserFragment = new AddUserFragment();
                Bundle bundle = new Bundle();
                bundle.putString("role", "Temporary Company");
                addUserFragment.setArguments(bundle);
                getFragmentManager().beginTransaction().remove(new CompaniesFragment()).commit();
                getFragmentManager().beginTransaction().replace(R.id.fragment2, addUserFragment).commit();
            }
        });
        text_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search_ids.clear();
                search_names.clear();
                search_photos.clear();
                int x;
                for (x = 0; x < company_ids.size(); x++) {
                    if (company_names.get(x).toLowerCase().contains(text_search.getText().toString().toLowerCase())) {
                        search_ids.add(company_ids.get(x));
                        search_names.add(company_names.get(x));
                        search_photos.add(company_photos.get(x));
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

    private void load_companies() {
        String request = "get_companies";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, new SplashScreenActivity().url+ request, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                try {
                    JSONArray res = new JSONArray(response);
                    for (int i = 0; i<res.length(); i++) {
                        JSONObject company = res.getJSONObject(i);
                        company_ids.add(company.getInt("id"));
                        company_names.add(company.getString("name"));
                        company_photos.add(company.getString("photo"));
                    }
                    if (company_ids.size()>0){
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
        RecyclerView recyclerView = root.findViewById(R.id.list_companies);
        CompaniesAdapter adapter = new CompaniesAdapter(getActivity(), company_names, company_ids, company_photos);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void initSearchView(){
        RecyclerView recyclerView = root.findViewById(R.id.list_companies);
        CompaniesAdapter adapter = new CompaniesAdapter(getActivity(), search_names, search_ids, search_photos);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}
