package dreamers.caritaapp.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import dreamers.caritaapp.R;
import dreamers.caritaapp.activity.SetUpActivity;

public class CharitySetUp1Fragment extends Fragment {

    View root;
    Bundle bundle;
    ImageView image_charity;
    EditText text_organization;
    EditText text_address;
    EditText text_contact_number;

    public CharitySetUp1Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_charity_set_up1, container, false);

        Button btn_next = root.findViewById(R.id.btn_next);
        Button btn_back = root.findViewById(R.id.btn_back);
        text_organization = root.findViewById(R.id.text_organization);
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
                String organization = text_organization.getText().toString();
                String address = text_address.getText().toString();
                String contact_number = text_contact_number.getText().toString();
                Boolean valid = true;
                CharitySetUp2Fragment charitySetUp2Fragment = new CharitySetUp2Fragment();

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
                    text_contact_number.setError("Required");
                    valid = false;
                }

                if (valid){
                    bundle.putString("organization", organization);
                    bundle.putString("address", address);
                    bundle.putString("contact_number", contact_number);
                    charitySetUp2Fragment.setArguments(bundle);

                    getFragmentManager().beginTransaction().remove(new CharitySetUp1Fragment()).commit();
                    getFragmentManager().beginTransaction().add(R.id.fragment, charitySetUp2Fragment).addToBackStack(null).commit();
                }
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(new CharitySetUp1Fragment()).commit();
                getFragmentManager().beginTransaction().add(R.id.fragment,new SetUpAsFragment()).addToBackStack(null).commit();
            }
        });

        return root;
    }

    public void configure() {
        if(bundle.getString("organization") != null && bundle.getString("address") != null && bundle.getString("contact_number") != null){
            text_organization.setText(bundle.getString("organization"));
            text_address.setText(bundle.getString("address"));
            text_contact_number.setText(bundle.getString("contact_number"));
        }
    }
}
