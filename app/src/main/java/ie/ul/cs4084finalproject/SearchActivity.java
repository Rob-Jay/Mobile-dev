package ie.ul.cs4084finalproject;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;


//This page has been tested
public class SearchActivity extends AppCompatActivity {

    final String TAG = "SearchActivity";

    private FirebaseFirestore db;
    private ArrayList<Advertisement> ads = new ArrayList<>();
    private SearchRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("Created on Create for search Activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        db = FirebaseFirestore.getInstance();

        Button searchBtn = findViewById(R.id.sa_search_button);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Loading results ...", Toast.LENGTH_SHORT).show();

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

                                    filter();

                                    adapter.emptyHolders();
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(getApplicationContext(), "No results found.", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "Error getting documents : ", task.getException());
                                }
                            }
                        });
            }
        });

        initRecyclerView();
    }

    //filter method for search
    private void filter(){
        EditText searchText = findViewById(R.id.sa_search_text);
        EditText minText = findViewById(R.id.sa_min_price_field);
        EditText maxText = findViewById(R.id.sa_max_price_field);

        String search = searchText.getText().toString();
        double min = 0.0;
        double max = 999999999.99;

        if(minText.getText().toString().length() > 0){
            min = Double.parseDouble(minText.getText().toString());
        }
        if(maxText.getText().toString().length() > 0){
            max = Double.parseDouble(maxText.getText().toString());
        }

        // Filter out results with price outside range
        for(int i = 0; i < ads.size(); i++) {
            Advertisement ad = ads.get(i);

            if(ad.getPrice() > max && ad.getPrice() < min) {
                // Advertisement can be removed, price not in range
                ads.remove(i);
                i--;
            }
        }

        if(search.length() > 0){
           // Filter by search term
            for(int i = 0; i < ads.size(); i++) {
                Advertisement ad = ads.get(i);

                if( !ad.getTitle().toLowerCase().contains(search.toLowerCase()) ){
                    ads.remove(i);
                    i--;
                }
            }
        }
    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init Home Screen RecyclerView");
        recyclerView = findViewById(R.id.sa_search_recycler_view);
        adapter = new SearchRecyclerViewAdapter(this, ads);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
