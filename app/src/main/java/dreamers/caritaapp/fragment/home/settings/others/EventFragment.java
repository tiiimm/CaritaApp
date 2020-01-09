package dreamers.caritaapp.fragment.home.settings.others;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import dreamers.caritaapp.R;
import dreamers.caritaapp.database.SessionHandler;
import dreamers.caritaapp.database.User;

public class EventFragment extends Fragment implements RewardedVideoAdListener {

    View root;
    SessionHandler sessionHandler;
    User user;
    Bundle bundle;
    RewardedVideoAd mRewardedVideoAd;

    ImageView image_event;
    EditText text_title;
    EditText text_description;
    EditText text_date;
    EditText text_venue;
    EditText text_open_until;

    public EventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_event, container, false);
        sessionHandler = new SessionHandler(getActivity());
        user = sessionHandler.getUserDetails();

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(getActivity());
        mRewardedVideoAd.setRewardedVideoAdListener(this);

        loadRewardedVideoAd();

        bundle = getArguments();

        configure();

        Button btn_donate = root.findViewById(R.id.btn_donate);

        btn_donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRewardedVideoAd.isLoaded()) {
                    mRewardedVideoAd.show();
                }
            }
        });

        image_event = root.findViewById(R.id.image_event);
        text_title = root.findViewById(R.id.text_title);
        text_description = root.findViewById(R.id.text_description);
        text_date = root.findViewById(R.id.text_date);
        text_venue = root.findViewById(R.id.text_venue);
        text_open_until = root.findViewById(R.id.text_open_until);

        return root;
    }

    private void configure() {
        if (!bundle.getString("event_id").matches("")) {
            Glide.with(getActivity()).asBitmap().load(MediaManager.get().url().generate(bundle.getString("photo"))).into(image_event);
            text_title.setText(bundle.getString("title"));
            text_description.setText(bundle.getString("description"));
            text_date.setText(bundle.getString("date"));
            text_venue.setText(bundle.getString("venue"));
            text_open_until.setText(bundle.getString("open_until"));
        }
    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                new AdRequest.Builder().build());
    }

    @Override
    public void onRewardedVideoAdLoaded() {

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        loadRewardedVideoAd();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }

    @Override
    public void onRewardedVideoCompleted() {

    }
}
