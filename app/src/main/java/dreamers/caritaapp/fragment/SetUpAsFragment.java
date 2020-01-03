package dreamers.caritaapp.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import dreamers.caritaapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SetUpAsFragment extends Fragment {

    View root;

    public SetUpAsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_set_up_as, container, false);



        ImageView image_charity = root.findViewById(R.id.image_charity);
        ImageView image_philanthropist = root.findViewById(R.id.image_philanthropist);

        image_charity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(new SetUpAsFragment()).commit();
                getFragmentManager().beginTransaction().add(R.id.fragment,new CharitySetUp1Fragment()).addToBackStack(null).commit();
            }
        });

        image_philanthropist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(new SetUpAsFragment()).commit();
                getFragmentManager().beginTransaction().add(R.id.fragment,new PhilanthropistSetUp1Fragment()).addToBackStack(null).commit();
            }
        });

        return root;
    }
}
