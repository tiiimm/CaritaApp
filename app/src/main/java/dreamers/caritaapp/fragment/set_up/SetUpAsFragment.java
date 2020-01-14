package dreamers.caritaapp.fragment.set_up;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

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

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        final GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        ImageView image_charity = root.findViewById(R.id.image_charity);
        ImageView image_philanthropist = root.findViewById(R.id.image_philanthropist);
        ImageView image_company = root.findViewById(R.id.image_company);
        TextView btn_switch_account = root.findViewById(R.id.btn_switch_account);

        image_charity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(new SetUpAsFragment()).commit();
                getFragmentManager().beginTransaction().replace(R.id.fragment,new CharitySetUp1Fragment()).commit();
            }
        });

        image_philanthropist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(new SetUpAsFragment()).commit();
                getFragmentManager().beginTransaction().replace(R.id.fragment,new PhilanthropistSetUp1Fragment()).commit();
            }
        });

        image_company.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(new SetUpAsFragment()).commit();
                getFragmentManager().beginTransaction().replace(R.id.fragment,new CompanySetUp1Fragment()).commit();
            }
        });

        btn_switch_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session.logoutUser();
                mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        System.out.println("Signed out");
                    }
                });
                Intent i = new Intent(getActivity(), LoginActivity.class);
                startActivity(i);
            }
        });

        return root;
    }
}
