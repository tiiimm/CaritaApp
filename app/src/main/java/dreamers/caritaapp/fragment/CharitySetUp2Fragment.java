package dreamers.caritaapp.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import dreamers.caritaapp.R;

public class CharitySetUp2Fragment extends Fragment {

    View root;

    public CharitySetUp2Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_charity_set_up2, container, false);

        Button btn_next = root.findViewById(R.id.btn_next);
        Button btn_back = root.findViewById(R.id.btn_back);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(new CharitySetUp2Fragment()).commit();
                getFragmentManager().beginTransaction().add(R.id.fragment,new CharitySetUp3Fragment()).addToBackStack(null).commit();
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(new CharitySetUp2Fragment()).commit();
                getFragmentManager().beginTransaction().add(R.id.fragment,new CharitySetUp1Fragment()).addToBackStack(null).commit();
            }
        });
        return root;
    }
}
