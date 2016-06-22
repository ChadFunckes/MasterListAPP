package com.chadfunckes.test_list2;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.chadfunckes.test_list2.GeoFence.GeofenceTransitionsIntentService;
import com.chadfunckes.test_list2.Models.Fence;
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
    private final int DISTANCE = 100;
    //private String CALLED_ON;
    private int GID;
    private int IID;
    //double LAT, LNG;
    private String fenceID;
    private Fence fence;
    private Context mContext;
    private EditText input;
    private GoogleApiClient googleApiClient;
    private boolean mShowMap;
    private GoogleMap mMap;
    private MapFragment mapFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        input = (EditText)findViewById(R.id.addyEditTxt);
        mContext = this;
        //CALLED_ON = getIntent().getStringExtra("CALLED_ON"); // called on GROUP or ITEM
        GID = getIntent().getIntExtra("GID", -1);
        IID = getIntent().getIntExtra("IID", -1);
        if (IID == -1) IID = 0;
        fenceID = "G"+GID+"I"+IID; // set the fence ID (required for adding and deleting a fence
        Log.d(TAG, "fence ID is: " + fenceID);
        mShowMap = isPlayseviceAvailable() && initMap();

        if (mShowMap){
            fence = MainActivity.database.getFence(GID, IID);
            // if fence is null there is no fence....if not null use fence and change button to remove
            if (fence != null){
                input.setText(fence.ADD);
                Button useRem = (Button) findViewById(R.id.acceptBtn);
                useRem.setText("Remove Location");
                useRem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeAddress(fence);
                    }
                });
                LatLng spot = new LatLng(fence.LAT, fence.LNG);
                mMap.addMarker(new MarkerOptions().position(spot));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(spot, 15));
            }else {
                fence = new Fence();
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

    private synchronized void buildGoogleApiClient() {
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

    // This function is called to put the resulted address on the map
    public void AddressToMap(View view) {
        String inputAddress;
        List<Address> address;
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
        final String inputAddress;
        List<Address> address;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        inputAddress = input.getText().toString();
        clearKeyboard(view);  // take keyboard off screen
        fence.FID = fenceID; fence.ADD = inputAddress;

        try {
            address = geocoder.getFromLocationName(inputAddress, 1);
            if (address.size() > 0){
                fence.LAT = address.get(0).getLatitude();
                fence.LNG = address.get(0).getLongitude();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (fence.LAT != 0) {  // if a geo object was parsed (field not empty)
            AlertDialog.Builder dialog = new AlertDialog.Builder(MapsActivity.this);
            dialog.setTitle("Arrive/Depart")
                  .setMessage("Set alert for when you are leaving or departing location?")
                  .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                          dialog.cancel();
                      }
                  })
                  .setNeutralButton("Depart", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                          fence.ARR_DEP = 1;
                          fence.DIST = DISTANCE;
                          setFence(fence);
                          // set into db
                          MainActivity.database.addFence(fence);
                          end();
                      }
                  })
                  .setNegativeButton("Arrive", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                          fence.ARR_DEP = 0;
                          fence.DIST = DISTANCE;
                          setFence(fence);
                          // set into db
                          MainActivity.database.addFence(fence);
                          end();
                      }
                  })
            .create().show();
        }
    }

    private void removeAddress(final Fence fence){
        // do shit to remove fence from memory
        removeFence(fence);
        // do shit to remove fence from database
        MainActivity.database.removeFence(fence.FID);
        finish();
    }

// close the keyboard by getting the input manager and hiding it.
    private void clearKeyboard(View view){
        if (view != null){
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

// function to set the geofence into memory
    private void setFence(final Fence fence){
        // bool ad is true if selection was depart, false if selection was arrive
        // Group, item, LAT and LNG exits already in this object
        // fence ID will consist of Group ID followed by Item ID

        // create geofence Object
        Geofence.Builder builder = new Geofence.Builder()
                .setRequestId(fenceID) // string to id this fence
                .setCircularRegion(fence.LAT, fence.LNG, fence.DIST)
                .setExpirationDuration(-1); // negative 1 in the constant for NEVER_EXPIRE
        if (fence.ARR_DEP == 1) builder.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT);
        else builder.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER);

        // create geofence request from geofence object
        GeofencingRequest.Builder GRBuild = new GeofencingRequest.Builder();
        GRBuild.addGeofence(builder.build());

        // create new intent for the monitoring service and pending intent data
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, MainActivity.database.getFenceSystemID(fence), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // send all to location services
        LocationServices.GeofencingApi.addGeofences(googleApiClient, GRBuild.build(),pendingIntent).setResultCallback(this);
    }
// function to remove the geofence from memory
private void removeFence(final Fence fence){
        // create geofence Object
        Geofence.Builder builder = new Geofence.Builder()
                .setRequestId(fenceID) // string to id this fence
                .setCircularRegion(fence.LAT, fence.LNG, fence.DIST)
                .setExpirationDuration(-1); // negative 1 in the constant for NEVER_EXPIRE
        if (fence.ARR_DEP == 1) builder.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT);
        else builder.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER);

        // create geofence request from geofence object
        GeofencingRequest.Builder GRBuild = new GeofencingRequest.Builder();
        GRBuild.addGeofence(builder.build());

        // create new intent for the monitoring service and pending intent data
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, MainActivity.database.getFenceSystemID(fence), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // send all to location services to remove match
        LocationServices.GeofencingApi.removeGeofences(googleApiClient,pendingIntent).setResultCallback(this);
    }

/// google API client callbacks (required for geofencing)
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
/// END GOOGLE CALLBACK SECTION

    private void end(){
    finish();
}

}

