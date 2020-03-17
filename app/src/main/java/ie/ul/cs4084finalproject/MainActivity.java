package ie.ul.cs4084finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    
    private static final String TAG = "MainActivity";

    private ArrayList<Advertisement> ads = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button createAd = findViewById(R.id.createAdButton);
        Button search = findViewById(R.id.searchButton);

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

        initAdvertisements();
    }

    private void initAdvertisements() {
        // TODO : Load advertisements from database
        ads.add(new Advertisement("LawnMover", "image src", 349.99, "Brand New", 12, "Adam"));
        ads.add(new Advertisement("LawnMover 2", "image src", 389.99, "Used", 54, "Ricky"));
        ads.add(new Advertisement("LawnMover 3", "image src", 49.99, "Used", 623, "Morty"));
    
        initRecyclerView();
    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init Home Screen RecyclerView");
        RecyclerView recyclerView = findViewById(R.id.homeRecyclerView);
        SearchRecyclerViewAdapter adapter = new SearchRecyclerViewAdapter(ads);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
