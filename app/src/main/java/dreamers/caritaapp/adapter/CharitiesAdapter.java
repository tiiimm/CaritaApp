package dreamers.caritaapp.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import dreamers.caritaapp.R;
import dreamers.caritaapp.activity.LoginActivity;
import dreamers.caritaapp.activity.SplashScreenActivity;
import dreamers.caritaapp.database.MySingleton;
import dreamers.caritaapp.fragment.home.settings.ProfileFragment;
import dreamers.caritaapp.fragment.set_up.CharitySetUp3Fragment;

public class CharitiesAdapter extends RecyclerView.Adapter<CharitiesAdapter.ViewHolder> {

    private ArrayList<Integer> charity_ids;
    private ArrayList<Integer> charity_user_ids;
    private ArrayList<String> charity_names;
    private ArrayList<String> charity_statuses;
    private ArrayList<String> charity_photos;
    private ArrayList<String> charity_addresss;
    private ArrayList<String> charity_contacts;
    private ArrayList<Integer> charity_points;
    String user_role;
    ViewHolder viewHolder;

    private Context mContext;

    public CharitiesAdapter(Context context, ArrayList<String> names, ArrayList<String> addresss, ArrayList<String> contacts, ArrayList<Integer> ids, ArrayList<String> photos, ArrayList<Integer> user_ids, ArrayList<Integer> points, ArrayList<String> statuses, String role) {
        charity_ids = ids;
        charity_user_ids = user_ids;
        charity_points = points;
        charity_names = names;
        charity_statuses = statuses;
        charity_photos = photos;
        charity_addresss = addresss;
        charity_contacts = contacts;
        user_role = role;
        mContext = context;
    }

    @Override
    public CharitiesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_charities, parent, false);
        CharitiesAdapter.ViewHolder holder = new CharitiesAdapter.ViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(final CharitiesAdapter.ViewHolder holder, final int position) {
        viewHolder = holder;
        Glide.with(mContext)
                .load(MediaManager.get().url().generate(charity_photos.get(position)))
                .into(holder.image_charity);
        holder.text_charity_name.setText(charity_names.get(position));
        holder.text_charity_address.setText(charity_addresss.get(position));
        holder.text_charity_contact.setText(charity_contacts.get(position));
        if (user_role.matches("Administrator")){
            holder.text_charity_status.setText("Status: "+charity_statuses.get(position));
        }

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!charity_statuses.get(position).matches("Active")) {
                    return;
                }
                Bundle bundle = new Bundle();
                ProfileFragment profileFragment = new ProfileFragment();

                bundle.putInt("user_id", charity_user_ids.get(position));
                bundle.putString("name", charity_names.get(position));
                bundle.putString("username", "");
                bundle.putString("photo", charity_photos.get(position));
                bundle.putString("role", "Charity");
                bundle.putString("points", charity_points.get(position).toString());
                profileFragment.setArguments(bundle);

                AppCompatActivity appCompatActivity = (AppCompatActivity) mContext;
                appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment2, profileFragment).commit();
            }
        });

        holder.parentLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!user_role.matches("Administrator")){
                    return false;
                }
                CharSequence[] items = {};
                if (charity_statuses.get(position).matches("Pending")) {
                    items = new CharSequence[]{"Reject Charity", "Approve Charity"};
                }
                else {
                    items = new CharSequence[]{"Reject Charity"};
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setTitle("Select an Action");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:
                                delete_charity(charity_ids.get(position), position);
                                break;
                            case 1:
                                approve_charity(charity_ids.get(position), position);
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

    private void approve_charity(Integer id, final Integer position) {
        String request = "approve_charity?id="+ id;

        System.out.println(request);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, new SplashScreenActivity().url+ request, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                charity_statuses.set(position, "Active");

                notifyItemChanged(position);
                notifyItemRangeChanged(position,charity_ids.size());
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

    private void delete_charity(Integer id, final Integer position) {
        String request = "delete_charity?id="+ id;

        System.out.println(request);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, new SplashScreenActivity().url+ request, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                charity_ids.remove(position);
                charity_user_ids.remove(position);
                charity_names.remove(position);
                charity_statuses.remove(position);
                charity_photos.remove(position);
                charity_addresss.remove(position);
                charity_contacts.remove(position);
                charity_points.remove(position);

                notifyItemRemoved(position);
                notifyItemRangeChanged(position,charity_ids.size());
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
        return charity_ids.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image_charity;
        TextView text_charity_name;
        TextView text_charity_address;
        TextView text_charity_contact;
        TextView text_charity_status;
        LinearLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            image_charity = itemView.findViewById(R.id.image_charity);
            text_charity_name = itemView.findViewById(R.id.text_charity_name);
            text_charity_address = itemView.findViewById(R.id.text_charity_address);
            text_charity_contact = itemView.findViewById(R.id.text_charity_contact);
            text_charity_status = itemView.findViewById(R.id.text_charity_status);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
