package dreamers.caritaapp.fragment.home;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import dreamers.caritaapp.R;
import dreamers.caritaapp.fragment.home.settings.ProfileFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    View root;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_settings, container, false);

        TextView btn_to_profile = root.findViewById(R.id.btn_to_profile);
        btn_to_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(new SettingsFragment()).commit();
                getFragmentManager().beginTransaction().replace(R.id.fragment2, new ProfileFragment()).commit();
            }
        });

        return root;
    }

}
