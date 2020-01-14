package dreamers.caritaapp.fragment.set_up;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import dreamers.caritaapp.R;
import dreamers.caritaapp.activity.HomeActivity;
import dreamers.caritaapp.activity.SplashScreenActivity;
import dreamers.caritaapp.database.MySingleton;
import dreamers.caritaapp.database.SessionHandler;
import dreamers.caritaapp.database.User;

public class PhilanthropistSetUp3Fragment extends Fragment {

    View root;
    SessionHandler sessionHandler;
    User user;
    Bundle bundle;

    String image_philanthropist;
    ProgressDialog progressDialog;

    public PhilanthropistSetUp3Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_philanthropist_set_up3, container, false);
        sessionHandler = new SessionHandler(getActivity());
        user = sessionHandler.getUserDetails();

        Button btn_finish = root.findViewById(R.id.btn_finish);
        Button btn_back = root.findViewById(R.id.btn_back);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");

        bundle = getArguments();

        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                image_philanthropist = "carita/profile_picture.png";
                String timestamp = String.valueOf(System.currentTimeMillis());
                if (!bundle.getString("image_philanthropist").matches("")) {
                    MediaManager.get().upload(bundle.getString("image_philanthropist"))
                    .option("resource_type", "image")
                    .option("folder", "carita/")
                    .option("public_id", timestamp)
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
                            image_philanthropist = resultData.get("public_id").toString() + "." + resultData.get("format").toString();

                            set_up(image_philanthropist);
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
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhilanthropistSetUp2Fragment philanthropistSetUp2Fragment = new PhilanthropistSetUp2Fragment();

                philanthropistSetUp2Fragment.setArguments(bundle);
                getFragmentManager().beginTransaction().remove(new PhilanthropistSetUp3Fragment()).commit();
                getFragmentManager().beginTransaction().replace(R.id.fragment, philanthropistSetUp2Fragment).commit();
            }
        });

        return root;
    }

    private void set_up(final String photo) {
        String request = "set_up?user_id="+ user.getID() +"&contact_number="+ bundle.getString("contact_number") +"&photo="+ photo +"&role=Philanthropist&birthday=&sex=";
        System.out.println(request);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, new SplashScreenActivity().url+ request, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                try {
                    progressDialog.dismiss();
                    JSONObject res = new JSONObject(response);
                    if (res.has("errors")){
                    }
                    else {
                        sessionHandler.set_up("Philanthropist", "", photo);
                        Toast.makeText(getActivity(),
                                "Successful!", Toast.LENGTH_LONG).show();
                        getFragmentManager().beginTransaction().remove(new PhilanthropistSetUp3Fragment()).commit();
                        Intent i = new Intent(getActivity(), HomeActivity.class);
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
                progressDialog.dismiss();
                System.out.println(error);
                Toast.makeText(getActivity(),
                        "Something went wrong", Toast.LENGTH_LONG).show();
            }
        });
        MySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }
}
