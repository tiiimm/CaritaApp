package dreamers.caritaapp.fragment.set_up;


import android.Manifest;
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
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;
import dreamers.caritaapp.R;

import static android.app.Activity.RESULT_OK;

public class PhilanthropistSetUp2Fragment extends Fragment {

    View root;
    Bundle bundle;
    CircleImageView image_philanthropist;
    String image_path = "";

    public PhilanthropistSetUp2Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_philanthropist_set_up2, container, false);

        image_philanthropist = root.findViewById(R.id.image_profile_picture);
        Button btn_next = root.findViewById(R.id.btn_next);
        Button btn_back = root.findViewById(R.id.btn_back);

        bundle = getArguments();
        if (bundle != null) {
            System.out.println(bundle);
            configure();
        }
        else
            bundle = new Bundle();

        image_philanthropist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                }
                else {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 1);
                }
            }
        });
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhilanthropistSetUp3Fragment philanthropistSetUp3Fragment = new PhilanthropistSetUp3Fragment();

                bundle.putString("image_philanthropist", image_path);
                philanthropistSetUp3Fragment.setArguments(bundle);

                getFragmentManager().beginTransaction().remove(new PhilanthropistSetUp2Fragment()).commit();
                getFragmentManager().beginTransaction().replace(R.id.fragment, philanthropistSetUp3Fragment).commit();
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhilanthropistSetUp1Fragment philanthropistSetUp1Fragment = new PhilanthropistSetUp1Fragment();

                bundle.putString("image_philanthropist", image_path);
                philanthropistSetUp1Fragment.setArguments(bundle);

                getFragmentManager().beginTransaction().remove(new PhilanthropistSetUp2Fragment()).commit();
                getFragmentManager().beginTransaction().replace(R.id.fragment, philanthropistSetUp1Fragment).commit();
            }
        });

        return root;
    }

    private void configure() {
        if(bundle.getString("image_philanthropist") != null){
            image_path = bundle.getString("image_philanthropist");

            if (!image_path.matches(""))
                image_philanthropist.setImageBitmap(BitmapFactory.decodeFile(bundle.getString("image_philanthropist")));
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if(resultCode == RESULT_OK){
            Uri selected = imageReturnedIntent.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getActivity().getContentResolver().query(selected, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            image_path = cursor.getString(columnIndex);
            cursor.close();
            image_philanthropist.setImageBitmap(BitmapFactory.decodeFile(image_path));
        }
        else
            image_path = "";
    }
}
