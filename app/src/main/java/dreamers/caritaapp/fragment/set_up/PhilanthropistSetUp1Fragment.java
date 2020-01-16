package dreamers.caritaapp.fragment.set_up;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

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
    EditText text_birthdate;
    EditText text_contact_number;
    Boolean valid = true;
    String sex = "";

    GoogleSignInClient mGoogleSignInClient;

    public PhilanthropistSetUp1Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_philanthropist_set_up1, container, false);
        sessionHandler = new SessionHandler(getActivity());
        user = sessionHandler.getUserDetails();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        Button btn_next = root.findViewById(R.id.btn_next);
        Button btn_back = root.findViewById(R.id.btn_back);
        RadioGroup radioGroup = root.findViewById(R.id.radio_sex);
        text_birthdate = root.findViewById(R.id.text_birthdate);
        text_contact_number = root.findViewById(R.id.text_contact_number);

        bundle = getArguments();
        if (bundle != null) {
            System.out.println(bundle);
            configure();
        }
        else
            bundle = new Bundle();

        DatePickerDialog.OnDateSetListener birthdate_listener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month++;
                text_birthdate.setText(year+"-"+month+"-"+dayOfMonth);
            }
        };
        final DatePickerDialog birthdate_dialog = new DatePickerDialog(getActivity(), birthdate_listener, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        text_birthdate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) birthdate_dialog.show();
            }
        });
        text_birthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                birthdate_dialog.show();
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rd_female:
                        sex = "Female";
                        break;
                    case R.id.rd_male:
                        sex = "Male";
                        break;
                    default:
                        sex = "";
                        break;
                }
            }
        });
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String birthdate = text_birthdate.getText().toString();
                String contact_number = text_contact_number.getText().toString();
                valid = true;

                if (contact_number.matches("")) {
                    text_contact_number.setError("Required!");
                    valid = false;
                }

                if(valid) {
                    validate(contact_number, birthdate);
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
        if(bundle.getString("birthdate") != null && bundle.getString("contact_number") != null){
            text_birthdate.setText(bundle.getString("birthdate"));
            text_contact_number.setText(bundle.getString("contact_number"));
        }
    }

    private void validate(final String contact_number, final String birthdate) {
        String request = "validate_philanthropist?user_id="+ user.getID() +"&contact_number="+ contact_number +"&birthday="+ birthdate +"&sex="+ sex;
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
                                    Toast.makeText(getActivity(), "You already have an account. Try to login", Toast.LENGTH_LONG).show();
                                    sessionHandler.logoutUser();
                                    mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            System.out.println("Signed out");
                                        }
                                    });
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
                        bundle.putString("birthdate", birthdate);
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
