package dreamers.caritaapp.fragment.set_up;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dreamers.caritaapp.R;
import dreamers.caritaapp.activity.LoginActivity;
import dreamers.caritaapp.activity.SplashScreenActivity;
import dreamers.caritaapp.database.MySingleton;
import dreamers.caritaapp.database.SessionHandler;
import dreamers.caritaapp.database.User;

public class PhilanthropistSetUp1Fragment extends Fragment {

    View root;
    SessionHandler sessionHandler;
    User user;
    Bundle bundle;
    EditText text_address;
    EditText text_contact_number;
    Boolean valid = true;

    public PhilanthropistSetUp1Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_philanthropist_set_up1, container, false);
        sessionHandler = new SessionHandler(getActivity());
        user = sessionHandler.getUserDetails();

        Button btn_next = root.findViewById(R.id.btn_next);
        Button btn_back = root.findViewById(R.id.btn_back);
        text_address = root.findViewById(R.id.text_address);
        text_contact_number = root.findViewById(R.id.text_contact_number);

        bundle = getArguments();
        if (bundle != null) {
            System.out.println(bundle);
            configure();
        }
        else
            bundle = new Bundle();

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = text_address.getText().toString();
                String contact_number = text_contact_number.getText().toString();
                valid = true;

                if (address.matches("")) {
                    text_address.setError("Required");
                    valid = false;
                }
                if (contact_number.matches("")) {
                    text_contact_number.setError("Required");
                    valid = false;
                }

                if(valid) {
                    validate(contact_number, address);
                }
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(new PhilanthropistSetUp1Fragment()).commit();
                getFragmentManager().beginTransaction().replace(R.id.fragment,new SetUpAsFragment()).commit();
            }
        });

        return root;
    }

    private void configure() {
        if(bundle.getString("address") != null && bundle.getString("contact_number") != null){
            text_address.setText(bundle.getString("address"));
            text_contact_number.setText(bundle.getString("contact_number"));
        }
    }

    private void validate(final String contact_number, final String address) {
        String request = "validate_philanthropist?user_id="+ user.getID() +"&contact_number="+ contact_number;
        System.out.println(request);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, new SplashScreenActivity().url+ request, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject res = new JSONObject(response);
                    if (res.has("errors")){
                        JSONArray errors = new JSONArray(res.getString("errors"));
                        for (int i = 0; i < errors.length(); i++) {
                            switch (errors.get(i).toString().split(" ")[1]) {
                                case "user":
                                    Toast.makeText(getActivity(), "You already have an account. Try to login", Toast.LENGTH_SHORT).show();
                                    sessionHandler.logoutUser();
                                    Intent j = new Intent(getActivity(), LoginActivity.class);
                                    startActivity(j);
                                    break;
                                case "contact":
                                    text_contact_number.setError(errors.get(i).toString());
                                    valid = false;
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                    else if (res.has("success")) {
                        PhilanthropistSetUp2Fragment philanthropistSetUp2Fragment = new PhilanthropistSetUp2Fragment();
                        bundle.putString("address", address);
                        bundle.putString("contact_number", contact_number);
                        philanthropistSetUp2Fragment.setArguments(bundle);

                        getFragmentManager().beginTransaction().remove(new PhilanthropistSetUp1Fragment()).commit();
                        getFragmentManager().beginTransaction().replace(R.id.fragment, philanthropistSetUp2Fragment).commit();
                    }
                }
                catch (JSONException e) {
                    Toast.makeText(getActivity(),
                            "Something went wrong", Toast.LENGTH_LONG).show();
                    valid = false;
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
                Toast.makeText(getActivity(),
                        "Something went wrong", Toast.LENGTH_LONG).show();
                valid = false;
            }
        });
        MySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }
}
