package ie.ul.cs4084finalproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ViewAdvertisementActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final String TAG = "ViewAdActivity";

    private GoogleMap mMap;
    private String advertisement_id;

    FirebaseFirestore db;
    StorageReference ref;

    Advertisement currentAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_advertisement);

        db = FirebaseFirestore.getInstance();
        ref = FirebaseStorage.getInstance().getReference();

        getIncomingIntent();

        ImageView backBtn = findViewById(R.id.va_backArrow);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        loadAdvertisementData();

        Button purchase = findViewById(R.id.va_purchaseBtn);
        purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), StripePaymentActivity.class);
                Log.d(TAG, "onClick: ad_id : " + advertisement_id);
                Log.d(TAG, "onClick: price : " + currentAd.getPrice());
                intent.putExtra("advertisement_id", advertisement_id);
                intent.putExtra("price", currentAd.getPrice());
                startActivityForResult(intent, 7);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 7 && resultCode == 7) {
            if(data.getBooleanExtra("result", false)){
                // Payment success
                Log.d(TAG, "onActivityResult: Success Payment");
            } else {
                // Payment failure
                Log.d(TAG, "onActivityResult: Failed Payment");
            }
        }
    }

    private void getIncomingIntent(){
        Log.d(TAG, "getIncomingIntent: checking for incoming intents.");

        if(getIntent().hasExtra("advertisement_id")){
            Log.d(TAG, "getIncomingIntent: found intent extras.");

            advertisement_id = getIntent().getStringExtra("advertisement_id");
        }
    }

    @Override
    public void onMapReady(GoogleMap map){
        mMap = map;
    }

    private void setupMap() {
        mMap.addMarker(new MarkerOptions().position(
                currentAd.getLocation()
        ).title("Marker"));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentAd.getLocation(), 12));
    }

    private void loadAdvertisementData() {
        db.collection("advertisements")
                .document(advertisement_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if(document.getData() != null) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                currentAd = new Advertisement(
                                        document.get("title").toString(),
                                        document.get("image_src").toString(),
                                        (Double)document.get("price"),
                                        document.get("quality").toString(),
                                        ((Long)document.get("distance")).intValue(),
                                        document.get("seller").toString(),
                                        document.get("description").toString()
                                );
                                currentAd.setLocation(new LatLng((double)document.get("coord_lat"), (double)document.get("coord_lng")));
                                setupMap();
                                displayAdvertisementData();
                            } else {
                                Log.d(TAG, "Document could not be found");
                                Toast.makeText(getApplicationContext(), "Advertisement could not be loaded!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d(TAG, "Error getting documents : ", task.getException());
                        }
                    }
                });
    }

    private void displayAdvertisementData() {
        TextView title = findViewById(R.id.va_adTitle);
        TextView description = findViewById(R.id.va_description);
        TextView quality = findViewById(R.id.va_quality);
        TextView price = findViewById(R.id.va_adPrice);
        TextView seller = findViewById(R.id.va_sellerDetails);
        final ImageView image = findViewById(R.id.va_mainImage);

        title.setText(currentAd.getTitle());
        description.setText(currentAd.getDescription());
        quality.setText(currentAd.getQuality());
        price.setText(String.valueOf(currentAd.getPrice()));
        seller.setText(currentAd.getSeller());

        // Load advertisement image
        StorageReference img = ref.child(currentAd.getImageUrl());

        // Max image size 5MB
        final int STORAGE_BUFFER = (1024*1024) * 5;

        img.getBytes(STORAGE_BUFFER).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                image.setImageBitmap(Bitmap.createScaledBitmap(bmp, image.getWidth(),
                        image.getHeight(), false));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                image.setImageResource(R.drawable.cloud_upload);
            }
        });
    }

}
