package ie.ul.cs4084finalproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EditAdvertisementActivity extends AppCompatActivity {

    private final String TAG = "EditAdvertActivity";

    final int REQUEST_IMAGE_CAPTURE = 4;
    final int WRITE_STORAGE_PERMISSION = 5;

    private FirebaseFirestore db;
    private StorageReference mStorageRef;
    private FirebaseUser user;

    FloatingActionButton editBtn;
    Button ch, tp, locationBtn;

    ImageView img;
    public Uri imguri;
    String imageName = "";
    boolean updatedImage = false;

    // Advertisement data
    private String advertisementID;
    private Advertisement currentAd;

    // Form datas
    String title;
    String description;
    String quality;
    String price;
    int distance;
    LatLng location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_advertisement);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        img = findViewById(R.id.ea_upload_img_view);

        editBtn = findViewById(R.id.ea_submitAdvertisement);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editAdvertisement();
            }
        });

        locationBtn = findViewById(R.id.ea_locationBtn);
        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GoogleMapsActivity.class);
                intent.putExtra("lat", currentAd.getLocation().latitude);
                intent.putExtra("lng", currentAd.getLocation().longitude);
                startActivityForResult(intent, 3);
            }
        });

        ch = findViewById(R.id.ea_choose_file_btn);
        ch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileChooser();
            }
        });

        tp = findViewById(R.id.ea_take_picture_btn);
        tp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TakePicture();
            }
        });

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
                                        (double)document.get("price"),
                                        document.get("quality").toString(),
                                        ((Long)document.get("distance")).intValue(),
                                        document.get("seller").toString(),
                                        document.get("description").toString()
                                );
                                currentAd.setLocation(new LatLng((double)document.get("coord_lat"), (double)document.get("coord_lng")));
                                location = new LatLng((double)document.get("coord_lat"), (double)document.get("coord_lng"));
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
        StorageReference img = mStorageRef.child(currentAd.getImageUrl());

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

    private void editAdvertisement() {
        editBtn.setEnabled(false);

        // Place views into variables
        TextView titleView = findViewById(R.id.ea_title);
        TextView descView = findViewById(R.id.ea_adDesc);
        RadioGroup qualityRadioGroup = findViewById(R.id.ea_adQualityRadioGroup);
        RadioButton checkedRadio;
        TextView priceView = findViewById(R.id.ea_adPrice);

        int selectedRadioId = qualityRadioGroup.getCheckedRadioButtonId();

        // Place view titles into variables
        TextView titleTitle = findViewById(R.id.ea_adTitle);
        TextView descTitle = findViewById(R.id.ea_adDescriptionTitle);
        TextView imageTitle = findViewById(R.id.ea_adImageTitle);
        TextView qualityTitle = findViewById(R.id.ea_adQualityTitle);
        TextView priceTitle = findViewById(R.id.ea_adPriceTitle);
        TextView mapTitle = findViewById(R.id.ea_adMapTitle);

        if(findViewById(selectedRadioId)==null){
            qualityTitle.setTextColor(Color.RED);
            editBtn.setEnabled(true);
            return;
        }
        checkedRadio = findViewById(selectedRadioId);

        // Put values into variables
        title = titleView.getText().toString();
        description = descView.getText().toString();
        quality = checkedRadio.getText().toString();
        price = priceView.getText().toString();
        distance = 10;

        boolean error = false;

        int primary = getResources().getColor(R.color.colorPrimaryDark);

        // Reset titles
        titleTitle.setTextColor(primary);
        descTitle.setTextColor(primary);
        imageTitle.setTextColor(primary);
        qualityTitle.setTextColor(primary);
        priceTitle.setTextColor(primary);
        mapTitle.setTextColor(primary);

        // Check that all supplied information is valid
        if(title.isEmpty()) {
            titleTitle.setTextColor(Color.RED);
            error = true;
        }
        if(description.isEmpty()) {
            descTitle.setTextColor(Color.RED);
            error = true;
        }
        if(!quality.equals("Brand New") && !quality.equals("Used")) {
            Log.d(TAG, "Quality : " + quality);
            qualityTitle.setTextColor(Color.RED);
            error = true;
        }
        if(price.isEmpty()) {
            priceTitle.setTextColor(Color.RED);
            error = true;
        }
        if(currentAd.getLocation().latitude == 53.0 && currentAd.getLocation().longitude == -8.0){
            mapTitle.setTextColor(Color.RED);
            Toast.makeText(this, "Please select a location on the map", Toast.LENGTH_SHORT).show();
            error = true;
        }

        if(error) {
            editBtn.setEnabled(true);
            return;
        }

        Log.d(TAG, "editAdvertisement: imageUploaded " + updatedImage);

        // Upload the new image to the db if it has been changed
        if(updatedImage) {
            imageName = System.currentTimeMillis() + "-" + user.getUid() + "." + getExtension(imguri);
            StorageReference ref = mStorageRef.child(imageName);

            // Compress Image
            Bitmap mSelectImage;
            try {
                mSelectImage = decodeUri(this, imguri, 1080);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Log.d(TAG, "mSelectImage.size : " + mSelectImage.getByteCount());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            mSelectImage.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
            byte[] data = byteArrayOutputStream.toByteArray();

            Log.d(TAG, "data.length() : " + data.length);

            ref.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            editAdvertisementDocument();
                            remove_current_image();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Upload FAILED!", Toast.LENGTH_SHORT).show();
                            editBtn.setEnabled(true);
                        }
                    });
        }
        editAdvertisementDocument();
    }

    private void editAdvertisementDocument() {
        HashMap<String, Object> ad_data = new HashMap<>();
        boolean change_made = false;

        // Check for changed data
        if(!currentAd.getTitle().equals(title)){
            ad_data.put("title", title);
            change_made = true;
        }
        if(!currentAd.getDescription().equals(description)){
            ad_data.put("description", description);
            change_made = true;
        }
        if(!String.valueOf(currentAd.getPrice()).equals(price)){
            ad_data.put("price", price);
            change_made = true;
        }
        if(!currentAd.getQuality().equals(quality)){
            ad_data.put("quality", quality);
            change_made = true;
        }

        // Image
        if(updatedImage) {
            ad_data.put("image_src", imageName);
            change_made = true;
        }

        // Map
        if(!currentAd.getLocation().equals(location)){
            ad_data.put("coord_lat", location.latitude);
            ad_data.put("coord_lng", location.longitude);
            change_made = true;
        }


        if(change_made) {
            db.collection("advertisements")
                    .document(advertisementID)
                    .update(ad_data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(EditAdvertisementActivity.this, "Advertisement Updated", Toast.LENGTH_SHORT).show();

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    finish();
                                }
                            }, 1500);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error editing document", e);
                            Toast.makeText(EditAdvertisementActivity.this, "Advertisement not Updated!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            editBtn.setEnabled(true);
                        }
                    });
        } else {
            Toast.makeText(EditAdvertisementActivity.this, "Please make an edit before trying to update your advertisement.", Toast.LENGTH_SHORT).show();
            editBtn.setEnabled(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imguri = data.getData();
            img.setImageURI(imguri);
            updatedImage = true;
        }

        if(requestCode == 3 && resultCode == 55) {
            location = new LatLng(data.getDoubleExtra("lat", 53.0), data.getDoubleExtra("lng", -8.0));
        }

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            img.setImageBitmap(imageBitmap);
            imguri = getImageUri(this, imageBitmap);
            updatedImage = true;
        }
    }

    // ***** Image Handling Functions *****
    // Open file chooser dialog for selecting an image
    private void FileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    // Creates a Bitmap image from a URI and scales it down to a preferred size
    public static Bitmap decodeUri(Context c, Uri uri, final int requiredSize) throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o);

        int width_tmp = o.outWidth
                , height_tmp = o.outHeight;
        int scale = 1;

        while(true) {
            if(width_tmp / 2 < requiredSize || height_tmp / 2 < requiredSize)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o2);
    }

    // turns a Bitmap image into its URI version
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        int img_quality = 50;
        inImage.compress(Bitmap.CompressFormat.JPEG, img_quality, bytes);

        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "my-picture", null);
        return Uri.parse(path);
    }

    // Gets the extension of the passed image URI
    private String getExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    // Open dialog to take a picture
    private void TakePicture() {

        check_image_write_permission();

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    // Check that the user has permission to write an image to the file system
    private void check_image_write_permission(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted - Request permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_STORAGE_PERMISSION);
        }
    }

    private void remove_current_image(){
        StorageReference ref = mStorageRef.child(currentAd.getImageUrl());

        ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Log.d(TAG, "onSuccess: deleted file");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Log.d(TAG, "onFailure: did not delete file");
            }
        });
    }
}
