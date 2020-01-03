package dreamers.caritaapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import dreamers.caritaapp.R;
import dreamers.caritaapp.fragment.SetUpAsFragment;

public class SetUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);
    }

    @Override
    public void onBackPressed() {
    }
}
