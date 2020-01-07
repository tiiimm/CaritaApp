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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;
import dreamers.caritaapp.R;
import dreamers.caritaapp.activity.LoginActivity;
import dreamers.caritaapp.activity.SplashScreenActivity;
import dreamers.caritaapp.database.MySingleton;
import dreamers.caritaapp.database.SessionHandler;
import dreamers.caritaapp.database.User;

import static android.app.Activity.RESULT_OK;

public class CharitySetUp1Fragment extends Fragment {

    View root;
    SessionHandler sessionHandler;
    User user;
    Bundle bundle;
    CircleImageView image_charity;
    EditText text_organization;
    EditText text_address;
    EditText text_contact_number;
    String image_path = "";
    Boolean valid = true;

    public CharitySetUp1Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_charity_set_up1, container, false);
        sessionHandler = new SessionHandler(getActivity());
        user = sessionHandler.getUserDetails();

        Button btn_next = root.findViewById(R.id.btn_next);
        Button btn_back = root.findViewById(R.id.btn_back);
        text_organization = root.findViewById(R.id.text_organization);
        text_address = root.findViewById(R.id.text_address);
        text_contact_number = root.findViewById(R.id.text_contact_number);
        image_charity = root.findViewById(R.id.image_charity);

        bundle = getArguments();
        if (bundle != null) {
            System.out.println(bundle);
            configure();
        }
        else
            bundle = new Bundle();

        image_charity.setOnClickListener(new View.OnClickListener() {
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

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String organization = text_organization.getText().toString();
                String address = text_address.getText().toString();
                String contact_number = text_contact_number.getText().toString();

                if (organization.matches("")) {
                    text_organization.setError("Required");
                    valid = false;
                }
                if (address.matches("")) {
                    text_address.setError("Required");
                    valid = false;
                }
                if (contact_number.matches("")) {
                    text_contact_number.setError("Required");
                    valid = false;
                }
                if (contact_number.length()<10) {
                    text_contact_number.setError("Should be at least 10 digits");
                    valid = false;
                }
                if (valid){
                    validate(organization, contact_number, address);
                }
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(new CharitySetUp1Fragment()).commit();
                getFragmentManager().beginTransaction().replace(R.id.fragment,new SetUpAsFragment()).addToBackStack(null).commit();
            }
        });

        return root;
    }

    private void configure() {
        if(bundle.getString("organization") != null && bundle.getString("address") != null && bundle.getString("contact_number") != null && bundle.getString("image_charity") != null){
            text_organization.setText(bundle.getString("organization"));
            text_address.setText(bundle.getString("address"));
            text_contact_number.setText(bundle.getString("contact_number"));
            image_path = bundle.getString("image_charity");

            if (!image_path.matches(""))
                image_charity.setImageBitmap(BitmapFactory.decodeFile(bundle.getString("image_charity")));
        }
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
            image_charity.setImageBitmap(BitmapFactory.decodeFile(image_path));
        }
        else
            image_path = "";
    }

    private void validate(final String organization, final String contact_number, final String address) {
        String request = "validate_charity?user_id="+ user.getID() +"&organization="+ organization +"&contact_number="+ contact_number;

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
                                case "organization":
                                    text_organization.setError(errors.get(i).toString());
                                    valid = false;
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                    else if (res.has("success")) {
                        CharitySetUp2Fragment charitySetUp2Fragment = new CharitySetUp2Fragment();
                        bundle.putString("organization", organization);
                        bundle.putString("address", address);
                        bundle.putString("contact_number", contact_number);
                        bundle.putString("image_charity", image_path);
                        charitySetUp2Fragment.setArguments(bundle);

                        getFragmentManager().beginTransaction().remove(new CharitySetUp1Fragment()).commit();
                        getFragmentManager().beginTransaction().replace(R.id.fragment, charitySetUp2Fragment).addToBackStack(null).commit();
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
