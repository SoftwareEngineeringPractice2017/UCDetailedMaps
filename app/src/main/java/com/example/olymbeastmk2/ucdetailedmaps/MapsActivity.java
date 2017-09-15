package com.example.olymbeastmk2.ucdetailedmaps;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback
{

    private GoogleMap mMap;

    // A Handle to the applications resources
    public Resources resources;

    // The package name for easy use
    public static String PACKAGE_NAME;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        // Package name init
        PACKAGE_NAME = getApplicationContext().getPackageName();

        setContentView( R.layout.activity_maps );
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = ( SupportMapFragment ) getSupportFragmentManager()
                .findFragmentById( R.id.map );
        mapFragment.getMapAsync( this );
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
    public void onMapReady( GoogleMap googleMap )
    {
        mMap = googleMap;

        // Set Maximum and Minimum Zoom
        mMap.setMaxZoomPreference( 27 );
        mMap.setMinZoomPreference( 12 );

        // Add a marker in Sydney and move the camera
        LatLng Building22 = new LatLng( -35.240489, 149.088301 );
        LatLng UCBruceCampus = new LatLng( -35.238569, 149.086063 );
        mMap.addMarker( new MarkerOptions().position( Building22 ).title( "Building 22" ) );

        // Move camera to UC Bruce Campus and zoom in at a value of 16 ( up to 21 )
        float zoomLevel= 14;
        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( UCBruceCampus, zoomLevel ) );

        // Initialize Database helper for all database needs
        DbHelper dbBuildHelp = new DbHelper( this, "UCMapsDB", null, 1 );

        // Get the resources ( mainly for the CSV file at the moment )
        resources = getResources();

        // Create a uri ( Full resource path name ) to get an ID from
        String uri = MapsActivity.PACKAGE_NAME + ":raw/gpscoord";

        Log.d( "UCDetailedMap", "URI is: " + uri );

        // Get the Resource ID of the CSV file
        int rID = resources.getIdentifier( uri, null, null );

        // Load the information from the CSV file into the database
        // As GPSCoord is a raw resource, the extension is not included
        dbBuildHelp.ImportCSVBuildings( rID, resources );

        // Building Array
        ArrayList<Building> tmpBuildArr = dbBuildHelp.GetBuildings();

        for( Building b : tmpBuildArr )
        {
            // Get the building's outline coordinates from the database
            ArrayList<LatLng> latLngArr = b.getOutline();

            // If there are no points in this array, then skip to the next building
            // ( Not sure if still valid... )
            if( latLngArr.isEmpty() )
            {
                continue;
            }

            // Instantiates a new Polyline object and add points to make the building
            PolygonOptions bOptions = new PolygonOptions().addAll( latLngArr ).fillColor( 0xFFFF00FF );

            // Add the Polygon to Google maps
            Polygon bPoly = mMap.addPolygon( bOptions );
        }
    }
}
