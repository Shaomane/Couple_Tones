package com.example.noellin.coupletones;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.InputType;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
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
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ArrayList<Location> favoriteLocations;
    Marker prevMarker;
    Location prevLocation;
    static ArrayList<LatLng> arrayLatLng = new ArrayList<LatLng>();
    static int locationToggle = 0;              // counter to check for addlocation toggle
    public static String placeName;

    /* these lines below save user favorite locations between app sessions */
    static final int PREFERENCE_MODE_PRIVATE = 0;
    public static final String SAVED_LOCATIONS = "Saved_locations_file";


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
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;

        // add a partner in La Jolla and move the camera
        //LatLng laJolla = new LatLng(32.882340, -117.233620);
        LatLng laJolla = new LatLng(32.882340, -117.233620);
        //mMap.addMarker(new MarkerOptions().position(laJolla).title("Marker in La Jolla"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom((laJolla), 15.0f));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // opens the sharedpreferences
        SharedPreferences savedLocations = getSharedPreferences(SAVED_LOCATIONS, PREFERENCE_MODE_PRIVATE);
       // SharedPreferences.Editor savedLocationsEditor = savedLocations.edit();

        // adds a marker for each previously set marker
        Map<String, ?> previousLocations = savedLocations.getAll();
        for (Map.Entry<String, ?> entry : previousLocations.entrySet())
        {
            String locName = entry.getKey();
            String locPos = entry.getValue().toString();
            System.out.println("LatLng from getValue: " + locPos);
            String strLatLng[] = (locPos.split (" "));
            // creates a new LatLng with string[] by parsing them into doubles
            System.out.println (strLatLng[0]);
            System.out.println (strLatLng[1]);
               LatLng thisLocationLatLng = new LatLng(Double.parseDouble(strLatLng[0]),
                                             Double.parseDouble(strLatLng[1]) );
    /*        System.out.println ("Name is: " + locName);
            System.out.println ("Lat after parse double is: " + Double.parseDouble(strLatLng[0]));
            System.out.println ("Long after parse double is: " + Double.parseDouble(strLatLng[1])); */

            mMap.addMarker(new MarkerOptions().position(thisLocationLatLng).title(locName));
        }


        //  public void addLocation() {
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {

            @Override
            public void onMapClick(LatLng point)
            {
                if (locationToggle % 2 == 1)
                {
                    showDialog(point);
       /*             Marker instMarker =
                            mMap.addMarker (new MarkerOptions()
                    .title(placeName).position(point));    */
                                    //.showInfoWindow();
                   // instMarker.setTitle(placeName);
                   // instMarker.setPosition(point);
/*
                    Double currLat = point.latitude;
                    Double currLong = point.longitude;
                    // adds a location as a concatenation of latitude and longitude
                    String locationData = (currLat.toString()+ " " + currLong.toString());

                    savedLocationsEditor.putString(placeName, locationData);
                    savedLocationsEditor.apply();
                    System.out.println ("Place name is " + placeName);
                    System.out.println ("Location is " + locationData);  */
                }
            }

        });
    }

    // shows a dialog box when called to accept user input
    public void showDialog (final LatLng currPoint)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MapsActivity.this);
        dialogBuilder.setTitle("Name this location:");

        // To grab input from user
        final EditText input = new EditText(MapsActivity.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        dialogBuilder.setView(input);

        // Create the buttons
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {


            @Override
            public void onClick(DialogInterface dialog, int which) {

               // SharedPreferences savedLocations = PreferenceManager.getDefaultSharedPreferences(0);
               // final SharedPreferences.Editor savedLocationsEditor = savedLocations.edit();
                SharedPreferences savedLocations = getSharedPreferences(SAVED_LOCATIONS, 0);
                SharedPreferences.Editor savedLocationsEditor = savedLocations.edit();

                placeName = input.getText().toString();

                mMap.addMarker (new MarkerOptions()
                                .title(placeName).position(currPoint)).showInfoWindow();


                Double currLat = currPoint.latitude;
                Double currLong = currPoint.longitude;
                // adds a location as a concatenation of latitude and longitude
                String locationData = (currLat.toString()+ " " + currLong.toString());


                savedLocationsEditor.putString(placeName, locationData);
                savedLocationsEditor.apply();

                // err messages for debugging purposes
                System.err.println("Hello from line 278");
                System.err.println(savedLocations.getAll());
                System.err.println ("Place name is " + placeName);
                System.err.println ("Location is " + locationData);
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        dialogBuilder.show();

    }

    // toggles the add location button
    public void addLocation (View view)
    {
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



