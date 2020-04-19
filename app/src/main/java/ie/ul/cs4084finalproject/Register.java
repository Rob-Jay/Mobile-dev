package ie.ul.cs4084finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class Register extends AppCompatActivity {

    EditText mFullName, mEmail, mPassword;
    Button mRegisterBTN, mLoginBtn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;

    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        mFullName = findViewById(R.id.FullName_Input);
        mEmail = findViewById(R.id.Email_Input);
        mPassword = findViewById(R.id.password_input);
        mRegisterBTN = findViewById(R.id.Register_BTN);
        mLoginBtn = findViewById(R.id.LoginButton);

        fAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);

        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }


        mRegisterBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                final String fullname = mFullName.getText().toString().trim();


                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is Required.");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    mEmail.setError("Password is Required.");
                    return;
                }

                if (TextUtils.isEmpty(fullname)){
                    mFullName.setError("Full Name is Required.");
                    return;
                }

                if (password.length() < 6) {

                    mPassword.setError("Password must have more than 6 characters");
                    return;
                }

                Toast.makeText(Register.this, "Creating Profile ...",Toast.LENGTH_SHORT).show();


                progressBar.setVisibility(View.VISIBLE);

                //Registering the user

                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(fullname)
                                    .build();

                            FirebaseUser user = fAuth.getCurrentUser();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(Register.this, "User Created",Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(Register.this, "Fullname not updated. Continuing.",Toast.LENGTH_SHORT).show();
                                            }

                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        }
                                    });
                        } else {
                            Toast.makeText(Register.this, "User already exists", Toast.LENGTH_SHORT).show();
                        }
                    }


                });
            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });


    }


}