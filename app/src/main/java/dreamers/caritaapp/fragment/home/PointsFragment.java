package dreamers.caritaapp.fragment.home;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import dreamers.caritaapp.R;
import dreamers.caritaapp.activity.SplashScreenActivity;
import dreamers.caritaapp.database.MySingleton;
import dreamers.caritaapp.database.SessionHandler;
import dreamers.caritaapp.database.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class PointsFragment extends Fragment {

    View root;
    SessionHandler sessionHandler;
    User user;

    String year;

    TextView january;
    TextView february;
    TextView march;
    TextView april;
    TextView may;
    TextView june;
    TextView july;
    TextView august;
    TextView september;
    TextView october;
    TextView november;
    TextView december;

    TextView january_charity;
    TextView february_charity;
    TextView march_charity;
    TextView april_charity;
    TextView may_charity;
    TextView june_charity;
    TextView july_charity;
    TextView august_charity;
    TextView september_charity;
    TextView october_charity;
    TextView november_charity;
    TextView december_charity;

    TextView points_label;
    TextView charity_label;
    TextView total_charity;
    TextView total_points;

    Spinner spinner_year;
    ArrayAdapter<String> adp1;

    public PointsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_points, container, false);
        sessionHandler = new SessionHandler(getActivity());
        user = sessionHandler.getUserDetails();

        points_label = root.findViewById(R.id.points_label);

        if (user.getRole().matches("Philanthropist")) {
            points_label.setText("Kindness Points");
        }
        else if (user.getRole().matches("Charity")) {
            points_label.setText("Supports");
        }
        else if (user.getRole().matches("Admin")) {
            points_label.setText("Total Donations");
        }

        january = root.findViewById(R.id.january);
        february = root.findViewById(R.id.february);
        march = root.findViewById(R.id.march);
        april = root.findViewById(R.id.april);
        may = root.findViewById(R.id.may);
        june = root.findViewById(R.id.june);
        july = root.findViewById(R.id.july);
        august = root.findViewById(R.id.august);
        september = root.findViewById(R.id.september);
        october = root.findViewById(R.id.october);
        november = root.findViewById(R.id.november);
        december = root.findViewById(R.id.december);

        total_charity = root.findViewById(R.id.total_charity);
        total_points = root.findViewById(R.id.total_points);
        charity_label = root.findViewById(R.id.charity_label);
        january_charity = root.findViewById(R.id.january_charity);
        february_charity = root.findViewById(R.id.february_charity);
        march_charity = root.findViewById(R.id.march_charity);
        april_charity = root.findViewById(R.id.april_charity);
        may_charity = root.findViewById(R.id.may_charity);
        june_charity = root.findViewById(R.id.june_charity);
        july_charity = root.findViewById(R.id.july_charity);
        august_charity = root.findViewById(R.id.august_charity);
        september_charity = root.findViewById(R.id.september_charity);
        october_charity = root.findViewById(R.id.october_charity);
        november_charity = root.findViewById(R.id.november_charity);
        december_charity = root.findViewById(R.id.december_charity);

        spinner_year = root.findViewById(R.id.spinner_year);

        load_spinner();

        year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        spinner_year.setSelection(adp1.getPosition(year));

        if (user.getRole().matches("Philanthropist")) {
            load_user_donations();
        }
        else if (user.getRole().matches("Charity")) {
            load_charity_donations();
            total_charity.setVisibility(View.VISIBLE);
            charity_label.setText("Events");
            charity_label.setVisibility(View.VISIBLE);
            january_charity.setVisibility(View.VISIBLE);
            february_charity.setVisibility(View.VISIBLE);
            march_charity.setVisibility(View.VISIBLE);
            april_charity.setVisibility(View.VISIBLE);
            may_charity.setVisibility(View.VISIBLE);
            june_charity.setVisibility(View.VISIBLE);
            july_charity.setVisibility(View.VISIBLE);
            august_charity.setVisibility(View.VISIBLE);
            september_charity.setVisibility(View.VISIBLE);
            october_charity.setVisibility(View.VISIBLE);
            november_charity.setVisibility(View.VISIBLE);
            december_charity.setVisibility(View.VISIBLE);
        }

        spinner_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                year = spinner_year.getSelectedItem().toString();

                if (user.getRole().matches("Philanthropist")) {
                    load_user_donations();
                }
                else if (user.getRole().matches("Charity")) {
                    load_charity_donations();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


        return root;
    }

    public void load_spinner() {
        List<String> list = new ArrayList<>();

        for (int i = 2019; i <= 2030; i++) {
            list.add(Integer.toString(i));
        }

        adp1 = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, list);
        adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_year.setAdapter(adp1);
    }

    private void load_user_donations() {
        String request = "get_donations?user_id="+ user.getID() +"&year="+ year;
        System.out.println(request);
        StringRequest jsArrayRequest = new StringRequest(
        Request.Method.GET, new SplashScreenActivity().url + request, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                try {
                    JSONObject res = new JSONObject(response);
                    january.setText(res.getString("january"));
                    february.setText(res.getString("february"));
                    march.setText(res.getString("march"));
                    april.setText(res.getString("april"));
                    may.setText(res.getString("may"));
                    june.setText(res.getString("june"));
                    july.setText(res.getString("july"));
                    august.setText(res.getString("august"));
                    september.setText(res.getString("september"));
                    october.setText(res.getString("october"));
                    november.setText(res.getString("november"));
                    december.setText(res.getString("december"));

                    total_points.setText("Total Points: "+res.getString("total_donations"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        MySingleton.getInstance(getActivity()).addToRequestQueue(jsArrayRequest);
    }

    private void load_charity_donations() {
        String request = "get_supports?user_id="+ user.getID() +"&year="+ year;
        System.out.println(request);
        StringRequest jsArrayRequest = new StringRequest(
                Request.Method.GET, new SplashScreenActivity().url + request, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                try {
                    JSONObject res = new JSONObject(response);
                    january.setText(res.getString("january"));
                    february.setText(res.getString("february"));
                    march.setText(res.getString("march"));
                    april.setText(res.getString("april"));
                    may.setText(res.getString("may"));
                    june.setText(res.getString("june"));
                    july.setText(res.getString("july"));
                    august.setText(res.getString("august"));
                    september.setText(res.getString("september"));
                    october.setText(res.getString("october"));
                    november.setText(res.getString("november"));
                    december.setText(res.getString("december"));

                    january_charity.setText(res.getString("january_charity"));
                    february_charity.setText(res.getString("february_charity"));
                    march_charity.setText(res.getString("march_charity"));
                    april_charity.setText(res.getString("april_charity"));
                    may_charity.setText(res.getString("may_charity"));
                    june_charity.setText(res.getString("june_charity"));
                    july_charity.setText(res.getString("july_charity"));
                    august_charity.setText(res.getString("august_charity"));
                    september_charity.setText(res.getString("september_charity"));
                    october_charity.setText(res.getString("october_charity"));
                    november_charity.setText(res.getString("november_charity"));
                    december_charity.setText(res.getString("december_charity"));

                    total_points.setText("Total Points: "+res.getString("total_supports"));
                    total_charity.setText("Event Points: "+res.getString("total_events_points"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        MySingleton.getInstance(getActivity()).addToRequestQueue(jsArrayRequest);
    }
}
