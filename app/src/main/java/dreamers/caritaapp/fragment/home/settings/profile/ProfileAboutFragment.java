package dreamers.caritaapp.fragment.home.settings.profile;


import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import dreamers.caritaapp.R;
import dreamers.caritaapp.activity.SplashScreenActivity;
import dreamers.caritaapp.database.MySingleton;
import dreamers.caritaapp.database.SessionHandler;
import dreamers.caritaapp.database.User;
import dreamers.caritaapp.fragment.home.settings.ProfileFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileAboutFragment extends Fragment {

    View root;
    Bundle bundle;
    TextView text_description;
    TextView text_address;
    TextView text_contact_number;
    TextView text_bank_account;
    TextView text_bank;

    public ProfileAboutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root =inflater.inflate(R.layout.fragment_profile_about, container, false);

        text_description = root.findViewById(R.id.text_description);
        text_address = root.findViewById(R.id.text_address);
        text_contact_number = root.findViewById(R.id.text_contact_number);
        text_bank_account = root.findViewById(R.id.text_bank_account);
        text_bank = root.findViewById(R.id.text_bank);

        bundle = getArguments();

        if (bundle.getString("role").matches("Charity")) {
            System.out.println("MATECHES");
            configure();
        }

        return root;
    }

    private void configure() {
        String request = "get_profile?user_id="+ bundle.getInt("user_id");
        System.out.println("about"+request);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, new SplashScreenActivity().url+ request, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject res = new JSONObject(response);
                    text_description.setText(res.getString("description"));
                    text_address.setText(res.getString("address"));
                    text_contact_number.setText(res.getString("contact_number"));
                    text_bank_account.setText(res.getString("account_name")+" - "+res.getString("account_number"));
                    text_bank.setText(res.getString("bank"));
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
