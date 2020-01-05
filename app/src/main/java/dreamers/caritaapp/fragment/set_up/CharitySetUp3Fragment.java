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
import com.cloudinary.android.MediaManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dreamers.caritaapp.R;
import dreamers.caritaapp.activity.HomeActivity;
import dreamers.caritaapp.activity.SplashScreenActivity;
import dreamers.caritaapp.database.MySingleton;
import dreamers.caritaapp.database.SessionHandler;
import dreamers.caritaapp.database.User;

public class CharitySetUp3Fragment extends Fragment {

    View root;
    SessionHandler sessionHandler;
    User user;
    Bundle bundle;
    EditText text_account_name;
    EditText text_account_number;
    EditText text_bank;

    public CharitySetUp3Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_charity_set_up3, container, false);
        sessionHandler = new SessionHandler(getActivity());
        user = sessionHandler.getUserDetails();

        Button btn_finish = root.findViewById(R.id.btn_finish);
        Button btn_back = root.findViewById(R.id.btn_back);
        text_account_name = root.findViewById(R.id.text_account_name);
        text_account_number = root.findViewById(R.id.text_account_number);
        text_bank = root.findViewById(R.id.text_bank);

        bundle = getArguments();
        if (bundle != null) {
            System.out.println(bundle);
            configure();
        }
        else
            bundle = new Bundle();

        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account_name = text_account_name.getText().toString();
                String account_number = text_account_number.getText().toString();
                String bank = text_bank.getText().toString();
                Boolean valid = true;

                if (account_name.matches("")) {
                    text_account_name.setError("Required");
                    valid = false;
                }
                if (account_number.matches("")) {
                    text_account_number.setError("Required");
                    valid = false;
                }
                if (bank.matches("")) {
                    text_bank.setError("Required");
                    valid = false;
                }

                if (valid) {
                    bundle.putString("account_name", account_name);
                    bundle.putString("account_number", account_number);
                    bundle.putString("bank", bank);

                    set_up();
                }
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharitySetUp2Fragment charitySetUp2Fragment = new CharitySetUp2Fragment();
                String account_name = text_account_name.getText().toString();
                String account_number = text_account_number.getText().toString();
                String bank = text_bank.getText().toString();

                bundle.putString("account_name", account_name);
                bundle.putString("account_number", account_number);
                bundle.putString("bank", bank);
                charitySetUp2Fragment.setArguments(bundle);

                getFragmentManager().beginTransaction().remove(new CharitySetUp3Fragment()).commit();
                getFragmentManager().beginTransaction().add(R.id.fragment, charitySetUp2Fragment).addToBackStack(null).commit();
            }
        });
        return root;
    }

    public void configure() {
        if(bundle.getString("account_name") != null && bundle.getString("account_number") != null && bundle.getString("bank") != null){
            text_account_name.setText(bundle.getString("account_name"));
            text_account_number.setText(bundle.getString("account_number"));
            text_bank.setText(bundle.getString("bank"));
        }
    }

    public void set_up() {
        String image_charity = "";
        String charity_bio = "";
        String type = "";

        try {
            String timestamp = String.valueOf(System.currentTimeMillis());
            if (!bundle.getString("image_charity").matches("")) {
                MediaManager.get().upload(bundle.getString("image_charity"))
                        .option("resource_type", "image")
                        .option("folder", "carita/")
                        .option("public_id", timestamp)
                        .option("overwrite", true)
                        .dispatch();

                image_charity = timestamp + bundle.getString("image_charity").substring(bundle.getString("image_charity").lastIndexOf("."));
                image_charity = "carita/" + image_charity;
            }
            if (!bundle.getString("charity_bio").matches("")) {
                MediaManager.get().upload(bundle.getString("charity_bio"))
                        .option("resource_type", bundle.getString("type"))
                        .option("folder", "carita/")
                        .option("public_id", timestamp)
                        .option("overwrite", true)
                        .dispatch();

                charity_bio = timestamp + bundle.getString("charity_bio").substring(bundle.getString("charity_bio").lastIndexOf("."));
                charity_bio = "carita/" + charity_bio;
                type = bundle.getString("type");
            }

            if (image_charity.matches(""))
                image_charity = "carita/profile_picture.png";
            if (charity_bio.matches("")) {
                charity_bio = "carita/profile_picture.png";
                type = "image";
            }

            save_to_database(image_charity, charity_bio, type);
        }
        catch (Exception e) {

        }
    }

    public void save_to_database(String image_charity, String charity_bio, String type) {

        String request = "set_up?user_id="+ user.getID() +"&role=Charity&contact_number="+ bundle.getString("contact_number") +
                "&organization="+ bundle.getString("organization") +"&account_name="+ bundle.getString("account_name") +
                "&account_number="+ bundle.getString("account_number") +"&bank="+ bundle.getString("bank") +
                "&address="+ bundle.getString("address") +"&description="+ bundle.getString("description") +
                "&photo="+ image_charity +"&bio_path="+ charity_bio +"&bio_path_type="+ type;

        System.out.println(request);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, new SplashScreenActivity().url+ request, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                try {
                    JSONObject res = new JSONObject(response);
                    if (res.has("errors")){
                        JSONArray errors = new JSONArray(res.getString("errors"));
                        for (int i = 0; i < errors.length(); i++) {
                            switch (errors.get(i).toString().split(" ")[2]) {
                                case "name":
                                    text_account_name.setError(errors.get(i).toString());
                                    break;
                                case "number":
                                    text_account_number.setError(errors.get(i).toString());
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                    else {
                        sessionHandler.set_up_charity();
                        Toast.makeText(getActivity(),
                                "Successful!", Toast.LENGTH_LONG).show();
                        getFragmentManager().beginTransaction().remove(new CharitySetUp3Fragment()).commit();
                        Intent i = new Intent(getActivity(), HomeActivity.class);
                        startActivity(i);
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
