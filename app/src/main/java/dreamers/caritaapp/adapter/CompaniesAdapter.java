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

public class CompaniesAdapter extends RecyclerView.Adapter<CompaniesAdapter.ViewHolder> {

    private ArrayList<Integer> company_ids;
    private ArrayList<String> company_names;
    private ArrayList<String> company_photos;

    private Context mContext;

    public CompaniesAdapter(Context context, ArrayList<String> names, ArrayList<Integer> ids, ArrayList<String> photos) {
        company_ids = ids;
        company_names = names;
        company_photos = photos;
        mContext = context;
    }

    @Override
    public CompaniesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_companies, parent, false);
        CompaniesAdapter.ViewHolder holder = new CompaniesAdapter.ViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(CompaniesAdapter.ViewHolder holder, final int position) {
        Glide.with(mContext)
                .load(MediaManager.get().url().generate(company_photos.get(position)))
                .into(holder.image_company);
        holder.text_company_name.setText(company_names.get(position));
    }


    @Override
    public int getItemCount() {
        return company_ids.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView image_company;
        TextView text_company_name;
        TextView text_company_address;
        LinearLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            image_company = itemView.findViewById(R.id.image_company);
            text_company_name = itemView.findViewById(R.id.text_company_name);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
