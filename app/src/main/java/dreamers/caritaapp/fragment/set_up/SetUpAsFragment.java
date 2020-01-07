package dreamers.caritaapp.fragment.set_up;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import dreamers.caritaapp.R;
import dreamers.caritaapp.activity.LoginActivity;
import dreamers.caritaapp.database.SessionHandler;

/**
 * A simple {@link Fragment} subclass.
 */
public class SetUpAsFragment extends Fragment {

    View root;
    SessionHandler session;

    public SetUpAsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_set_up_as, container, false);
        session = new SessionHandler(getActivity());

        ImageView image_charity = root.findViewById(R.id.image_charity);
        ImageView image_philanthropist = root.findViewById(R.id.image_philanthropist);
        TextView btn_switch_account = root.findViewById(R.id.btn_switch_account);

        image_charity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(new SetUpAsFragment()).commit();
                getFragmentManager().beginTransaction().replace(R.id.fragment,new CharitySetUp1Fragment()).addToBackStack(null).commit();
            }
        });

        image_philanthropist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(new SetUpAsFragment()).commit();
                getFragmentManager().beginTransaction().replace(R.id.fragment,new PhilanthropistSetUp1Fragment()).addToBackStack(null).commit();
            }
        });

        btn_switch_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session.logoutUser();
                Intent i = new Intent(getActivity(), LoginActivity.class);
                startActivity(i);
            }
        });

        return root;
    }
}
