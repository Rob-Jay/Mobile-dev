package ie.ul.cs4084finalproject;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SearchRecyclerViewAdapter extends RecyclerView.Adapter<SearchRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "SearchRecyclerView";

    private ArrayList<Advertisement> ads;

    public SearchRecyclerViewAdapter(ArrayList<Advertisement> advertisements) {
        ads = advertisements;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.advertisement_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        holder.title.setText(ads.get(position).getTitle());
        // TODO : Use glide to load advertisement image from internet
        holder.price.setText(String.valueOf(ads.get(position).getPrice()));
        holder.quality.setText(ads.get(position).getQuality());
        holder.distance.setText(String.valueOf(ads.get(position).getDistance()));
        holder.seller.setText(ads.get(position).getSeller());
    }

    @Override
    public int getItemCount() {
        return ads.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView title;
        TextView price;
        TextView quality;
        TextView distance;
        TextView seller;
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View adView) {
            super(adView);

            image = adView.findViewById(R.id.advertisementImage);
            title = adView.findViewById(R.id.advertisementTitle);
            price = adView.findViewById(R.id.advertisementPrice);
            quality = adView.findViewById(R.id.advertisementQuality);
            distance = adView.findViewById(R.id.advertisementDistance);
            seller = adView.findViewById(R.id.advertisementSeller);
            parentLayout = adView.findViewById(R.id.parentLayout);
        }
    }
}
