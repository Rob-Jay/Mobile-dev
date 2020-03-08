package ie.ul.cs4084finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchActivity";

    private ArrayList<Advertisement> ads = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initAdvertisements();
    }

    private void initAdvertisements() {
        // TODO : Create example advertisemnts for testing
        ads.add(new Advertisement("LawnMover", "image src", 349.99, "Brand New", 12, "Adam"));
        ads.add(new Advertisement("LawnMover 2", "image src", 389.99, "Used", 54, "Ricky"));
        ads.add(new Advertisement("LawnMover 3", "image src", 49.99, "Used", 623, "Morty"));

        initRecyclerView();
    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init SearchRecyclerView");
        RecyclerView recyclerView = findViewById(R.id.searchRecyclerView);
        SearchRecyclerViewAdapter adapter = new SearchRecyclerViewAdapter(ads);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
