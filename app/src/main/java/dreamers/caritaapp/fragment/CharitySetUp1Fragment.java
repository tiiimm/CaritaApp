package dreamers.caritaapp.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import dreamers.caritaapp.R;
import dreamers.caritaapp.activity.SetUpActivity;

public class CharitySetUp1Fragment extends Fragment {

    View root;

    public CharitySetUp1Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_charity_set_up1, container, false);

        Button btn_next = root.findViewById(R.id.btn_next);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(new CharitySetUp1Fragment()).commit();
                getFragmentManager().beginTransaction().add(R.id.fragment,new CharitySetUp2Fragment()).addToBackStack(null).commit();
            }
        });

        return root;
    }
}
