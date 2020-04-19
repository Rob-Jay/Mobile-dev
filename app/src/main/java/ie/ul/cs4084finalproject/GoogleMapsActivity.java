package ie.ul.cs4084finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class GoogleMapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    final String TAG = "GoogleMapsActivity";

    // Google Maps
    private GoogleMap mMap;
    private LatLng ad_marker;

    FloatingActionButton btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);

        ad_marker = new LatLng(53.0, -8.0);

        if(getIntent().hasExtra("lat") && getIntent().hasExtra("lat")) {
            ad_marker = new LatLng(getIntent().getDoubleExtra("lat", 53.0), getIntent().getDoubleExtra("lat", -8.0));
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.gma_map);
        mapFragment.getMapAsync(this);

        btn = findViewById(R.id.gma_submit);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ad_marker.latitude == 53.0 && ad_marker.longitude == -8.0){
                    Toast.makeText(getApplicationContext(), "Please move the marker before continuing.", Toast.LENGTH_SHORT).show();
                } else {
                    // Pass Lat and Lng data back to create advertisement screen
                    Intent intent = new Intent();
                    intent.putExtra("lat", ad_marker.latitude);
                    intent.putExtra("lng", ad_marker.longitude);
                    setResult(55, intent);
                    finish();
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map){
        mMap = map;
        map.addMarker(new MarkerOptions().position(ad_marker).title("My Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ad_marker, 7));

        // Place marker on map where user tapped the map
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                ad_marker = point;
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(point));
            }
        });

        btn.setClickable(true);
    }
}
