package ie.ul.cs4084finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class StripePaymentActivity extends AppCompatActivity {

    final String TAG = "StripePaymentActivity";

    Stripe stripe;
    private FirebaseFirestore db;

    int price;
    String advertisement_id;

    private Intent resultIntent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stripe_payment);

        db = FirebaseFirestore.getInstance();

        if(getIntent().hasExtra("advertisement_id") && getIntent().hasExtra("price")){
            price = (int)(getIntent().getDoubleExtra("price", 0.0) * 100);
            advertisement_id = getIntent().getStringExtra("advertisement_id");

            if(price == 0.0){
                Log.d(TAG, "onCreate: Price is 0.0, exiting");
                Toast.makeText(StripePaymentActivity.this, "An error occurred, please try again", Toast.LENGTH_SHORT).show();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        finish();
                    }
                }, 1500);
            }
        } else {
            Log.e(TAG, "onCreate: Intent has no passed data", new Exception());
            Toast.makeText(StripePaymentActivity.this, "An error occurred, please try again", Toast.LENGTH_SHORT).show();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    finish();
                }
            }, 1500);
        }

        PaymentConfiguration.init(
                getApplicationContext(),
                "pk_test_y91O9g6bejcj2SaYn6jpk6Db00Wlj6ICye"
        );

        // Hook up the pay button to the card widget and Stripe instance
        Button payButton = findViewById(R.id.payButton);
        WeakReference<StripePaymentActivity> weakActivity = new WeakReference<>(this);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the card details from the card widget
                CardInputWidget cardInputWidget = findViewById(R.id.cardInputWidget);
                Card card = cardInputWidget.getCard();
                if (card != null) {
                    // Create a Stripe token from the card details
                    stripe = new Stripe(getApplicationContext(), PaymentConfiguration.getInstance(getApplicationContext()).getPublishableKey());
                    stripe.createCardToken(card, new ApiResultCallback<Token>() {
                        @Override
                        public void onSuccess(@NonNull Token result) {
                            String tokenID = result.getId();
                            // Send the token identifier to the server...
                            sendRequest(tokenID);
                        }

                        @Override
                        public void onError(@NonNull Exception e) {
                            // Handle error
                        }
                    });
                }
            }
        });
    }

    private void sendRequest(String tokenID) {

        String url = "http://hive.csis.ul.ie/cs4116/17226864/android-stripe-handler.php";
        // String url = "http://192.168.1.104/cs4084/android-stripe-handler.php";

        String body = "?token=" + tokenID + "&price=" + price;

        Log.d(TAG, "sendRequest: URL : " + url + body);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url + body, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse: " + response.toString());
                try {
                    if(response.has("paid")){
                        if((boolean)response.get("paid")){
                            // Update Advertisement to purchased
                            db.collection("advertisements")
                                    .document(advertisement_id)
                                    .update("status", "sold")
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getApplicationContext(), "Item Purchased", Toast.LENGTH_SHORT).show();

                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                public void run() {
                                                    finish();
                                                }
                                            }, 1500);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error purchasing advertisement", e);
                                            Toast.makeText(getApplicationContext(), "Purchase was not sucessful", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            resultIntent.putExtra("result", true);
                                            setResult(7, resultIntent);
                                            finish();
                                        }
                                    });
                        } else {
                            resultIntent.putExtra("result", false);
                            setResult(7, resultIntent);
                            finish();
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "onResponse: Error caught: ", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: error : " + error);
                resultIntent.putExtra("result", false);
                setResult(7, resultIntent);
                finish();
            }
        });

        RequestSingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
}
