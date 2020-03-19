package ie.ul.cs4084finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    
    private static final String TAG = "MainActivity";

    FirebaseFirestore db;

    private ArrayList<Advertisement> ads = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        Button createAd = findViewById(R.id.createAdButton);
        Button search = findViewById(R.id.searchButton);

        Button addDoc = findViewById(R.id.addDoc);

        createAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateAdActivity.class);
                startActivity(intent);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(i);
            }
        });

        addDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> advertisement = new HashMap<>();
                advertisement.put("title", "Lawnmower");
                advertisement.put("image_src", "link-to-img");
                advertisement.put("price", 359.99);
                advertisement.put("quality", "Brand New");
                advertisement.put("distance", 55);
                advertisement.put("seller", "Adam");

                db.collection("advertisements")
                        .add(advertisement)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "Document added with ID : " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });
            }
        });

        initAdvertisements();
    }

    private void initAdvertisements() {
        // TODO : Load advertisements from database
        db.collection("advertisements")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.get("seller"));
                                ads.add(new Advertisement(
                                        document.get("title").toString(),
                                        document.get("image_src").toString(),
                                        (Double)document.get("price"),
                                        document.get("quality").toString(),
                                        ((Long)document.get("distance")).intValue(),
                                        document.get("seller").toString()
                                ));
                            }

                            initRecyclerView();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init Home Screen RecyclerView");
        RecyclerView recyclerView = findViewById(R.id.homeRecyclerView);
        SearchRecyclerViewAdapter adapter = new SearchRecyclerViewAdapter(ads);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
