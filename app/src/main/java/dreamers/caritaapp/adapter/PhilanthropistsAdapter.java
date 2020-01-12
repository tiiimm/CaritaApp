package dreamers.caritaapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import dreamers.caritaapp.R;

public class PhilanthropistsAdapter extends RecyclerView.Adapter<PhilanthropistsAdapter.ViewHolder> {

    private ArrayList<Integer> philanthropist_ids;
    private ArrayList<String> philanthropist_names;
    private ArrayList<String> philanthropist_usernames;
    private ArrayList<String> philanthropist_points;
    private ArrayList<String> philanthropist_photos;

    private Context mContext;

    public PhilanthropistsAdapter(Context context, ArrayList<String> names, ArrayList<String> usernamees, ArrayList<String> points, ArrayList<Integer> ids, ArrayList<String> photos) {
        philanthropist_ids = ids;
        philanthropist_names = names;
        philanthropist_usernames = usernamees;
        philanthropist_points = points;
        philanthropist_photos = photos;
        mContext = context;
    }

    @Override
    public PhilanthropistsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_philanthropists, parent, false);
        PhilanthropistsAdapter.ViewHolder holder = new PhilanthropistsAdapter.ViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(PhilanthropistsAdapter.ViewHolder holder, final int position) {
        Glide.with(mContext)
                .load(MediaManager.get().url().generate(philanthropist_photos.get(position)))
                .into(holder.image_philanthropist);
        holder.text_philanthropist_name.setText(philanthropist_names.get(position));
        holder.text_philanthropist_username.setText(philanthropist_usernames.get(position));
        holder.text_philanthropist_point.setText(philanthropist_points.get(position));
    }


    @Override
    public int getItemCount() {
        return philanthropist_ids.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView image_philanthropist;
        TextView text_philanthropist_name;
        TextView text_philanthropist_username;
        TextView text_philanthropist_point;
        LinearLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            image_philanthropist = itemView.findViewById(R.id.image_philanthropist);
            text_philanthropist_name = itemView.findViewById(R.id.text_philanthropist_name);
            text_philanthropist_username = itemView.findViewById(R.id.text_philanthropist_username);
            text_philanthropist_point = itemView.findViewById(R.id.text_philanthropist_point);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
