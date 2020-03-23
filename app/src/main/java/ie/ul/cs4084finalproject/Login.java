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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Timer;
import java.util.TimerTask;

public class Login extends AppCompatActivity {

    EditText xEmail,xPassword;
    Button xLoginButton, createButton;
    ProgressBar progressbar;
    FirebaseAuth fAuth;
    int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        xEmail = findViewById(R.id.Email_Input);
        xPassword = findViewById(R.id.passwordInput);
        xLoginButton = findViewById(R.id.LoginButton);
        fAuth = FirebaseAuth.getInstance();
        progressbar = findViewById(R.id.progressBar);
        createButton= findViewById(R.id.createButton);



        xLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = xEmail.getText().toString().trim();
                String password = xPassword.getText().toString().trim();


                if (TextUtils.isEmpty(email)) {
                    xEmail.setError("Email is Required.");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    xEmail.setError("password is Required.");
                    return;
                }

                if (password.length() < 6) {

                    xPassword.setError("Password must be greater than 6");
                    return;
                }

                //Authenticate

                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Login.this, "Login Successful",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }

                        else{
                            Toast.makeText(Login.this, "An error has occurred",Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Register.class));

            }
        });




    }

    public  void prog()
    {
        final Timer t = new Timer();
        TimerTask tt =new TimerTask() {
            @Override
            public void run() {
                counter++;
                progressbar.setProgress(counter);

                if(counter == 100)
                    t.cancel();
            }
        };
        t.schedule(tt,0,100);
    }
}
