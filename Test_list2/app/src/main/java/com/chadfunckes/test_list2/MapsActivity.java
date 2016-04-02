package com.chadfunckes.test_list2;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.chadfunckes.test_list2.GeoFence.GeofenceTransitionsIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    private final static String TAG = "Maps Activity";
    private String CALLED_ON;
    int GID, IID, AID;
    double LAT, LNG;
    static String GROUP_NAME, ITEM_NAME;
    private Context mContext;
    private EditText input;
    GoogleApiClient googleApiClient;

    boolean mShowMap;
    GoogleMap mMap;
    MapFragment mapFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        input = (EditText)findViewById(R.id.addyEditTxt);
        mContext = this;
        // @TODO change CALLED_ON to an int type here and in the list adapter to save memory space
        CALLED_ON = getIntent().getStringExtra("CALLED_ON"); // called on GROUP or ITEM
        GID = getIntent().getIntExtra("GID", -1);
        GROUP_NAME = getIntent().getStringExtra("GROUP_NAME");
        IID = getIntent().getIntExtra("IID", -1);
        ITEM_NAME = getIntent().getStringExtra("ITEM_NAME");

        mShowMap = isPlayseviceAvailable() && initMap();

        if (mShowMap){
            // camera update Cameraupdate update = CameraupdateFactory(LATLNG, ZOOM LVL)
            // mmap.moveCampera(update);
            // add markers

            if (CALLED_ON.equals("GROUP")) {
                Log.d(TAG, "maps on group");
                // set location data from object
            }
            else if (CALLED_ON.equals("ITEM")) {
                Log.d(TAG, "maps on item");
                // set location data from object
            }
        }

        buildGoogleApiClient();
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private boolean initMap() {
        if (mMap == null){
            mapFrag = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
            mMap = mapFrag.getMap();
        }
        return (mMap != null);
    }
    private boolean isPlayseviceAvailable() {
        int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
        switch (isAvailable){
            case ConnectionResult.SUCCESS:
                Log.i(TAG, "Connected to service");
                return true;
            case ConnectionResult.SERVICE_MISSING:
                Log.i(TAG, "Service Missing");
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                Log.i(TAG, "Update Required");
                break;
            case ConnectionResult.SERVICE_INVALID:
                Log.i(TAG, "Service Invalid");
                break;
            default:
                Log.i(TAG, "Service other value returned");
                break;
        }
        Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailable, (Activity)mContext, 1);
        dialog.show();
        return false;
    }
    // This function is called to put the resulted address on the man
    public void AddressToMap(View view) {
        String inputAddress;
        List<Address> address = null;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        inputAddress = input.getText().toString();

        clearKeyboard(view); // take keyboard off screen

        try{
            address = geocoder.getFromLocationName(inputAddress, 1);
            if (address.size() > 0){
                double LAT = address.get(0).getLatitude();
                double LNG = address.get(0).getLongitude();
                LatLng spot = new LatLng(LAT, LNG);

                mMap.addMarker(new MarkerOptions().position(spot));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(spot, 15));
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /* called when accept address is selected, it picks the address entered, gets its lat/long
       asks the user to set the fence leaving or entering, sets the fence on the system and
       saves the data to the local database.
     */
    public void AcceptAddress(View view){
        String inputAddress;
        List<Address> address = null;
        //double LAT = 0, LNG = 0;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        inputAddress = input.getText().toString();

        clearKeyboard(view);  // take keyboard off screen

        try {
            address = geocoder.getFromLocationName(inputAddress, 1);
            if (address.size() > 0){
                LAT = address.get(0).getLatitude();
                LNG = address.get(0).getLongitude();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (LAT != 0) {  // if a geo object was parsed (field not empty)

            AlertDialog.Builder dialog = new AlertDialog.Builder(MapsActivity.this);
            final EditText distance = new EditText(MapsActivity.this);

            dialog.setTitle("Arrive/Depart")
                  .setMessage("Enter Distance in feet from location and click if you should be alerted when arriving to / departing from location, default will be 500ft")
                  .setView(distance)
                    // cancel action
                  .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                          dialog.cancel();
                      }
                  })
                    // Depart Action
                  .setNeutralButton("Depart", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                          //do depart functions
                          //get distance input
                          //String diztaunce = distance.getText().toString();
                          //int dist = Integer.getInteger(diztaunce);
                          // set fence
                          setFence(100,true);
                          // set into db
                          //setInDatabase(dist, true);
                      }
                  })
                    // Arrive action
                  .setNegativeButton("Arrive", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                          // do arrive function
                          // get distance
                          String diztaunce = distance.getText().toString();
                          int dist = Integer.getInteger(diztaunce);
                          setFence(dist,false);
                          // set into db
                          setInDatabase(dist, false);
                      }
                  })
            .create().show();
        }

    }

    // close the keyboard by getting the input manager and hiding it.
    private void clearKeyboard(View view){
        if (view != null){
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    // function to set the geofence into memory
    private void setFence(int distance, boolean ad){
        // bool ad is true if selection was depart, false if selection was arrive
        // group, item, LAT and LNG exits already in the object
        String fenceID = "G"+GID+"I"+IID;

        // create geofence Object
        Geofence.Builder builder = new Geofence.Builder()
                .setRequestId(fenceID) // string to id this fence
                .setCircularRegion(LAT, LNG, distance)
                .setExpirationDuration(10000);
        if (ad) builder.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT);
        else builder.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER);

        // create geofence request from geofence object
        GeofencingRequest.Builder GRBuild = new GeofencingRequest.Builder();
        GRBuild.addGeofence(builder.build());

        // create new intent for the monitoring service and pending intent data
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // send all to location services
        LocationServices.GeofencingApi.addGeofences(googleApiClient, GRBuild.build(),pendingIntent);

    }


    private void setInDatabase(int distance, boolean ad){
        // bool ad is true if selection was depart, false if selection was arrive
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Connected to GoogleApiClient");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onResult(Status status) {

    }
}

