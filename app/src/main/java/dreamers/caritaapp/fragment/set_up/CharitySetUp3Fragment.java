package dreamers.caritaapp.fragment.set_up;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import dreamers.caritaapp.R;
import dreamers.caritaapp.activity.HomeActivity;
import dreamers.caritaapp.activity.LoginActivity;
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

    String image_charity;
    String charity_bio;
    String type;
    ProgressDialog progressDialog;

    GoogleSignInClient mGoogleSignInClient;

    public CharitySetUp3Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_charity_set_up3, container, false);
        sessionHandler = new SessionHandler(getActivity());
        user = sessionHandler.getUserDetails();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        Button btn_finish = root.findViewById(R.id.btn_finish);
        Button btn_back = root.findViewById(R.id.btn_back);
        text_account_name = root.findViewById(R.id.text_account_name);
        text_account_number = root.findViewById(R.id.text_account_number);
        text_bank = root.findViewById(R.id.text_bank);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");

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
                    progressDialog.show();
                    bundle.putString("account_name", account_name);
                    bundle.putString("account_number", account_number);
                    bundle.putString("bank", bank);

                    image_charity = "carita/profile_picture.png";
                    charity_bio = "carita/profile_picture.png";
                    type = "image";

                    upload_profile();
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
                getFragmentManager().beginTransaction().replace(R.id.fragment, charitySetUp2Fragment).commit();
            }
        });
        return root;
    }

    private void configure() {
        if(bundle.getString("account_name") != null && bundle.getString("account_number") != null && bundle.getString("bank") != null){
            text_account_name.setText(bundle.getString("account_name"));
            text_account_number.setText(bundle.getString("account_number"));
            text_bank.setText(bundle.getString("bank"));
        }
    }

    private void upload_profile() {
        try {
            final String charity_timestamp = String.valueOf(System.currentTimeMillis());
            if (!bundle.getString("image_charity").matches("")) {
                MediaManager.get().upload(bundle.getString("image_charity"))
                .option("resource_type", "image")
                .option("folder", "carita/")
                .option("public_id", charity_timestamp)
                .option("overwrite", true)
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                    }
                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        // example code starts here
                        Double progress = (double) bytes/totalBytes;
                    }
                    @Override
                    public void onSuccess(String requestId, Map resultData) {
//                        image_charity = charity_timestamp + bundle.getString("image_charity").substring(bundle.getString("image_charity").lastIndexOf("."));
//                        image_charity = "carita/" + image_charity;
                        image_charity = resultData.get("public_id").toString() + "." + resultData.get("format").toString();
//
                        upload_bio();
                    }
                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Error uploading. Try again", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Error uploading. Try again", Toast.LENGTH_SHORT).show();
                        return;
                    }})
                .dispatch();
            }
            else {
                upload_bio();
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    private void upload_bio() {
        try {
            final String bio_timestamp = String.valueOf(System.currentTimeMillis());
            if (!bundle.getString("charity_bio").matches("")) {
                MediaManager.get().upload(bundle.getString("charity_bio"))
                .option("resource_type", bundle.getString("type"))
                .option("folder", "carita/")
                .option("public_id", bio_timestamp)
                .option("overwrite", true)
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        // your code here
                    }
                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        // example code starts here
                        Double progress = (double) bytes/totalBytes;
                        // post progress to app UI (e.g. progress bar, notification)
                        // example code ends here
                    }
                    @Override
                    public void onSuccess(String requestId, Map resultData) {
//                        charity_bio = bio_timestamp + bundle.getString("charity_bio").substring(bundle.getString("charity_bio").lastIndexOf("."));
//                        charity_bio = "carita/" + charity_bio;
                        charity_bio = resultData.get("public_id").toString() + "." + resultData.get("format").toString();
                        type = resultData.get("resource_type").toString();

                        save_to_database(image_charity, charity_bio, type);
                    }
                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Error uploading. Try again", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Error uploading. Try again", Toast.LENGTH_SHORT).show();
                        return;
                    }})
                .dispatch();
            }
            else {
                save_to_database(image_charity, charity_bio, type);
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    private void save_to_database(final String image_charity, String charity_bio, String type) {
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
                    progressDialog.dismiss();
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
                        sessionHandler.logoutUser();
                        mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                System.out.println("Signed out");
                            }
                        });
                        Toast.makeText(getActivity(),
                                "Successful! Wait for admin to accept your application", Toast.LENGTH_LONG).show();
                        getFragmentManager().beginTransaction().remove(new CharitySetUp3Fragment()).commit();
                        Intent i = new Intent(getActivity(), LoginActivity.class);
                        startActivity(i);
                    }
                } catch (JSONException e) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(),
                            "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
                progressDialog.dismiss();
                Toast.makeText(getActivity(),
                        "Something went wrong", Toast.LENGTH_LONG).show();
            }
        });
        MySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }
}
