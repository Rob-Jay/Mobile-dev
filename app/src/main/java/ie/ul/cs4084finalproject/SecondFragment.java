package ie.ul.cs4084finalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SecondFragment extends Fragment {
    private EditText NameEdit,DescriptionEdit;
    private FirebaseFirestore fStore;
    private String userID;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState

    ) {

        fStore= FirebaseFirestore.getInstance();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false);


    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NameEdit = view.findViewById(R.id.NameEdit);
        DescriptionEdit = view.findViewById(R.id.DescriptionEdit);

        view.findViewById(R.id.button_second).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
        String Name =NameEdit.getText().toString();
        String Description = DescriptionEdit.getText().toString();
        DocumentReference documentReference = fStore.collection("users").document(userID);
                Map<String, Object> user = new HashMap<>();
                user.put("Name",Name);
                user.put("Desc", DescriptionEdit);

                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
    }




}
