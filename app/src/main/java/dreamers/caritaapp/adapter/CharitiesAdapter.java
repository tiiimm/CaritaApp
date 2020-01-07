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

public class CharitiesAdapter extends RecyclerView.Adapter<CharitiesAdapter.ViewHolder> {

    private ArrayList<Integer> charity_ids;
    private ArrayList<String> charity_names;
    private ArrayList<String> charity_photos;
    private ArrayList<String> charity_addresss;
    private ArrayList<String> charity_contacts;

    private Context mContext;

    public CharitiesAdapter(Context context, ArrayList<String> names, ArrayList<String> addresss, ArrayList<String> contacts, ArrayList<Integer> ids, ArrayList<String> photos) {
        charity_ids = ids;
        charity_names = names;
        charity_photos = photos;
        charity_addresss = addresss;
        charity_contacts = contacts;
        mContext = context;
    }

    @Override
    public CharitiesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_charities, parent, false);
        CharitiesAdapter.ViewHolder holder = new CharitiesAdapter.ViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(CharitiesAdapter.ViewHolder holder, final int position) {

        Glide.with(mContext)
                .load(MediaManager.get().url().generate(charity_photos.get(position)))
                .into(holder.image_charity);
        holder.text_charity_name.setText(charity_names.get(position));
        holder.text_charity_address.setText(charity_addresss.get(position));
        holder.text_charity_contact.setText(charity_contacts.get(position));

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
//                                intent.putExtra("charity_id", charity_ids.get(position));
//                                intent.putExtra("charity_name", charity_names.get(position));
//                                intent.putExtra("charity_description", charity_descriptions.get(position));
//                                intent.putExtra("charity_held_on", charity_addresss.get(position));
//                                mContext.startActivity(intent);
//                                break;
//                            case 1:
//                                delete_charity(charity_ids.get(position));
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
        return charity_ids.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image_charity;
        TextView text_charity_name;
        TextView text_charity_address;
        TextView text_charity_contact;
        LinearLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            image_charity = itemView.findViewById(R.id.image_charity);
            text_charity_name = itemView.findViewById(R.id.text_charity_name);
            text_charity_address = itemView.findViewById(R.id.text_charity_address);
            text_charity_contact = itemView.findViewById(R.id.text_charity_contact);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
