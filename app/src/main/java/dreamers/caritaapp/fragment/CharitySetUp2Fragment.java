package dreamers.caritaapp.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.VideoView;

import dreamers.caritaapp.R;

public class CharitySetUp2Fragment extends Fragment {

    View root;
    Bundle bundle;
    ImageView image_charity_bio;
    VideoView video_charity_bio;
    EditText text_description;
    EditText text_preference;

    public CharitySetUp2Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_charity_set_up2, container, false);

        Button btn_next = root.findViewById(R.id.btn_next);
        Button btn_back = root.findViewById(R.id.btn_back);
        image_charity_bio = root.findViewById(R.id.image_charity_bio);
        video_charity_bio = root.findViewById(R.id.video_charity_bio);
        text_description = root.findViewById(R.id.text_description);
        text_preference = root.findViewById(R.id.text_preference);

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
                String description = text_description.getText().toString();
                String preference = text_preference.getText().toString();
                Boolean valid = true;
                CharitySetUp3Fragment charitySetUp3Fragment = new CharitySetUp3Fragment();
                if (description.matches("")) {
                    text_description.setError("Required");
                    valid = false;
                }
                if (preference.matches("")) {
                    text_preference.setError("Required");
                    valid = false;
                }

                if (valid) {
                    bundle.putString("description", description);
                    bundle.putString("preference", preference);
                    bundle.putString("image", "blah");
                    charitySetUp3Fragment.setArguments(bundle);

                    getFragmentManager().beginTransaction().remove(new CharitySetUp2Fragment()).commit();
                    getFragmentManager().beginTransaction().add(R.id.fragment, charitySetUp3Fragment).addToBackStack(null).commit();
                }
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharitySetUp1Fragment charitySetUp1Fragment = new CharitySetUp1Fragment();
                String description = text_description.getText().toString();
                String preference = text_preference.getText().toString();

                bundle.putString("description", description);
                bundle.putString("preference", preference);
                bundle.putString("image", "blah");
                charitySetUp1Fragment.setArguments(bundle);

                getFragmentManager().beginTransaction().remove(new CharitySetUp2Fragment()).commit();
                getFragmentManager().beginTransaction().add(R.id.fragment, charitySetUp1Fragment).addToBackStack(null).commit();
            }
        });
        return root;
    }

    public void configure() {
        if(bundle.getString("description") != null && bundle.getString("preference") != null && bundle.getString("image") != null){
            text_description.setText(bundle.getString("description"));
            text_preference.setText(bundle.getString("preference"));
        }
    }
}
