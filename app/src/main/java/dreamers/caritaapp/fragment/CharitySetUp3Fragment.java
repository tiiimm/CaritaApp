package dreamers.caritaapp.fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import dreamers.caritaapp.R;
import dreamers.caritaapp.activity.HomeActivity;

public class CharitySetUp3Fragment extends Fragment {

    View root;
    Bundle bundle;
    EditText text_account_name;
    EditText text_account_number;

    public CharitySetUp3Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_charity_set_up3, container, false);

        Button btn_finish = root.findViewById(R.id.btn_finish);
        Button btn_back = root.findViewById(R.id.btn_back);
        text_account_name = root.findViewById(R.id.text_account_name);
        text_account_number = root.findViewById(R.id.text_account_number);

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
                Boolean valid = true;

                if (account_name.matches("")) {
                    text_account_name.setError("Required");
                    valid = false;
                }
                if (account_number.matches("")) {
                    text_account_number.setError("Required");
                    valid = false;
                }

                if (valid) {
                    bundle.putString("account_name", account_name);
                    bundle.putString("account_number", account_number);

                    set_up();

//                    getFragmentManager().beginTransaction().remove(new CharitySetUp3Fragment()).commit();
//                    Intent i = new Intent(getActivity(), HomeActivity.class);
//                    startActivity(i);
                }
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharitySetUp2Fragment charitySetUp2Fragment = new CharitySetUp2Fragment();
                String account_name = text_account_name.getText().toString();
                String account_number = text_account_number.getText().toString();

                bundle.putString("account_name", account_name);
                bundle.putString("account_number", account_number);
                charitySetUp2Fragment.setArguments(bundle);

                getFragmentManager().beginTransaction().remove(new CharitySetUp3Fragment()).commit();
                getFragmentManager().beginTransaction().add(R.id.fragment, charitySetUp2Fragment).addToBackStack(null).commit();
            }
        });
        return root;
    }

    public void configure() {
        if(bundle.getString("account_name") != null && bundle.getString("account_number") != null){
            text_account_name.setText(bundle.getString("account_name"));
            text_account_number.setText(bundle.getString("account_number"));
        }
    }

    public void set_up() {

    }
}
