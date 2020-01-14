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
import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import dreamers.caritaapp.R;
import dreamers.caritaapp.activity.SplashScreenActivity;
import dreamers.caritaapp.database.MySingleton;

public class CompaniesAdapter extends RecyclerView.Adapter<CompaniesAdapter.ViewHolder> {

    private ArrayList<Integer> company_ids;
    private ArrayList<String> company_names;
    private ArrayList<String> company_statuses;
    private ArrayList<String> company_photos;

    private Context mContext;

    public CompaniesAdapter(Context context, ArrayList<String> names, ArrayList<Integer> ids, ArrayList<String> photos, ArrayList<String> statuses) {
        company_ids = ids;
        company_names = names;
        company_statuses = statuses;
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
        holder.text_company_status.setText(company_statuses.get(position));


        holder.parentLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                CharSequence[] items = {};
                if (company_statuses.get(position).matches("Pending")) {
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
                                delete_company(company_ids.get(position), position);
                                break;
                            case 1:
                                approve_company(company_ids.get(position), position);
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

    private void approve_company(Integer id, final Integer position) {
        String request = "approve_company?id="+ id;

        System.out.println(request);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, new SplashScreenActivity().url+ request, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                company_statuses.set(position, "Active");

                notifyItemChanged(position);
                notifyItemRangeChanged(position,company_ids.size());
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

    private void delete_company(Integer id, final Integer position) {
        String request = "delete_company?id="+ id;

        System.out.println(request);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, new SplashScreenActivity().url+ request, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                company_ids.remove(position);
                company_names.remove(position);
                company_statuses.remove(position);
                company_photos.remove(position);

                notifyItemRemoved(position);
                notifyItemRangeChanged(position,company_ids.size());
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
        return company_ids.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView image_company;
        TextView text_company_name;
        TextView text_company_status;
        LinearLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            image_company = itemView.findViewById(R.id.image_company);
            text_company_name = itemView.findViewById(R.id.text_company_name);
            text_company_status = itemView.findViewById(R.id.text_company_status);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
