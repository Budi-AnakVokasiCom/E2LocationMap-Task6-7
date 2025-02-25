package com.example.e2locationmap;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.e2locationmap.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.PointOfInterest;

import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(-34, 151)));

        // Add a marker in Sydney and move the camera

        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Cakung"));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        setMapLongClick(mMap);
        setPointClick(mMap);
        enableMyLoc();

        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json
                    )
            );
            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }

        }catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_options, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
           switch (item.getItemId()) {
               case R.id.normal_map:
                   mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                   return true;
               case R.id.hybrid_map:
                   mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                   return true;
               case R.id.satellite_map:
                   mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                   return true;
               case R.id.terrain_map:
                   mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                   return true;
               default:
                   return super.onOptionsItemSelected(item);
           }
    }

    private void setMapLongClick(final  GoogleMap map) {
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener()
        {
            @Override
            public void onMapLongClick(@NonNull LatLng latLng) {
                String text = String.format(Locale.getDefault(),
                        "Lat: %1$.5f, Long: %2$.5f",
                        latLng.latitude,
                        latLng.longitude);
                map.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Dropped pin")
                        .snippet(text));
            }
        });
    }


    private void setPointClick(final GoogleMap map) {
        map.setOnPointClickListener(new GoogleMap.OnPointClickListener()
        {
            @Override
            public void onPointClick(@NonNull PointOfInterest pointOfInterest) {
                Marker pointMarker = mMap.addMarker(new MarkerOptions()
                        .position(pointOfInterest.latLng)
                        .title(pointOfInterest.name));
                pointMarker.showInfoWindow();
            }

        });
    }
    private void enableMyLoc() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull
    String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0
                        && grantResults[0]
                        == PackageManager.PERMISSION_GRANTED) {
                    enableMyLoc();
                    break;
                }
        }}

}
