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
        holder.text_advertisement_billing_date.setText("");

        holder.parentLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                CharSequence[] items = {};
                if (advertisement_statuses.get(position).matches("Pending")) {
                    items = new CharSequence[]{"Delete", "Approve Company"};
                }
                else {
                    items = new CharSequence[]{"Delete"};
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setTitle("Select an Action");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:
                                delete_advertisement(advertisement_ids.get(position));
                                break;
                            case 1:
                                approve_advertisement(advertisement_ids.get(position));
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

    private void approve_advertisement(Integer id) {
        String request = "approve_advertisement?id="+ id;

        System.out.println(request);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, new SplashScreenActivity().url+ request, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

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

    private void delete_advertisement(Integer id) {
        String request = "delete_advertisement?id="+ id;

        System.out.println(request);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, new SplashScreenActivity().url+ request, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

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
