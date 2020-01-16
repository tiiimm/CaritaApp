package dreamers.caritaapp.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;

import dreamers.caritaapp.R;
import dreamers.caritaapp.activity.SplashScreenActivity;
import dreamers.caritaapp.database.MySingleton;

public class AdvertisementsAdapter extends RecyclerView.Adapter<AdvertisementsAdapter.ViewHolder> {

    private ArrayList<Integer> advertisement_ids;
    private ArrayList<String> advertisement_names;
    private ArrayList<String> advertisement_statuses;
    private ArrayList<Integer> advertisement_views;
    private ArrayList<String> advertisement_billing_dates;
    String user_role;

    private Context mContext;

    public AdvertisementsAdapter(Context context, ArrayList<String> names, ArrayList<String> statuses, ArrayList<String> billing_dates, ArrayList<Integer> ids, String role, ArrayList<Integer> views) {
        advertisement_ids = ids;
        advertisement_names = names;
        advertisement_views = views;
        advertisement_statuses = statuses;
        advertisement_billing_dates = billing_dates;
        user_role = role;
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
        holder.text_advertisement_views.setText("Views: "+advertisement_views.get(position));
        holder.text_advertisement_billing_date.setText("");

        holder.parentLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!user_role.matches("Administrator")){
                    return false;
                }
                CharSequence[] items = {};
                if (advertisement_statuses.get(position).matches("Pending")) {
                    items = new CharSequence[]{"Set Inactive", "Approve Company"};
                }
                else if (advertisement_statuses.get(position).matches("Active")) {
                    items = new CharSequence[]{"Set Inactive"};
                }
                else if (advertisement_statuses.get(position).matches("Inactive")) {
                    items = new CharSequence[]{"Activate"};
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setTitle("Select an Action");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:
                                if (advertisement_statuses.get(position).matches("Active")) {
                                    delete_advertisement(advertisement_ids.get(position), position);
                                }
                                else if (advertisement_statuses.get(position).matches("Inactive")) {
                                    approve_advertisement(advertisement_ids.get(position), position);
                                }
                                break;
                            case 1:
                                approve_advertisement(advertisement_ids.get(position), position);
                                break;
                            default:
                                break;
                        }
                    }
                });
                builder.show();
                return true;
            }
        });
    }

    private void approve_advertisement(Integer id, final Integer position) {
        String request = "approve_advertisement?id="+ id;

        System.out.println(request);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, new SplashScreenActivity().url+ request, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                advertisement_statuses.set(position, "Active");

                notifyItemChanged(position);
                notifyItemRangeChanged(position,advertisement_ids.size());
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
                Toast.makeText(mContext,
                        "Something went wrong", Toast.LENGTH_LONG).show();
            }
        });
        MySingleton.getInstance(mContext).addToRequestQueue(stringRequest);
    }

    private void delete_advertisement(Integer id, final Integer position) {
        String request = "delete_advertisement?id="+ id;

        System.out.println(request);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, new SplashScreenActivity().url+ request, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                advertisement_ids.remove(position);
//                advertisement_names.remove(position);
//                advertisement_statuses.remove(position);
//                advertisement_billing_dates.remove(position);
//
//                notifyItemRemoved(position);
//                notifyItemRangeChanged(position,advertisement_ids.size());
                advertisement_statuses.set(position, "Inactive");

                notifyItemChanged(position);
                notifyItemRangeChanged(position,advertisement_ids.size());
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
                Toast.makeText(mContext,
                        "Something went wrong", Toast.LENGTH_LONG).show();
            }
        });
        MySingleton.getInstance(mContext).addToRequestQueue(stringRequest);
    }

    @Override
    public int getItemCount() {
        return advertisement_ids.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView text_advertisement_name;
        TextView text_advertisement_status;
        TextView text_advertisement_views;
        TextView text_advertisement_billing_date;
        LinearLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            text_advertisement_name = itemView.findViewById(R.id.text_advertisement_name);
            text_advertisement_views = itemView.findViewById(R.id.text_advertisement_views);
            text_advertisement_status = itemView.findViewById(R.id.text_advertisement_status);
            text_advertisement_billing_date = itemView.findViewById(R.id.text_advertisement_billing_date);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
