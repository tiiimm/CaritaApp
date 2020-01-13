package dreamers.caritaapp.fragment.set_up;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import dreamers.caritaapp.R;
import dreamers.caritaapp.activity.CompanyActivity;
import dreamers.caritaapp.activity.LoginActivity;
import dreamers.caritaapp.activity.SplashScreenActivity;
import dreamers.caritaapp.database.MySingleton;
import dreamers.caritaapp.database.SessionHandler;
import dreamers.caritaapp.database.User;

import static android.app.Activity.RESULT_OK;

public class CompanySetUp1Fragment extends Fragment {

    View root;
    SessionHandler sessionHandler;
    User user;
    String image_path="";
    CircleImageView circleImageView;
    EditText text_name;

    public CompanySetUp1Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_company_set_up1, container, false);
        sessionHandler = new SessionHandler(getActivity());
        user = sessionHandler.getUserDetails();

        circleImageView = root.findViewById(R.id.image_company);
        text_name = root.findViewById(R.id.text_company);
        Button btn_finish = root.findViewById(R.id.btn_finish);
        Button btn_back = root.findViewById(R.id.btn_back);

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                }
                else {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 1);
                }
            }
        });
        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("clicked");
                String name = text_name.getText().toString();

                if (name.matches("")) {
                    text_name.setError("Required");
                    return;
                }

                if (!image_path.matches(""))
                    upload_profile(name);
                else {
                    image_path = "carita/profile_picture.png";
                    set_up(name, image_path);
                }
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(new CompanySetUp1Fragment()).commit();
                getFragmentManager().beginTransaction().replace(R.id.fragment,new SetUpAsFragment()).commit();
            }
        });

       return root;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if(resultCode == RESULT_OK){
            Uri selected = imageReturnedIntent.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getActivity().getContentResolver().query(selected, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            image_path = cursor.getString(columnIndex);
            cursor.close();
            circleImageView.setImageBitmap(BitmapFactory.decodeFile(image_path));
        }
        else
            image_path = "";
    }

    private void upload_profile(final String name) {
        final String charity_timestamp = String.valueOf(System.currentTimeMillis());
        MediaManager.get().upload(image_path)
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
                Double progress = (double) bytes/totalBytes;
            }
            @Override
            public void onSuccess(String requestId, Map resultData) {
                image_path = resultData.get("public_id").toString() + "." + resultData.get("format").toString();

                set_up(name, image_path);
            }
            @Override
            public void onError(String requestId, ErrorInfo error) {
                return;
            }
            @Override
            public void onReschedule(String requestId, ErrorInfo error) {
                return;
            }})
        .dispatch();
    }

    private void set_up(String name, String photo) {
        String request = "set_up_company?user_id="+ user.getID() +"&name="+ name +"&photo="+ photo;
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
                                    text_name.setError(errors.get(i).toString());
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                    else {
                        sessionHandler.logoutUser();
                        Toast.makeText(getActivity(),
                                "Successful! Wait for admin to accept your application", Toast.LENGTH_LONG).show();
                        getFragmentManager().beginTransaction().remove(new CompanySetUp1Fragment()).commit();
                        Intent i = new Intent(getActivity(), LoginActivity.class);
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
