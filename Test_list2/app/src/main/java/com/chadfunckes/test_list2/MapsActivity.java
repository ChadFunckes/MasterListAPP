package com.chadfunckes.test_list2;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity {
    private final static String TAG = "Maps Activity";
    private String CALLED_ON;
    int GID, IID, AID;
    static String GROUP_NAME, ITEM_NAME;
    private Context mContext;

    boolean mShowMap;
    GoogleMap mMap;
    SupportMapFragment mapFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
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
    }

    private boolean initMap() {
        if (mMap == null){
            mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
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

    public void AddressToMap(View view) {
        String inputAddress;
        List<Address> address = null;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        EditText input = (EditText)findViewById(R.id.addyEditTxt);
        inputAddress = input.getText().toString();

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
}
