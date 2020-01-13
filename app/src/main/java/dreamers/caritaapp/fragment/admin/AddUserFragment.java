package dreamers.caritaapp.fragment.admin;


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
import dreamers.caritaapp.activity.SetUpActivity;
import dreamers.caritaapp.activity.SignupActivity;
import dreamers.caritaapp.activity.SplashScreenActivity;
import dreamers.caritaapp.database.MySingleton;
import dreamers.caritaapp.database.SessionHandler;
import dreamers.caritaapp.database.User;
import dreamers.caritaapp.fragment.home.home.HomeCharitiesFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddUserFragment extends Fragment {

    View root;
    SessionHandler sessionHandler;
    User user;
    Bundle bundle;

    EditText text_fullname;
    EditText text_email;
    EditText text_username;

    public AddUserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_add_user, container, false);

        text_fullname = root.findViewById(R.id.text_fullname);
        text_email = root.findViewById(R.id.text_email);
        text_username = root.findViewById(R.id.text_username);
        Button btn_save = root.findViewById(R.id.btn_save);
        Button btn_back = root.findViewById(R.id.btn_back);

        bundle = getArguments();
        final String role = bundle.getString("role");

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (role.matches("Temporary Philanthropist")) {
                getFragmentManager().beginTransaction().remove(new AddUserFragment()).commit();
                getFragmentManager().beginTransaction().replace(R.id.fragment2, new PhilanthropistsFragment()).commit();
            }
            if (role.matches("Temporary Charity")) {
                getFragmentManager().beginTransaction().remove(new AddUserFragment()).commit();
                getFragmentManager().beginTransaction().replace(R.id.fragment2, new HomeCharitiesFragment()).commit();
            }
            if (role.matches("Temporary Company")) {
                getFragmentManager().beginTransaction().remove(new AddUserFragment()).commit();
                getFragmentManager().beginTransaction().replace(R.id.fragment2, new CompaniesFragment()).commit();
            }
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullname = text_fullname.getText().toString();
                String email = text_email.getText().toString();
                String username = text_username.getText().toString();
                Boolean valid = true;

                if (fullname.matches("")) {
                    text_fullname.setError("Required");
                    valid = false;
                }
                if (email.matches("")) {
                    text_email.setError("Required");
                    valid = false;
                }
                if (username.matches("")) {
                    text_username.setError("Required");
                    valid = false;
                }

                if (bundle.getString("role").matches("")) {
                    valid = false;
                }

                if (valid) {
                    save(fullname, email, username, bundle.getString("role"));
                }
            }
        });

        return root;
    }

    private void save(String fullname, String email, String username, final String role) {
        String request = "create_user?email="+ email +"&password=123456789&name="+ fullname +"&password_confirmation=123456789&username="+ username +"&role="+ role;
        System.out.println(request);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                new SplashScreenActivity().url+ request,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                        try {
                            JSONObject res = new JSONObject(response);
                            if (res.has("errors")){
                                JSONArray errors = new JSONArray(res.getString("errors"));
                                for (int i = 0; i < errors.length(); i++) {
                                    switch (errors.get(i).toString().split(" ")[1]) {
                                        case "email":
                                            text_email.setError(errors.get(i).toString());
                                            break;
                                        case "fullname":
                                            text_fullname.setError(errors.get(i).toString());
                                            break;
                                        case "username":
                                            text_username.setError(errors.get(i).toString());
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            }
                            else {
                                Toast.makeText(getActivity(),
                                        "Successful!", Toast.LENGTH_LONG).show();
                                if (role.matches("Temporary Philanthropist")) {
                                    getFragmentManager().beginTransaction().remove(new AddUserFragment()).commit();
                                    getFragmentManager().beginTransaction().replace(R.id.fragment2, new PhilanthropistsFragment()).commit();
                                }
                                if (role.matches("Temporary Charity")) {
                                    getFragmentManager().beginTransaction().remove(new AddUserFragment()).commit();
                                    getFragmentManager().beginTransaction().replace(R.id.fragment2, new HomeCharitiesFragment()).commit();
                                }
                                if (role.matches("Temporary Company")) {
                                    getFragmentManager().beginTransaction().remove(new AddUserFragment()).commit();
                                    getFragmentManager().beginTransaction().replace(R.id.fragment2, new CompaniesFragment()).commit();
                                }
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getActivity(),
                                    "Something went wrong", Toast.LENGTH_LONG).show();
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
