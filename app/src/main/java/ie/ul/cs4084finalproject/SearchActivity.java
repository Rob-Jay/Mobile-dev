package ie.ul.cs4084finalproject;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class SearchActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference advertismentsRef = db.collection("advertisements");
    private SearchResultAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        setUpReyclerView();
    }

    private void  setUpReyclerView(){
        Query query = advertismentsRef.orderBy("title",Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<SearchResult> options = new FirestoreRecyclerOptions.Builder<SearchResult>()
                .setQuery(query, SearchResult.class)
                .build();

         adapter = new SearchResultAdapter(options);
        RecyclerView recyclerView = findViewById(R.id.homeRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }
    protected void onStart(){
        super.onStart();
        adapter.startListening();
    }

    protected void onStop() {
        super.onStop();
        adapter.startListening();
    }
}
