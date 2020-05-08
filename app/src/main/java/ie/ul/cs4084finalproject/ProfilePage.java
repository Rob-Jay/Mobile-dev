package ie.ul.cs4084finalproject;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ProfilePage extends AppCompatActivity {
    private static final String TAG = "ProfilePage";

    FirebaseFirestore db;
    FirebaseUser user;
    private ArrayList<Advertisement> ads = new ArrayList<>();
    private ArrayList<Advertisement> purchasedAds = new ArrayList<>();
    private MiniRecyclerViewAdapter ownedAdapter;
    private MiniRecyclerViewAdapter purchasedAdapter;

    private boolean loadedOwnedAds = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        initOwnedAdvertisements();
        initPurchasedAdvertisements();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(loadedOwnedAds) {
            initOwnedAdvertisements();
        }
    }

    private void initOwnedAdvertisements() {
        ads.clear();
        db.collection("advertisements")
                .whereEqualTo("user_id", user.getUid())
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
                                        Double.parseDouble(String.valueOf(document.get("price"))),
                                        document.get("quality").toString(),
                                        ((Long) document.get("distance")).intValue(),
                                        document.get("seller").toString()
                                ));
                            }

                            if(loadedOwnedAds){
                                ownedAdapter.notifyDataSetChanged();
                            } else {
                                loadedOwnedAds = true;
                                initOwnedRecyclerView();
                            }

                        } else {
                            Log.d(TAG, "Error getting documents : ", task.getException());
                        }
                    }
                });
    }

    private void initPurchasedAdvertisements() {
        purchasedAds.clear();
        db.collection("advertisements")
                .whereEqualTo("buyer_id", user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                purchasedAds.add(new Advertisement(
                                        document.getId(),
                                        document.get("title").toString(),
                                        document.get("image_src").toString(),
                                        Double.parseDouble(String.valueOf(document.get("price"))),
                                        document.get("quality").toString(),
                                        ((Long) document.get("distance")).intValue(),
                                        document.get("seller").toString()
                                ));
                            }

                            initPurchasedRecyclerView();
                        } else {
                            Log.d(TAG, "Error getting documents : ", task.getException());
                        }
                    }
                });
    }

    private void initOwnedRecyclerView() {
        Log.d(TAG, "initRecyclerView: init Owned Ads mini view");
        RecyclerView recyclerView = findViewById(R.id.pp_ownedAdsRecyclerView);
        ownedAdapter = new MiniRecyclerViewAdapter(this, ads, true);
        recyclerView.setAdapter(ownedAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initPurchasedRecyclerView() {
        Log.d(TAG, "initRecyclerView: init Owned Ads mini view");
        RecyclerView recyclerView = findViewById(R.id.pp_purchasedAdsRecyclerView);
        purchasedAdapter = new MiniRecyclerViewAdapter(this, purchasedAds, false);
        recyclerView.setAdapter(purchasedAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

}
