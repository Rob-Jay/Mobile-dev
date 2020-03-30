package ie.ul.cs4084finalproject;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ProfilePage extends AppCompatActivity {
private FirebaseDatabase db;
public FirebaseAuth mAuth;
private FirebaseAuth.AuthStateListener mAuthListener;
private DatabaseReference myref;
private static final String TAG = "ProfilePage";
private String userID;
private ListView mListView;
private Button button_first;
FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_first);
        button_first = findViewById(R.id.button_first);

        mListView = findViewById(R.id.listview);

        mAuth=FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        myref= db.getReference();
        FirebaseUser user= mAuth.getCurrentUser();
        userID =user.getUid();

        mAuthListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!=null) {
                    Log.d(TAG, "onAuthStateChanged:sign_in:" + user.getUid());
                    toastMessage("Successfully signed in with: " + user.getEmail());
                }else{
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    toastMessage("Successfully signed out");
                }
            }
        };

        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }



    private void showData(DataSnapshot dataSnapshot) {
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            UserInformation uInfo =new UserInformation();
            uInfo.setName(ds.child(userID).getValue(UserInformation.class).getName());
            uInfo.setImage_src(ds.child(userID).getValue(UserInformation.class).getImage_src());
            uInfo.setTitle(ds.child(userID).getValue(UserInformation.class).getTitle());
           Log.d(TAG,"showData: name: "+ uInfo.getName());
            ArrayList<String> array = new ArrayList<>();
            array.add(uInfo.getName());
            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        }
    }


    public ProfilePage() {

        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onStop(){
        super.onStop();
        if(mAuthListener !=null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

}
