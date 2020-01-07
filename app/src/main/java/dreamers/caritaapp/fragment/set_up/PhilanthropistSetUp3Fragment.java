package dreamers.caritaapp.fragment.set_up;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import dreamers.caritaapp.R;
import dreamers.caritaapp.activity.HomeActivity;

public class PhilanthropistSetUp3Fragment extends Fragment {

    View root;

    public PhilanthropistSetUp3Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_philanthropist_set_up3, container, false);

        Button btn_finish = root.findViewById(R.id.btn_finish);
        Button btn_back = root.findViewById(R.id.btn_back);

        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(new PhilanthropistSetUp3Fragment()).commit();
                Intent i = new Intent(getActivity(), HomeActivity.class);
                startActivity(i);
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(new PhilanthropistSetUp3Fragment()).commit();
                getFragmentManager().beginTransaction().replace(R.id.fragment,new PhilanthropistSetUp2Fragment()).commit();
            }
        });

        return root;
    }
}
