package dreamers.caritaapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import dreamers.caritaapp.R;

public class AdvertisementsAdapter extends RecyclerView.Adapter<AdvertisementsAdapter.ViewHolder> {

    private ArrayList<Integer> advertisement_ids;
    private ArrayList<String> advertisement_names;
    private ArrayList<String> advertisement_statuses;
    private ArrayList<String> advertisement_billing_dates;

    private Context mContext;

    public AdvertisementsAdapter(Context context, ArrayList<String> names, ArrayList<String> statuses, ArrayList<String> billing_dates, ArrayList<Integer> ids) {
        advertisement_ids = ids;
        advertisement_names = names;
        advertisement_statuses = statuses;
        advertisement_billing_dates = billing_dates;
        mContext = context;
    }

    @Override
    public AdvertisementsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_advertisements, parent, false);
        AdvertisementsAdapter.ViewHolder holder = new AdvertisementsAdapter.ViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(AdvertisementsAdapter.ViewHolder holder, final int position) {
        holder.text_advertisement_name.setText(advertisement_names.get(position));
        holder.text_advertisement_status.setText(advertisement_statuses.get(position));
        holder.text_advertisement_billing_date.setText(advertisement_billing_dates.get(position));
    }


    @Override
    public int getItemCount() {
        return advertisement_ids.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView text_advertisement_name;
        TextView text_advertisement_status;
        TextView text_advertisement_billing_date;
        LinearLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            text_advertisement_name = itemView.findViewById(R.id.text_advertisement_name);
            text_advertisement_status = itemView.findViewById(R.id.text_advertisement_status);
            text_advertisement_billing_date = itemView.findViewById(R.id.text_advertisement_billing_date);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
