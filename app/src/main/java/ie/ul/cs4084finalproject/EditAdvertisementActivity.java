package ie.ul.cs4084finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class EditAdvertisementActivity extends AppCompatActivity {

    private final String TAG = "EditAdvertActivity";

    private String advertisementID;
    private FirebaseFirestore db;
    private StorageReference ref;
    private Advertisement currentAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_advertisement);

        db = FirebaseFirestore.getInstance();
        ref = FirebaseStorage.getInstance().getReference();

        // Pull the required advertisement id from the intent that launched this activity
        advertisementID = getIntent().getStringExtra("advertisement_id");

        Log.d(TAG, "Advertisement ID = " + advertisementID);

        loadAdvertisementData();
    }

    private void loadAdvertisementData() {
        db.collection("advertisements")
                .document(advertisementID)
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
        TextView title = findViewById(R.id.ea_title);
        TextView desc = findViewById(R.id.ea_adDesc);
        TextView price = findViewById(R.id.ea_adPrice);
        final ImageView image = findViewById(R.id.ea_upload_img_view);


        title.setText(currentAd.getTitle());
        // TODO : Add functionality for this
        desc.setText(currentAd.getDescription());
        price.setText(String.valueOf(currentAd.getPrice()));

        if(currentAd.getQuality().equals("Brand New")) {
            RadioButton bn = findViewById(R.id.ea_adQualityNewBtn);
            bn.setChecked(true);
        } else if(currentAd.getQuality().equals("Used")) {
            RadioButton u = findViewById(R.id.ea_adQualityUsedBtn);
            u.setChecked(true);
        }

        // Load advertisement image
        StorageReference img = ref.child(currentAd.getImageUrl());

        // Max image size 5MB
        final int STORAGE_BUFFER = (1024*1024) * 1;

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
