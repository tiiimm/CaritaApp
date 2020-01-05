package dreamers.caritaapp.fragment.home.home;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dreamers.caritaapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeAchievementsFragment extends Fragment {


    public HomeAchievementsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_achievements, container, false);
    }

}
