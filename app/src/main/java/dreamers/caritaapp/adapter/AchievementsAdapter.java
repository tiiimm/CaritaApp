package dreamers.caritaapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;

import java.util.ArrayList;

import dreamers.caritaapp.R;

public class AchievementsAdapter extends RecyclerView.Adapter<AchievementsAdapter.ViewHolder> {

    private ArrayList<Integer> achievement_ids;
    private ArrayList<String> achievement_titles;
    private ArrayList<String> achievement_photos;
    private ArrayList<String> achievement_dates;
    private ArrayList<String> achievement_venues;

    private Context mContext;

    public AchievementsAdapter(Context context, ArrayList<String> titles, ArrayList<String> dates, ArrayList<String> venues, ArrayList<Integer> ids, ArrayList<String> photos) {
        achievement_ids = ids;
        achievement_titles = titles;
        achievement_photos = photos;
        achievement_dates = dates;
        achievement_venues = venues;
        mContext = context;
    }

    @Override
    public AchievementsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_achievements, parent, false);
        AchievementsAdapter.ViewHolder holder = new AchievementsAdapter.ViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(AchievementsAdapter.ViewHolder holder, final int position) {

        Glide.with(mContext)
                .load(MediaManager.get().url().generate(achievement_photos.get(position)))
                .into(holder.image_achievement);
        holder.text_achievement_title.setText(achievement_titles.get(position));
        holder.text_achievement_date.setText(achievement_dates.get(position));
        holder.text_achievement_venue.setText(achievement_venues.get(position));

//        holder.parentLayout.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                final CharSequence[] items = {"Edit", "Delete"};
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//
//                builder.setTitle("Select The Action");
//                builder.setItems(items, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int item) {
//                        switch (item) {
//                            case 0:
//                                Intent intent = new Intent(mContext, CharityAchievementDetailsActivity.class);
//                                intent.putExtra("achievement_id", achievement_ids.get(position));
//                                intent.putExtra("achievement_title", achievement_titles.get(position));
//                                intent.putExtra("achievement_description", achievement_descriptions.get(position));
//                                intent.putExtra("achievement_held_on", achievement_dates.get(position));
//                                mContext.startActivity(intent);
//                                break;
//                            case 1:
//                                delete_achievement(achievement_ids.get(position));
//                                break;
//                            default:
//                                break;
//                        }
//                    }
//                });
//                builder.show();
//                return true;
//            }
//        });
    }


    @Override
    public int getItemCount() {
        return achievement_ids.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image_achievement;
        TextView text_achievement_title;
        TextView text_achievement_date;
        TextView text_achievement_venue;
        LinearLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            image_achievement = itemView.findViewById(R.id.image_achievement);
            text_achievement_title = itemView.findViewById(R.id.text_achievement_title);
            text_achievement_date = itemView.findViewById(R.id.text_achievement_date);
            text_achievement_venue = itemView.findViewById(R.id.text_achievement_venue);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
