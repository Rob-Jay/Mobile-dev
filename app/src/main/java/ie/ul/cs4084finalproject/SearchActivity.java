package ie.ul.cs4084finalproject;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


//This page has been tested
public class SearchActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference advertisementsRef = db.collection("advertisements");
    private SearchResultAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("Created on Create for search Activity");

        //Here is the edit text for the search
        EditText searchText = findViewById(R.id.search_text);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setUpRecyclerView();
    }

    //filter method for search
    private void filter(String text){
    }



    private void  setUpRecyclerView(){
        System.out.println("Created setUp RecyclerView");
        Query query = advertisementsRef.orderBy("price",Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<SearchResult> options = new FirestoreRecyclerOptions.Builder<SearchResult>()
                .setQuery(query, SearchResult.class)
                .build();

         adapter = new SearchResultAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }
    protected void onStart(){
        System.out.println("Created onStart");
        super.onStart();
        adapter.startListening();
    }

    protected void onStop() {

        System.out.println("Created onStop");
        super.onStop();
        adapter.startListening();
    }
}
