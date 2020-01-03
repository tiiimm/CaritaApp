package dreamers.caritaapp.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import dreamers.caritaapp.R;

public class PhilanthropistSetUp1Fragment extends Fragment {

    View root;

    public PhilanthropistSetUp1Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_philanthropist_set_up1, container, false);

        Button btn_next = root.findViewById(R.id.btn_next);
        Button btn_back = root.findViewById(R.id.btn_back);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(new PhilanthropistSetUp1Fragment()).commit();
                getFragmentManager().beginTransaction().add(R.id.fragment,new PhilanthropistSetUp2Fragment()).addToBackStack(null).commit();
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(new PhilanthropistSetUp1Fragment()).commit();
                getFragmentManager().beginTransaction().add(R.id.fragment,new SetUpAsFragment()).addToBackStack(null).commit();
            }
        });

        return root;
    }
}
