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

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    private ArrayList<Integer> event_ids;
    private ArrayList<String> event_titles;
    private ArrayList<String> event_photos;
    private ArrayList<String> event_dates;
    private ArrayList<String> event_venues;

    private Context mContext;

    public EventsAdapter(Context context, ArrayList<String> titles, ArrayList<String> dates, ArrayList<String> venues, ArrayList<Integer> ids, ArrayList<String> photos) {
        event_ids = ids;
        event_titles = titles;
        event_photos = photos;
        event_dates = dates;
        event_venues = venues;
        mContext = context;
    }

    @Override
    public EventsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_events, parent, false);
        EventsAdapter.ViewHolder holder = new EventsAdapter.ViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(EventsAdapter.ViewHolder holder, final int position) {

        Glide.with(mContext)
                .load(MediaManager.get().url().generate(event_photos.get(position)))
                .into(holder.image_event);
        holder.text_event_title.setText(event_titles.get(position));
        holder.text_event_date.setText(event_dates.get(position));
        holder.text_event_venue.setText(event_venues.get(position));

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
//                                intent.putExtra("event_id", event_ids.get(position));
//                                intent.putExtra("event_title", event_titles.get(position));
//                                intent.putExtra("event_description", event_descriptions.get(position));
//                                intent.putExtra("event_held_on", event_dates.get(position));
//                                mContext.startActivity(intent);
//                                break;
//                            case 1:
//                                delete_event(event_ids.get(position));
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
        return event_ids.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image_event;
        TextView text_event_title;
        TextView text_event_date;
        TextView text_event_venue;
        LinearLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            image_event = itemView.findViewById(R.id.image_event);
            text_event_title = itemView.findViewById(R.id.text_event_title);
            text_event_date = itemView.findViewById(R.id.text_event_date);
            text_event_venue = itemView.findViewById(R.id.text_event_venue);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
