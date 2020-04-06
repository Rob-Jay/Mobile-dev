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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class CreateAdActivity extends AppCompatActivity {

    final String TAG = "CreateAdActivity";

    final int REQUEST_IMAGE_CAPTURE = 4;
    final int WRITE_STORAGE_PERMISSION = 5;

    private StorageReference mStorageRef;
    private FirebaseUser user;
    private FirebaseFirestore db;

    Button ch, tp, submitAd;
    ImageView img;
    boolean imageUploaded = false;
    public Uri imguri;
    String imageName = "";

    // Advertisement data
    String title;
    String description;
    String quality;
    String price;
    int distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ad);

        // Check that user is logged in

        mStorageRef = FirebaseStorage.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        if(user == null) {
            Log.e(TAG, "User in not logged in");
            System.exit(0);
        }

        ch = findViewById(R.id.ea_choose_file_btn);
        tp = findViewById(R.id.ea_take_picture_btn);
        submitAd = findViewById(R.id.ea_submitAdvertisement);
        img = findViewById(R.id.ea_upload_img_view);

        ch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileChooser();
            }
        });
        tp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TakePicture();
            }
        });
        submitAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAdvertisement();
            }
        });
    }

    private void FileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imguri = data.getData();
            img.setImageURI(imguri);
        }

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            img.setImageBitmap(imageBitmap);
            imguri = getImageUri(this, imageBitmap);
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        int img_quality = 50;
        inImage.compress(Bitmap.CompressFormat.JPEG, img_quality, bytes);

        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "my-picture", null);
        return Uri.parse(path);
    }

    private String getExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    private void TakePicture() {

        check_image_write_permission();

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void check_image_write_permission(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted - Request permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_STORAGE_PERMISSION);
        }
    }

    private void createAdvertisement() {
        submitAd.setEnabled(false);

        // Place views into variables
        TextView titleView = findViewById(R.id.ea_title);
        TextView descView = findViewById(R.id.ea_adDesc);
        RadioGroup qualityRadioGroup = findViewById(R.id.adQualityRadioGroup);
        RadioButton checkedRadio;
        TextView priceView = findViewById(R.id.ea_adPrice);

        int selectedRadioId = qualityRadioGroup.getCheckedRadioButtonId();

        // Place view titles into variables
        TextView titleTitle = findViewById(R.id.ea_adTitle);
        TextView descTitle = findViewById(R.id.ea_adDescriptionTitle);
        TextView imageTitle = findViewById(R.id.ea_adImageTitle);
        TextView qualityTitle = findViewById(R.id.ea_adQualityTitle);
        TextView priceTitle = findViewById(R.id.ea_adPriceTitle);

        if(findViewById(selectedRadioId)==null){
            qualityTitle.setTextColor(Color.RED);
            submitAd.setEnabled(true);
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

        int primary = getResources().getColor(R.color.colorPrimary);

        // Reset titles
        titleTitle.setTextColor(primary);
        descTitle.setTextColor(primary);
        imageTitle.setTextColor(primary);
        qualityTitle.setTextColor(primary);
        priceTitle.setTextColor(primary);

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
        if(imguri == null) {
            Log.d(TAG, "FileUploader : imguri empty");
            imageTitle.setTextColor(Color.RED);
            Toast.makeText(this, "Please Attach an Image", Toast.LENGTH_SHORT).show();
            error = true;
        }

        if(error) {
            submitAd.setEnabled(true);
            return;
        }

        // Upload image
        Log.d(TAG, "FileUploader");

        // If the image has already been uploaded just try to add the document to the database
        if(!imageUploaded) {
            imageName = System.currentTimeMillis() + "-" + user.getUid() + "." + getExtension(imguri);
            StorageReference ref = mStorageRef.child(imageName);

            ref.putFile(imguri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            addAdvertisementDocument();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Upload FAILED!", Toast.LENGTH_SHORT).show();
                            submitAd.setEnabled(true);
                        }
                    });
        } else {
            addAdvertisementDocument();
        }
    }

    private void addAdvertisementDocument() {
        // Attempt to create new advertisement
        Map<String, Object> advertisement = new HashMap<>();
        advertisement.put("title", title);
        advertisement.put("description", description);
        advertisement.put("image_src", imageName);
        advertisement.put("price", Double.parseDouble(price));
        advertisement.put("quality", quality);
        advertisement.put("distance", distance);
        advertisement.put("seller", user.getDisplayName());
        advertisement.put("user_id", user.getUid());

        db.collection("advertisements")
                .add(advertisement)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "Document added with ID : " + documentReference.getId());
                        Toast.makeText(CreateAdActivity.this, "Advertisement Added", Toast.LENGTH_SHORT).show();

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                // Actions to do after 10 seconds
                                finish();
                            }
                        }, 1500);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        Toast.makeText(CreateAdActivity.this, "Advertisement not Added!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        submitAd.setEnabled(true);
                    }
                });
    }
}
