package com.example.olymbeastmk2.ucdetailedmaps;

import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.graphics.BitmapFactory;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static android.R.attr.label;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback
{
    //Map Stuff
    private GoogleMap mMap;

    //Location Stuff
    private FusedLocationProviderClient mFusedLocationClient;

    // Location Request
    private LocationRequest mLocationRequest;

    // 10 Seconds
    private long UPDATE_INTERVAL = 10 * 1000;

    // 2 Seconds
    private long FASTEST_INTERVAL = 2 * 1000;

    // Polygon Array for buildings for future use
    //ArrayList<Polygon> polyBuildArr = new ArrayList<Polygon>();
    // Commented out, Polygons are now kept in the Building class. - Riley

    //Database Stuff
    // A Handle to the applications resources
    public Resources resources;
    // The package name for easy use
    public static String PACKAGE_NAME;

    // Drawer stuff
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private MenuHandler menuHandler;

    //Local stuff
    private ArrayList< Building > buildings;
    private HashMap< String, ArrayList< Icon > > icons;

    private LatLng myLocation;

    public void getLastLocation()
    {
        // Get the last known recent location
        FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient( this );

        locationClient.getLastLocation()
                .addOnSuccessListener( new OnSuccessListener< Location >()
        {
            @Override
            public void onSuccess( Location location )
            {
                // GPS Location can be null if GPS is switched off
                if( location != null )
                {
                    onLocationChanged( location );
                }
            }
        } )
                .addOnFailureListener( new OnFailureListener()
                {
                    @Override
                    public void onFailure( @NonNull Exception e )
                    {
                        Log.e( "UCDetailedMaps", "Error trying to last GPS location" );
                        e.printStackTrace();
                    }
                });
    }

    // The marker that will be used to show the user's location
    private Marker userLocMarker = null;

    public void onLocationChanged( Location location )
    {
        // The new location has now been determined
        // String msg = "Updated Location: " + Double.toString( location.getLatitude() ) + Double.toString( location.getLongitude() );
        // Toast.makeText( this, msg, Toast.LENGTH_SHORT ).show();

        // A LatLnt Object for use with maps is now created!
        LatLng userLocation = new LatLng( location.getLatitude(), location.getLongitude() );

        // move global myLocation.
        myLocation = new LatLng(userLocation.latitude, userLocation.longitude);

        // Create a marker on the users position ( if there is not already one there )
        if( userLocMarker == null )
        {
            // Create a new marker
            userLocMarker = mMap.addMarker( new MarkerOptions().position( userLocation ).icon( BitmapDescriptorFactory.fromResource( R.mipmap.panda ) ) );

            Log.d( "UCDetailedMaps", "CREATED A PANDA :D" + Double.toString( userLocation.latitude ) + Double.toString( userLocation.longitude ) );
        }
        // If the marker is already created, move it!
        else
        {
            userLocMarker.setPosition( userLocation );

            Log.d( "UCDetailedMaps", "UPDATED THE PANDA! AT" + Double.toString( userLocation.latitude ) + Double.toString( userLocation.longitude ) );
        }
    }

    protected void startLocationUpdates()
    {
        // Create location request to start recieving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY );
        mLocationRequest.setInterval( UPDATE_INTERVAL );
        mLocationRequest.setFastestInterval( FASTEST_INTERVAL );

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest( mLocationRequest );
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient( this );
        settingsClient.checkLocationSettings( locationSettingsRequest );

        LocationServices.getFusedLocationProviderClient( this ).requestLocationUpdates( mLocationRequest,
                new LocationCallback()
                {
                    @Override
                    public void onLocationResult( LocationResult locationResult )
                    {
                        try
                        {
                            onLocationChanged( locationResult.getLastLocation() );
                        }
                        catch( SecurityException e )
                        {
                            checkPermissions();
                        }
                    }
                },
        Looper.myLooper() );
    }

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        // Package name init
        PACKAGE_NAME = this.getPackageName();

        setContentView( R.layout.activity_maps );
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = ( SupportMapFragment ) getSupportFragmentManager()
                .findFragmentById( R.id.map );

        // Location help
        // https://github.com/codepath/android_guides/wiki/Retrieving-Location-with-LocationServices-API

        // Location stuff.
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient( this );

        // Load buildings and icons with their respective data.
        databaseLoadsIconsAndBuildings();

        // Setup the drawer.
        InitializeDrawer();
        menuHandler = new MenuHandler(this);
        menuHandler.populate(buildings, icons);

        // Start updating the user's location
        startLocationUpdates();

        mapFragment.getMapAsync( this );
    }

    // Init Drawer
    private void InitializeDrawer()
    {
        //Drawer Layout
        mDrawerLayout = ( DrawerLayout ) findViewById( R.id.drawer_layout );
        mDrawerList = ( ListView ) findViewById( R.id.left_drawer );

        FloatingActionButton mFAB = ( FloatingActionButton ) findViewById( R.id.floatingActionButton );
        mFAB.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                mDrawerLayout.openDrawer( Gravity.START );
            }
        } );

        //Clear the drawer when it is opened.
        mDrawerLayout.addDrawerListener( new DrawerLayout.DrawerListener()
        {
            @Override
            public void onDrawerSlide( View drawerView, float slideOffset )
            {

            }

            @Override
            public void onDrawerOpened( View drawerView )
            {

            }

            @Override
            public void onDrawerClosed( View drawerView )
            {
                EditText SearchBox = ( EditText ) findViewById( R.id.editText );
                SearchBox.setText( "" );
            }

            @Override
            public void onDrawerStateChanged( int newState )
            {
                refreshDrawerListView();
            }
        } );

        EditText SearchBox = ( EditText ) findViewById( R.id.editText );
        SearchBox.addTextChangedListener( new TextWatcher()
        {
            @Override
            public void beforeTextChanged( CharSequence charSequence, int i, int i1, int i2 )
            {

            }

            @Override
            public void onTextChanged( CharSequence charSequence, int i, int i1, int i2 )
            {

            }

            @Override
            public void afterTextChanged( Editable editable )
            {
                refreshDrawerListView();
            }
        } );
    }

    // Refresh Drawer List
    public void refreshDrawerListView()
    {
        EditText SearchBox = ( EditText ) findViewById( R.id.editText );

        String search = SearchBox.getText().toString();

        //This should be moved at a later point. Should only be called once.
        //menuHandler.populate(buildings, icons);

        menuHandler.filter( search );

        MenuAdapter menuAdapter = new MenuAdapter( this,
                R.layout.drawer_list_item,
                menuHandler);

        mDrawerList.setAdapter( menuAdapter );
    }

    public void setVisibleIconsWithType(String type, boolean hidden)
    {
        for(Icon i : icons.get(type))
        {
            i.setHidden(hidden);
        }
    }

    public void FocusOnMenuItem(int menuItemPosition)
    {
        mDrawerLayout = ( DrawerLayout ) findViewById( R.id.drawer_layout );
        mDrawerLayout.closeDrawers();

        float zoomLevel = 18;
        LatLng location = new LatLng( 0, 0 );

        MenuItem menuItem = menuHandler.get(menuItemPosition);

        if(menuItem.type == MenuItem.ItemType.Building)
        {
            for( Building b : buildings )
            {
                if( b.getID() == menuItem.id )
                {
                    location = LatLngTools.getCenter( b.getOutline() );
                    Toast toast = Toast.makeText(getApplicationContext(), b.getName(), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }
        else
        {
            Icon icon;
            if(myLocation != null)
            {
                icon = LatLngTools.findClosestIcon(myLocation, icons.get(menuItem.text));
            }
            else
            {
                icon = icons.get(menuItem.text).get(0);

            }

            location = icon.getLocation();
            icon.showTitle();

            Toast toast = Toast.makeText(getApplicationContext(), "Closest " + icon.getType(), Toast.LENGTH_SHORT);
            toast.show();
        }

        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( location, zoomLevel ) );
    }


    private boolean checkPermissions()
    {
        if( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED )
        {
            return true;
        }
        else
        {
            requestPermissions();
            return false;
        }
    }

    private void requestPermissions()
    {
        // KEEP AN EYE ON THIS LINE, INT SHOULD PROBABLY NOT BE 0
        ActivityCompat.requestPermissions( this, new String[]{ android.Manifest.permission.ACCESS_FINE_LOCATION }, 0 );
    }

    private void databaseLoadsIconsAndBuildings()
    {
        // Initialize Database helper for all database needs
        DbHelper dbBuildHelp = new DbHelper( this, "UCMapsDB", null, 1 );

        //Temporary code for development. The database is constant even after updates in code.
        //Therefore it is necessary to rebuild it each time the app is debugged, in case of changes.
        dbBuildHelp.ClearEverything();
        dbBuildHelp.BuildEverything();

        // Get the resources ( mainly for the CSV file at the moment )
        resources = getResources();

        // Create a uri ( Full resource path name ) to get an ID from
        String uri = MapsActivity.PACKAGE_NAME + ":raw/gpscoord2";

        Log.d( "UCDetailedMap", "URI is: " + uri );

        // Get the Resource ID of the CSV file
        int rID = resources.getIdentifier( uri, null, null );

        // Load the information from the CSV file into the database
        // As GPSCoord is a raw resource, the extension is not included
        dbBuildHelp.ImportCSVBuildings( rID, resources );

        // Generate all Bitmaps for icons
        dbBuildHelp.GenerateAllTypeImages( getResources() );

        // Fill Building Array
        buildings = dbBuildHelp.GetBuildings();

        // Fill Icons Dictionary
        icons = dbBuildHelp.GetIcons();
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
        Toast.makeText( getApplicationContext(), "MAP IS READY", Toast.LENGTH_SHORT ).show();

        mMap = googleMap;

        googleMap.setMapStyle( MapStyleOptions.loadRawResourceStyle( this, R.raw.mapstyle));

        // Set up LatLng Debug Menu
        LatLngDebug llDBG = new LatLngDebug( this, mMap, getApplicationContext(), buildings );

        // Set up Floor Plan Debug Menu
        FloorPlanDebug fpDBG = new FloorPlanDebug( this, getApplicationContext(), buildings );

        // Check permissions for locations!
        if( checkPermissions() )
        {
            googleMap.setMyLocationEnabled( true );
        }

        // Set Maximum and Minimum Zoom
        mMap.setMaxZoomPreference( 60 );
        mMap.setMinZoomPreference( 10 );

//        // Add a marker in UC Campus
//        LatLng Building22 = new LatLng( -35.240489, 149.088301 );
        LatLng UCBruceCampus = new LatLng( -35.238569, 149.086063 );
//        mMap.addMarker( new MarkerOptions().position( Building22 ).title( "Building 22" ) );

        // Move camera to UC Bruce Campus and zoom in at a value of 16 ( up to 21 )
        float zoomLevel= 14;
        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( UCBruceCampus, zoomLevel ) );



        for( Building b : buildings )
        {
            // Add floor plans
            for( FloorPlan fp : b.getFloorPlans() )
            {
                // Get full name of file
                String fullFileName = fp.GetResourceString( b.getName() );

                int resID = getResources().getIdentifier( fullFileName, "drawable", getPackageName() );
                Log.d( "UCDetailedMaps", "Resource String is: " + fullFileName );
                GroundOverlayOptions tmpOverlay = new GroundOverlayOptions()
                        .image( BitmapDescriptorFactory.fromResource( resID ) )
                        .position( fp.latLng, fp.scale );
                fp.groundMapRef = mMap.addGroundOverlay( tmpOverlay );
            }

            b.hideFloorPlans();

            // Get the building's outline coordinates from the database
            ArrayList<LatLng> latLngArr = b.getOutline();

            // If there are no points in this array, then skip to the next building
            if( latLngArr.isEmpty() )
            {
                continue;
            }

            // Instantiates a new Polyline object and add points to make the building
            PolygonOptions bOptions = new PolygonOptions().addAll( latLngArr ).fillColor( 0xFFFFFFFF );

            // Add the Polygon to Google maps
            // Store the polygon on the building, for future manipulation.
            b.polygon = mMap.addPolygon( bOptions );

            // Add this to the Building Polygon Array for Future Use
            // polyBuildArr.add( bPoly );
        }

        for(String s : icons.keySet())
        {
            for(Icon i : icons.get(s))
            {
                i.AddAsMarker( mMap, getResources() );
            }
        }

        for(MenuItem m : menuHandler.getMenuItems())
        {
            if(m.type == MenuItem.ItemType.Icon)
            {
                setVisibleIconsWithType(m.text, !m.checked);
            }
        }



        // Set up listener to hide or show buildings
        mMap.setOnCameraMoveListener( new GoogleMap.OnCameraMoveListener()
        {
            @Override
            public void onCameraMove()
            {
                CameraPosition cameraPosition = mMap.getCameraPosition();
                if( cameraPosition.zoom > 19.0 )
                {
//                    // Loop through all the Building Polygons, making them invisible
//                    for( Building b : buildings)
//                    {
//                        b.polygon.setVisible( false );
//                    }
//
//                    Building focusedBuilding = LatLngTools.findClosestBuilding(cameraPosition.target, buildings);
//
//                    if(!focusedBuilding.isFocused)
//                    {
//                        Log.d( "UCDetailedMaps", "Focused building changed. New target: " + focusedBuilding.getName());
//                        for( Building b : buildings)
//                        {
//                            b.polygon.setVisible( true );
//                            b.isFocused = false;
//                        }
//                        focusedBuilding.polygon.setVisible(false);
//                        focusedBuilding.isFocused = true;
//
//                        for (Building b : buildings)
//                        {
//                            if(b.getID() != focusedBuilding.getID())
//                            {
//                                b.hideRooms();
//                            }
//                        }
//                        focusedBuilding.showRooms(0, getApplicationContext(), mMap);
//                    }

                    /*for( Building b : buildings )
                    {
                        b.hideRooms();
                        b.polygon.setVisible( true );
                        b.isFocused = false;
                    }*/

                    ArrayList<Building> closestBuildings = LatLngTools.findClosestBuildings( cameraPosition.target, buildings );

                    for( Building b : closestBuildings )
                    {
                        b.showFloorPlans();
                        b.polygon.setVisible( false );
                        b.isFocused = true;
                        b.showRooms( 0, getApplicationContext(), mMap );
                    }
                }
                else
                {
                    // Loop through all the Building Polygons, making them visible
                    for( Building b : buildings )
                    {
                        b.hideFloorPlans();
                        b.polygon.setVisible( true );
                        b.isFocused = false;
                        b.hideRooms();
                    }
                }
            }
        });

    }

    // Code was obtained from this location: https://stackoverflow.com/questions/33696488/getting-bitmap-from-vector-drawable
    // +++
    public Bitmap getBitmapFromVectorDrawable( Context context, int drawableId )
    {
        Drawable drawable = ContextCompat.getDrawable( context, drawableId );
        if( Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP )
        {
            drawable = ( DrawableCompat.wrap( drawable ) ).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap( drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888 );
        Canvas canvas = new Canvas( bitmap );
        drawable.setBounds( 0, 0, canvas.getWidth(), canvas.getHeight() );
        drawable.draw( canvas );

        return bitmap;
    }

}
