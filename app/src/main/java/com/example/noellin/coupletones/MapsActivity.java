package com.example.noellin.coupletones;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ArrayList<Location> favoriteLocations;
    Marker prevMarker;
    Location prevLocation;
    static ArrayList<LatLng> arrayLatLng = new ArrayList<LatLng>();
    static int locationToggle = 0;
    // counter to check for addlocation toggle

    private static final int METERS_160 = 160;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Initialize our favorite locations ArrayList
        favoriteLocations = new ArrayList<>();

        Location loc1 = new Location("location 1");
        loc1.setLatitude(32.882320);
        loc1.setLongitude(-117.226790);
        favoriteLocations.add(loc1);

        Location loc2 = new Location("location 2");
        loc2.setLatitude(32.878080);
        loc2.setLongitude(-117.214250);
        favoriteLocations.add(loc2);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Marker m = mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("updated path"));
                if (prevMarker != null)
                {
                    prevMarker.remove();
                }
                prevMarker = m;
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15.0f));

                Location target;
                for (Location point: favoriteLocations)
                {
                    target = point;
                    if (location.distanceTo(target) < METERS_160)
                    {
                        Log.d("success", "near location");
                        if ((prevLocation == null) || (target.getLatitude() != prevLocation.getLatitude() && target.getLongitude() != prevLocation.getLongitude()))
                        {
                            handleReachedFavoriteLocation(target);
                            prevLocation = target;
                        }
                    }
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        LocationManager locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.GPS_PROVIDER;


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    100);
            Log.d("test1","ins");
            return;
        }else if(mMap != null) {
            Log.d("test2", "outs");
            mMap.setMyLocationEnabled(true);

        }

        locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);

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

        // add a partner in La Jolla and move the camera
        LatLng laJolla = new LatLng(32.882340, -117.233620);
        //mMap.addMarker(new MarkerOptions().position(laJolla).title("Marker in La Jolla"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom((laJolla), 15.0f));
        mMap.getUiSettings().setZoomControlsEnabled(true);



      //  public void addLocation() {
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                // TODO Auto-generated method stub
                System.out.println(arrayLatLng);
                //  mMap.clear();
                if (locationToggle %2 == 0) {
                    mMap.addMarker(new MarkerOptions().position(point));
                    arrayLatLng.add(point);

                }
            }
        });
        }

    public void addLocation (View view) {

        locationToggle++;
    }

    private void handleReachedFavoriteLocation(Location location)
    {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        CharSequence text = "Reached favorite location: " + location.getProvider();
        Toast t = Toast.makeText(context, text, duration);
        t.show();
    }

}



