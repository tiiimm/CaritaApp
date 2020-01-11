package dreamers.caritaapp.fragment.company;


import android.Manifest;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Map;

import dreamers.caritaapp.R;
import dreamers.caritaapp.activity.SplashScreenActivity;
import dreamers.caritaapp.database.MySingleton;
import dreamers.caritaapp.database.SessionHandler;
import dreamers.caritaapp.database.User;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class UploadAdvertisementFragment extends Fragment {

    View root;
    SessionHandler sessionHandler;
    User user;

    ImageView image_advertisement;
    VideoView video_advertisement;
    String advertisement_path = "";
    String advertisement_type = "";

    public UploadAdvertisementFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_upload_advertisement, container, false);
        sessionHandler = new SessionHandler(getActivity());
        user = sessionHandler.getUserDetails();

        image_advertisement = root.findViewById(R.id.image_advertisement);
        video_advertisement = root.findViewById(R.id.video_advertisement);
        final TextView text_advertisement_name = root.findViewById(R.id.text_advertisement_name);
        Button btn_upload = root.findViewById(R.id.btn_upload);
        Button btn_back = root.findViewById(R.id.btn_back);

        image_advertisement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }
            else {
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType("image/* video/*");
                startActivityForResult(intent,1);
            }
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(new UploadAdvertisementFragment()).commit();
                getFragmentManager().beginTransaction().replace(R.id.fragment_company, new CompanyAdvertisements()).commit();
            }
        });
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timestamp = String.valueOf(System.currentTimeMillis());
                final String name = text_advertisement_name.getText().toString();
                Boolean valid = true;

                if (name.matches("")){
                    text_advertisement_name.setError("Required");
                    valid = false;
                }
                if (advertisement_path.matches("")) {
                    Toast.makeText(getActivity(), "Set advertisement!", Toast.LENGTH_SHORT).show();
                    valid = false;
                }

                if (valid) {
                    MediaManager.get().upload(advertisement_path)
                    .option("resource_type", advertisement_type)
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
                            System.out.println(progress);
                        }
                        @Override
                        public void onSuccess(String requestId, Map resultData) {
                            advertisement_path = resultData.get("public_id").toString() + "." + resultData.get("format").toString();
                            System.out.println(resultData);
                            upload(name, advertisement_path, resultData.get("resource_type").toString());
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
            if (selected.toString().contains("image")) {
                advertisement_type = "image";
                advertisement_path = cursor.getString(columnIndex);
                cursor.close();
                video_advertisement.setVisibility(View.GONE);
                image_advertisement.setVisibility(View.VISIBLE);
                image_advertisement.setImageBitmap(BitmapFactory.decodeFile(advertisement_path));
            }
            else if (selected.toString().contains("video")) {
                advertisement_type = "video";
                advertisement_path = cursor.getString(columnIndex);
                cursor.close();
                image_advertisement.setVisibility(View.GONE);
                video_advertisement.setVisibility(View.VISIBLE);
                video_advertisement.setVideoPath(advertisement_path);
                video_advertisement.start();
            }
        }
        else {
            advertisement_type = "";
            advertisement_path = "";
        }
    }

    private void upload(String name, String advertisement_path, String advertisement_type) {
        String request = "upload_advertisement?user_id="+ user.getID() +"&name="+ name +"&advertisement="+ advertisement_path +"&advertisement_type="+ advertisement_type +"&status=Pending";
        System.out.println(request);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, new SplashScreenActivity().url+ request, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject res = new JSONObject(response);
                    if (res.has("success")){
                        Toast.makeText(getActivity(), "Successfull!", Toast.LENGTH_SHORT).show();
                        getFragmentManager().beginTransaction().remove(new UploadAdvertisementFragment()).commit();
                        getFragmentManager().beginTransaction().replace(R.id.fragment_company, new CompanyAdvertisements()).commit();
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
