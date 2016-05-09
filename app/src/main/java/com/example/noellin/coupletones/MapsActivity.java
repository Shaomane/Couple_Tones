package com.example.noellin.coupletones;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
//import android.app.Notification;
import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
//<<<<<<< HEAD
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
//=======
import android.util.Log;
import android.widget.RadioGroup;
//>>>>>>> af92d14e35792164d3ea4db8de23b7f35300e5d6
import android.widget.Toast;
import android.widget.EditText;

import com.firebase.client.Firebase;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private TextView mTxtAccountName;
    private static final int RC_SELECT_ACCOUNT = 200;


    private GoogleMap mMap;
    ArrayList<Location> favoriteLocations;
    Marker prevMarker;
    Location prevLocation;
    static ArrayList<LatLng> arrayLatLng = new ArrayList<LatLng>();
   // static int addLocationToggle = 0;              // counter to check for addlocation toggle
   // static int removeLocationToggle = 0;            // counter to check for remove location toggle
    public static String placeName;

    /* these lines below save user favorite locations between app sessions */
    static final int PREFERENCE_MODE_PRIVATE = 0;
    public static final String SAVED_LOCATIONS = "Saved_locations_file";

    private static final int METERS_160 = 160;

    private String rel_id = null;
    private String senderEmail;
    private String senderName;

    boolean isSpecialMessageSent = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            rel_id = extras.getString("rel_id");
            senderEmail = extras.getString("senderEmail");
            senderName = extras.getString("senderName");
        }

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Initialize our favorite locations ArrayList
        favoriteLocations = new ArrayList<>();

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Marker m = mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),
                        location.getLongitude())).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title("Current location"));

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
                        //Log.d("success", "near location");
                        if ((prevLocation == null) || (target.getLatitude() != prevLocation.getLatitude() && target.getLongitude() != prevLocation.getLongitude()))
                        {
                            handleReachedFavoriteLocation(target);
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

        // creates a radio group and links it to this activity
        final RadioGroup rg = (RadioGroup) findViewById(R.id.radioLocationAction);
        // fills the map with markers
        populateMap();

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(LatLng point)
            {
                //if (addLocationToggle % 2 == 1)
                if (rg.getCheckedRadioButtonId() == R.id.addLocation)
                {
                    showInputDialog(point);
                }
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {
            @Override
            public boolean onMarkerClick(Marker clickedMarker) {

                //if (removeLocationToggle % 2 == 1)
                if (rg.getCheckedRadioButtonId() == R.id.removeLocation)
                {
                    System.err.println(clickedMarker.getTitle());
                    mMap.clear();
                    SharedPreferences savedLocations = getSharedPreferences(SAVED_LOCATIONS, PREFERENCE_MODE_PRIVATE);
                    SharedPreferences.Editor savedLocationsEditor = savedLocations.edit();

                    System.err.println(savedLocations.getAll());

                    savedLocationsEditor.remove(clickedMarker.getTitle().toString());
                    savedLocationsEditor.apply();
                    System.err.println(savedLocations.getAll());
                    populateMap();

                    for (int i = 0; i < favoriteLocations.size(); i++)
                    {
                        Location currLoc = favoriteLocations.get(i);
                        if ((currLoc.getProvider().equals(clickedMarker.getTitle())) &&
                            (currLoc.getLatitude() == clickedMarker.getPosition().latitude) &&
                                (currLoc.getLongitude() == clickedMarker.getPosition().longitude))
                        {
                            System.err.println("REMOVE SUCCESSFUL");
                            favoriteLocations.remove(i);
                        }
                    }
                }

                clickedMarker.showInfoWindow();

                return true;
            }

        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener()
        {
            Marker startMarker;

            @Override
            public void onMarkerDragStart (Marker startMarker) {
                System.err.println ("Marker drag now start");
                this.startMarker = startMarker;
            }
            @Override
            public void onMarkerDrag (Marker duringMarker) {

            }
            @Override
            public void onMarkerDragEnd (Marker endMarker) {
                System.err.println ("Marker drag now ends");

                SharedPreferences savedLocations = getSharedPreferences(SAVED_LOCATIONS, PREFERENCE_MODE_PRIVATE);
                SharedPreferences.Editor savedLocationsEditor = savedLocations.edit();
                System.err.println(savedLocations.getAll());

                savedLocationsEditor.remove(endMarker.getTitle());

                Double currLat = endMarker.getPosition().latitude;
                Double currLong = endMarker.getPosition().longitude;
                // adds a location as a concatenation of latitude and longitude
                String locationData = (currLat.toString()+ " " + currLong.toString());

                savedLocationsEditor.putString(endMarker.getTitle(), locationData);

                savedLocationsEditor.apply();
                System.err.println(savedLocations.getAll());

                for (int i = 0; i < favoriteLocations.size(); i++)
                {
                    if ((favoriteLocations.get(i).getProvider().equals(startMarker.getTitle())))
                    {
                        System.err.println("MOVE SUCCESSFUL");
                        System.err.println("favorite location lat: " + favoriteLocations.get(i).getLatitude());
                        System.err.println("startMarker lat: " + startMarker.getPosition().latitude);
                        System.err.println("new lat: " + currLat);
                        favoriteLocations.get(i).setLatitude(currLat);
                        favoriteLocations.get(i).setLongitude(currLong);
                    }
                }
            }
        });

    }

    // shows a dialog box when called to accept user input
    public void showInputDialog (final LatLng currPoint)
    {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MapsActivity.this);
        alertBuilder.setTitle("Name this location:");

        // To grab input from user
        final EditText input = new EditText(MapsActivity.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        alertBuilder.setView(input);

        // Create the buttons
        alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                SharedPreferences savedLocations = getSharedPreferences(SAVED_LOCATIONS, PREFERENCE_MODE_PRIVATE);
                SharedPreferences.Editor savedLocationsEditor = savedLocations.edit();

                placeName = input.getText().toString();

                mMap.addMarker (new MarkerOptions()
                                .title(placeName).position(currPoint).draggable(true)).showInfoWindow();

                Double currLat = currPoint.latitude;
                Double currLong = currPoint.longitude;
                // adds a location as a concatenation of latitude and longitude
                String locationData = (currLat.toString()+ " " + currLong.toString());


                savedLocationsEditor.putString(placeName, locationData);
                savedLocationsEditor.apply();

                //Add location to favoriteLocations
                Location currLoc = new Location(placeName);
                currLoc.setLatitude(currLat);
                currLoc.setLongitude(currLong);
                favoriteLocations.add(currLoc);

                // err messages for debugging purposes
                System.err.println("Hello from line 278");
                System.err.println(savedLocations.getAll());
                System.err.println ("Place name is " + placeName);
                System.err.println ("Location is " + locationData);
            }
        });
        alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertBuilder.show();

    }
