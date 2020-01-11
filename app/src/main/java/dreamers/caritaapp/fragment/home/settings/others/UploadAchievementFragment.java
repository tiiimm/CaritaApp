package dreamers.caritaapp.fragment.home.settings.others;


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
import dreamers.caritaapp.fragment.home.settings.OwnAchievementsFragment;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class UploadAchievementFragment extends Fragment {

    View root;
    SessionHandler sessionHandler;
    User user;

    ImageView image_achievement;
    String image_path = "";

    public UploadAchievementFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_upload_achievement, container, false);
        sessionHandler = new SessionHandler(getActivity());
        user = sessionHandler.getUserDetails();

        image_achievement = root.findViewById(R.id.image_achievement);
        final TextView text_achievement_title = root.findViewById(R.id.text_achievement_title);
        final TextView text_achievement_description = root.findViewById(R.id.text_achievement_description);
        final TextView text_achievement_venue = root.findViewById(R.id.text_achievement_venue);
        final TextView text_achievement_date_from = root.findViewById(R.id.text_achievement_date_from);
        final TextView text_achievement_date_to = root.findViewById(R.id.text_achievement_date_to);
        Button btn_upload = root.findViewById(R.id.btn_upload);
        Button btn_back = root.findViewById(R.id.btn_back);

        DatePickerDialog.OnDateSetListener achievement_from_listener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month++;
                text_achievement_date_from.setText(year+"-"+month+"-"+dayOfMonth);
                text_achievement_date_to.setText(year+"-"+month+"-"+dayOfMonth);
            }
        };

        DatePickerDialog.OnDateSetListener achievement_to_listener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month++;
                text_achievement_date_to.setText(year+"-"+month+"-"+dayOfMonth);
            }
        };

        final DatePickerDialog achievement_from_dialog = new DatePickerDialog(getActivity(), achievement_from_listener, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        final DatePickerDialog achievement_to_dialog = new DatePickerDialog(getActivity(), achievement_to_listener, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        text_achievement_date_from.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) achievement_from_dialog.show();
            }
        });

        text_achievement_date_to.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) achievement_to_dialog.show();
            }
        });

        text_achievement_date_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                achievement_from_dialog.show();
            }
        });

        text_achievement_date_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                achievement_to_dialog.show();
            }
        });

        image_achievement.setOnClickListener(new View.OnClickListener() {
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
                    startActivityForResult(pickPhoto, 1);
                }
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(new UploadAchievementFragment()).commit();
                getFragmentManager().beginTransaction().replace(R.id.fragment2, new OwnAchievementsFragment()).commit();
            }
        });
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timestamp = String.valueOf(System.currentTimeMillis());
                final String title = text_achievement_title.getText().toString();
                final String venue = text_achievement_venue.getText().toString();
                final String description = text_achievement_description.getText().toString();
                final String date_from = text_achievement_date_from.getText().toString();
                final String date_to = text_achievement_date_to.getText().toString();
                Boolean valid = true;

                if (title.matches("")){
                    text_achievement_title.setError("Required");
                    valid = false;
                }
                if (venue.matches("")){
                    text_achievement_venue.setError("Required");
                    valid = false;
                }
                if (description.matches("")){
                    text_achievement_description.setError("Required");
                    valid = false;
                }
                if (date_from.matches("")){
                    text_achievement_date_from.setError("Required");
                    valid = false;
                }
                if (date_to.matches("")){
                    text_achievement_date_to.setError("Required");
                    valid = false;
                }
                if (image_path.matches("")) {
                    Toast.makeText(getActivity(), "Set photo!", Toast.LENGTH_SHORT).show();
                    valid = false;
                }

                if (valid) {
                    MediaManager.get().upload(image_path)
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
                            image_path = resultData.get("public_id").toString() + "." + resultData.get("format").toString();

                            upload(title, venue, description, date_from, date_to, image_path);
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
            image_path = cursor.getString(columnIndex);
            cursor.close();
            image_achievement.setImageBitmap(BitmapFactory.decodeFile(image_path));
        }
        else
            image_path = "";
    }

    private void upload(String title, String venue, String description, String date_from, String date_to, String image_path) {
        String request = "upload_achievement?user_id="+ user.getID() +"&title="+ title +"&venue="+ venue +"&description="+ description +"&held_on_from="+ date_from +"&held_on_to="+ date_to +"&photo="+ image_path;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, new SplashScreenActivity().url+ request, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject res = new JSONObject(response);
                    if (res.has("success")){
                        Toast.makeText(getActivity(), "Successfull!", Toast.LENGTH_SHORT).show();
                        getFragmentManager().beginTransaction().remove(new UploadAchievementFragment()).commit();
                        getFragmentManager().beginTransaction().replace(R.id.fragment2, new OwnAchievementsFragment()).commit();
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
