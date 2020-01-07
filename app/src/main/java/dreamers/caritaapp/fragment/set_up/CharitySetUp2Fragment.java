package dreamers.caritaapp.fragment.set_up;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
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
    String bio_path = "";
    String bio_type = "";

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

        image_charity_bio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pick_bio();
            }
        });

        video_charity_bio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pick_bio();
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (video_charity_bio.isPlaying())
                    video_charity_bio.stopPlayback();

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
                    bundle.putString("charity_bio", bio_path);
                    bundle.putString("type", bio_type);
                    charitySetUp3Fragment.setArguments(bundle);

                    getFragmentManager().beginTransaction().remove(new CharitySetUp2Fragment()).commit();
                    getFragmentManager().beginTransaction().replace(R.id.fragment, charitySetUp3Fragment).addToBackStack(null).commit();
                }
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (video_charity_bio.isPlaying())
                    video_charity_bio.stopPlayback();

                CharitySetUp1Fragment charitySetUp1Fragment = new CharitySetUp1Fragment();
                String description = text_description.getText().toString();
                String preference = text_preference.getText().toString();

                bundle.putString("description", description);
                bundle.putString("preference", preference);
                bundle.putString("charity_bio", bio_path);
                bundle.putString("type", bio_type);
                charitySetUp1Fragment.setArguments(bundle);

                getFragmentManager().beginTransaction().remove(new CharitySetUp2Fragment()).commit();
                getFragmentManager().beginTransaction().replace(R.id.fragment, charitySetUp1Fragment).addToBackStack(null).commit();
            }
        });
        return root;
    }

    private void configure() {
        if(bundle.getString("description") != null && bundle.getString("preference") != null && bundle.getString("type") != null && bundle.getString("charity_bio") != null){
            text_description.setText(bundle.getString("description"));
            text_preference.setText(bundle.getString("preference"));
            bio_type = bundle.getString("type");
            bio_path = bundle.getString("charity_bio");

            switch (bio_type) {
                case "image":
                    image_charity_bio.setVisibility(View.VISIBLE);
                    video_charity_bio.setVisibility(View.GONE);
                    image_charity_bio.setImageBitmap(BitmapFactory.decodeFile(bio_path));
                    break;
                case "video":
                    video_charity_bio.setVisibility(View.VISIBLE);
                    image_charity_bio.setVisibility(View.GONE);
                    video_charity_bio.setVideoPath(bio_path);
                    video_charity_bio.start();
                    break;
            }
        }
    }

    private void pick_bio() {

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
        else {
            Intent intent=new Intent(Intent.ACTION_PICK);
            intent.setType("image/* video/*");
            startActivityForResult(intent,1);
        }
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri selected = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getActivity().getContentResolver().query(selected, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            if (selected.toString().contains("image")) {
                bio_type = "image";
                bio_path = cursor.getString(columnIndex);
                cursor.close();
                video_charity_bio.setVisibility(View.GONE);
                image_charity_bio.setVisibility(View.VISIBLE);
                image_charity_bio.setImageBitmap(BitmapFactory.decodeFile(bio_path));
            }
            else if (selected.toString().contains("video")) {
                bio_type = "video";
                bio_path = cursor.getString(columnIndex);
                cursor.close();
                image_charity_bio.setVisibility(View.GONE);
                video_charity_bio.setVisibility(View.VISIBLE);
                video_charity_bio.setVideoPath(bio_path);
                video_charity_bio.start();
            }
        }
        else {
            bio_type = "";
            bio_path = "";
        }
    }
}
