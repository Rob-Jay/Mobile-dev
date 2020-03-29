package ie.ul.cs4084finalproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class SearchRecyclerViewAdapter extends RecyclerView.Adapter<SearchRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "SearchRecyclerView";

    private StorageReference ref;
    private ViewHolder mHolder = null;

    private ArrayList<Advertisement> ads;

    public SearchRecyclerViewAdapter(ArrayList<Advertisement> advertisements) {
        ref = FirebaseStorage.getInstance().getReference();
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
        this.mHolder = holder;
        Log.d(TAG, "onBindViewHolder: called.");

        holder.title.setText(ads.get(position).getTitle());
        // TODO : Use glide to load advertisement image from internet
        holder.price.setText(String.valueOf(ads.get(position).getPrice()));
        holder.quality.setText(ads.get(position).getQuality());
        holder.distance.setText(String.valueOf(ads.get(position).getDistance()));
        holder.seller.setText(ads.get(position).getSeller());

        if(!ads.get(position).getImageUrl().equals("")){
            Log.d(TAG, "Loading image : " + ads.get(position).getImageUrl());
            StorageReference img = ref.child(ads.get(position).getImageUrl());

            // MAx image size 5MB
            final int STORAGE_BUFFER = (1024*1024) * 5;

            img.getBytes(STORAGE_BUFFER).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    // Data for "images/island.jpg" is returns, use this as needed
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    ImageView image = mHolder.image;

                    image.setImageBitmap(Bitmap.createScaledBitmap(bmp, image.getWidth(),
                            image.getHeight(), false));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // TODO : load placeholder image in-case image could not be loaded
                }
            });
        }
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
