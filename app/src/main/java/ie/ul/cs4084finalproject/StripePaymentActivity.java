package ie.ul.cs4084finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
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

    int price;
    String advertisement_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stripe_payment);

        // TODO : Get price and advertisement_id from intent
        price = 100;
        advertisement_id = "123";

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

        // String url = "http://hive.csis.ul.ie/cs4116/17226864/android-stripe-handler.php";
        String url = "http://192.168.1.104/cs4084/android-stripe-handler.php";

        String body = "?token=" + tokenID + "&price=" + price + "&advertisement_id=" + advertisement_id;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url + body, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse: " + response.toString());
                try {
                    if(response.has("paid")){
                        if((boolean)response.get("paid") == true){
                            // TODO : Mark advertisement as purchased
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
            }
        });

        RequestSingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
}
