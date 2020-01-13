package dreamers.caritaapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import dreamers.caritaapp.R;
import dreamers.caritaapp.fragment.set_up.CharitySetUp1Fragment;
import dreamers.caritaapp.fragment.set_up.CompanySetUp1Fragment;
import dreamers.caritaapp.fragment.set_up.PhilanthropistSetUp1Fragment;
import dreamers.caritaapp.fragment.set_up.SetUpAsFragment;

public class SetUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);

        try{
            Intent i = getIntent();
            String role = i.getStringExtra("role");
            if (role.matches("Temporary Philanthropist")){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment,new PhilanthropistSetUp1Fragment()).commit();
            }
            if (role.matches("Temporary Charity")){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment,new CharitySetUp1Fragment()).commit();
            }
            if (role.matches("Temporary Company")){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment,new CompanySetUp1Fragment()).commit();
            }
        }
        catch (Exception e) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment,new SetUpAsFragment()).commit();
        }
    }
}