/*
    // toggles the add location button
    public void addLocation (View view)
    {
        final Button addLocationButton = (Button) findViewById(R.id.addLocationButton);
        addLocationToggle++;
        if ((addLocationToggle % 2 == 1))
        {
            addLocationButton.setText("Adding Location!");
            addLocationButton.setTextColor(Color.BLUE);
        }
        else {
            addLocationButton.setText("Not Adding Location.");
            addLocationButton.setTextColor(Color.BLACK);
          //  removeLocation();
        }
    }

    // to remove location
    public void removeLocation (View view)
    {
        final Button removeLocationButton = (Button) findViewById(R.id.removeLocationButton);
        removeLocationToggle++;
        if ((removeLocationToggle % 2 == 1))
        {
            removeLocationButton.setText("Removing Location!");
            removeLocationButton.setTextColor(Color.RED);
        }
        else {
            removeLocationButton.setText("Not Removing Location.");
            removeLocationButton.setTextColor(Color.BLACK);

            //  removeLocation();
        }

    }  */
    public void populateMap ()
    {
        // opens the sharedpreferences
        SharedPreferences savedLocations = getSharedPreferences(SAVED_LOCATIONS, PREFERENCE_MODE_PRIVATE);
        // adds a marker for each previously set marker
        Map<String, ?> previousLocations = savedLocations.getAll();
        for (Map.Entry<String, ?> entry : previousLocations.entrySet())
        {
            String locName = entry.getKey();
            String locPos = entry.getValue().toString();
            String strLatLng[] = (locPos.split (" "));
            // creates a new LatLng with string[] by parsing them into doubles
            LatLng thisLocationLatLng = new LatLng(Double.parseDouble(strLatLng[0]),
                    Double.parseDouble(strLatLng[1]) );

            mMap.addMarker(new MarkerOptions().position(thisLocationLatLng).title(locName).draggable(true)).showInfoWindow();

            //Add each location to favoriteLocations
            Location currLoc = new Location(locName);
            currLoc.setLatitude(thisLocationLatLng.latitude);
            currLoc.setLongitude(thisLocationLatLng.longitude);
            favoriteLocations.add(currLoc);
        }
    }
    private void handleReachedFavoriteLocation(Location location)
    {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        CharSequence text = "Reached favorite location: " + location.getProvider();
        Toast t = Toast.makeText(context, text, duration);
        if (isMyServiceRunning(CoolDownService.class))
        {
            Log.d("running", "service is running in the background");
        }
        else
        {
            // Start the background service
            Intent intent = new Intent(MapsActivity.this, CoolDownService.class);
            startService(intent);
            Log.d("success", "near location " + location.getProvider());
            t.show();
            prevLocation = location;
            sendMessage(location);
            //sendMessage();
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void sendMessage(Location location) {
        if (location == null){
            Log.e("sendMessage", "null location was received in sendMessage");
            return;
        }else if (rel_id == null || senderEmail == null){
            Log.e("sendMessage", "rel_id is null. Implied that we are not in a relationship");
            return;
        }

        String msg = senderName + " visited " + location.getProvider();
        //long time = location.getTime();
        String coords = ""+(int)(location.getLatitude()+location.getLongitude());

        final String relationshipID = rel_id;

        //Get a Firebase reference to our relationship
        Firebase ref = new Firebase("https://dazzling-inferno-7112.firebaseio.com/relationships/"+rel_id);

        Map<String, Object> newNotification = new HashMap<String, Object>();
        newNotification.put(coords, "");

        Map<String, Object> sender = new HashMap<String, Object>();
        sender.put("sender", senderEmail);
        Map<String, Object> message = new HashMap<String, Object>();
        sender.put("message", msg);
        //Map<String, Object> occuranceTime = new HashMap<String, Object>();
        //occuranceTime.put("occuranceTime", time);

        ref.child("notifications").child(coords).updateChildren(sender);
        ref.child("notifications").child(coords).updateChildren(message);
        //ref.child("notifications").child(coords).updateChildren(occuranceTime);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SELECT_ACCOUNT) {
            if (resultCode == Activity.RESULT_OK) {
                String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                mTxtAccountName.setText(accountName);
            }
            else {
                Log.v("grokkingandroid", "couldn't select account: " + resultCode);
            }
        }
    }



}