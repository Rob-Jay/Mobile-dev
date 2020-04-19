package ie.ul.cs4084finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    FirebaseFirestore db;

    private ArrayList<Advertisement> ads = new ArrayList<>();
    private boolean adsInit = false;
    private SearchRecyclerViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        Button createAd = findViewById(R.id.ma_createAdButton);
        Button searchButton = findViewById(R.id.ma_search_button);

        createAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateAdActivity.class);
                startActivityForResult(intent, 34);
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SearchActivity.class);
                System.out.println("Created Listener");
                startActivity(i);
            }
        });

        initAdvertisements();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 34 && resultCode == 101) {
            initAdvertisements();
        }
    }

    private void initAdvertisements() {
        ads.clear();
        db.collection("advertisements")
                .whereEqualTo("status", "available")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                ads.add(new Advertisement(
                                        document.getId(),
                                        document.get("title").toString(),
                                        document.get("image_src").toString(),
                                        (Double) document.get("price"),
                                        document.get("quality").toString(),
                                        ((Long) document.get("distance")).intValue(),
                                        document.get("seller").toString()
                                ));
                            }

                            if(!adsInit){
                                adsInit = true;
                                initRecyclerView();
                            } else {
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            Log.d(TAG, "Error getting documents : ", task.getException());
                        }
                    }
                });
    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init Home Screen RecyclerView");
        RecyclerView recyclerView = findViewById(R.id.ma_homeRecyclerView);
        adapter = new SearchRecyclerViewAdapter(this, ads);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    // Log user out
    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }
}



