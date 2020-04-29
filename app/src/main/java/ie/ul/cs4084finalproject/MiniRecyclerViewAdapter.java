package ie.ul.cs4084finalproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MiniRecyclerViewAdapter extends RecyclerView.Adapter<MiniRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "MiniRecyclerView";

    private StorageReference ref;
    private ArrayList<MiniRecyclerViewAdapter.ViewHolder> mHolders = new ArrayList<>();
    private Context mContext;

    private ArrayList<Advertisement> ads;

    public MiniRecyclerViewAdapter(Context c, ArrayList<Advertisement> advertisements) {
        mContext = c;
        ref = FirebaseStorage.getInstance().getReference();
        ads = advertisements;
    }

    @NonNull
    @Override
    public MiniRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.advertisement_item_small, parent, false);
        MiniRecyclerViewAdapter.ViewHolder holder = new MiniRecyclerViewAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MiniRecyclerViewAdapter.ViewHolder holder, final int position) {
        mHolders.add(holder);
        Log.d(TAG, "onBindViewHolder: called.");

        holder.advertisement_id = ads.get(position).getAdvertisement_id();

        holder.title.setText(ads.get(position).getTitle());
        String priceString = "€ " + ads.get(position).getPrice();
        holder.price.setText(priceString);
        holder.quality.setText(ads.get(position).getQuality());
        //holder.distance.setText(String.valueOf(ads.get(position).getDistance()));
        holder.distance.setVisibility(View.INVISIBLE);
        holder.seller.setText(ads.get(position).getSeller());
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, EditAdvertisementActivity.class);
                i.putExtra("advertisement_id", mHolders.get(position).advertisement_id);
                mContext.startActivity(i);
            }
        });

        if(!ads.get(position).getImageUrl().equals("")){
            Log.d(TAG, "Loading image : " + ads.get(position).getImageUrl());
            StorageReference img = ref.child(ads.get(position).getImageUrl());

            // Max image size 5MB
            final int STORAGE_BUFFER = (1024*1024) * 5;

            img.getBytes(STORAGE_BUFFER).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    // Data for "images/island.jpg" is returns, use this as needed
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    ImageView image = mHolders.get(position).image;

                    // double imgScaler = bmp.getWidth() / image.getWidth();

                    image.setImageBitmap(Bitmap.createScaledBitmap(bmp, image.getWidth(),
                            image.getHeight(), false));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return ads.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        String advertisement_id;
        ImageView image;
        TextView title;
        TextView price;
        TextView quality;
        TextView distance;
        TextView seller;
        Button editButton;
        ConstraintLayout parentLayout;

        public ViewHolder(@NonNull View adView) {
            super(adView);

            image = adView.findViewById(R.id.aim_advertisementImage);
            title = adView.findViewById(R.id.aim_advertisementTitle);
            price = adView.findViewById(R.id.aim_advertisementPrice);
            quality = adView.findViewById(R.id.aim_advertisementQuality);
            distance = adView.findViewById(R.id.aim_advertisementDistance);
            seller = adView.findViewById(R.id.aim_advertisementSeller);
            parentLayout = adView.findViewById(R.id.aim_parentLayout);
            editButton = adView.findViewById(R.id.aim_editButton);
        }
    }

}
