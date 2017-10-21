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
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
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

    // Inner Class for debug stuff only
    private class NamedLocation
    {
        public String name;
        public LatLng location;

        public NamedLocation( String _name, LatLng _location )
        {
            name = _name;
            location = _location;
        }
    }

    // Polygon Array for buildings for future use
    ArrayList<Polygon> polyBuildArr = new ArrayList<Polygon>();

    // Floor Plans Array
    HashMap<String, ArrayList<FloorPlan>> floorPlansHM;

    // An Iterator will be used to loop through this set
    Set<HashMap.Entry<String, ArrayList<FloorPlan>>> curFloorPlanSet = null;
    Iterator<HashMap.Entry<String, ArrayList<FloorPlan>>> cFIterator = null;

    // Current Floor Plan and building for Debugging
    FloorPlan curFloorPlan = null;
    String curBuilding = null;

    // Current Floor Plan Array num and the temp array
    int floorPlanArrayInd = 0;
    ArrayList<FloorPlan> floorPlanArr = null;

    //Database Stuff
    // A Handle to the applications resources
    public Resources resources;
    // The package name for easy use
    public static String PACKAGE_NAME;

    // Drawer stuff
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private MenuHandler menuHandler;

    // Array that holds all LatLngs to be sent by email
    ArrayList<NamedLocation> debugLatLngArray = new ArrayList<NamedLocation>();

    // The temporary placeholder for the current LatLng on the map selected
    Marker curMarker = null;

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
        String msg = "Updated Location: " + Double.toString( location.getLatitude() ) + Double.toString( location.getLongitude() );
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

        // Set up debug LatLng Menu
        InitializeLatLngMenu();

        // Start updating the user's location
        startLocationUpdates();

        mapFragment.getMapAsync( this );
    }

    // Bool var for indicating whether we want buildings on or off
    boolean showBuildings = true;

    private void InitializeLatLngMenu()
    {
        // Find the second floating action button
        final FloatingActionButton debugFAB = ( FloatingActionButton ) findViewById( R.id.debugFAB );
        final FloatingActionButton debugDisableFAB = ( FloatingActionButton ) findViewById( R.id.debugDisableFAB );
        final FloatingActionButton debugAddLocationFAB = ( FloatingActionButton ) findViewById( R.id.debugAddLocationFAB );
        final FloatingActionButton debugRemoveLastLocationFAB = ( FloatingActionButton ) findViewById( R.id.debugRemoveLastLocationFAB );
        final FloatingActionButton debugClearListFAB = ( FloatingActionButton ) findViewById( R.id.debugClearListFAB );
        final FloatingActionButton debugSaveToClipboard = ( FloatingActionButton ) findViewById( R.id.debugSaveToClipboardFAB );
        final FloatingActionButton debugToggleBuildings = ( FloatingActionButton ) findViewById( R.id.debugToggleBuildingsFAB );

        final FloatingActionButton FPFAB = ( FloatingActionButton ) findViewById( R.id.FPFAB );
        final FloatingActionButton FPDisableFAB = ( FloatingActionButton ) findViewById( R.id.FPDisableFAB );
        final FloatingActionButton FPDownFAB = ( FloatingActionButton ) findViewById( R.id.FPDownFAB );
        final FloatingActionButton FPLeftFAB = ( FloatingActionButton ) findViewById( R.id.FPLeftFAB );
        final FloatingActionButton FPRightFAB = ( FloatingActionButton ) findViewById( R.id.FPRightFAB );
        final FloatingActionButton FPUpFAB = ( FloatingActionButton ) findViewById( R.id.FPUpFAB );
        final FloatingActionButton FPSelectFAB = ( FloatingActionButton ) findViewById( R.id.FPSelectFAB );

        // Text field for inputting a label for the LatLng
        final EditText debugEntryField = ( EditText ) findViewById( R.id.debugEntryField );

        // Set floating button to enable LatLng find mode
        debugFAB.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                // ANNOUNCE YOURSELF!
                Toast.makeText( getApplicationContext(), "LATLNG DEBUG MODE ACTIVATED", Toast.LENGTH_SHORT ).show();

                // Disable this button
                debugFAB.setVisibility( FloatingActionButton.INVISIBLE );

                // Enable the other buttons
                debugDisableFAB.setVisibility( FloatingActionButton.VISIBLE );
                debugAddLocationFAB.setVisibility( FloatingActionButton.VISIBLE );
                debugRemoveLastLocationFAB.setVisibility( FloatingActionButton.VISIBLE );
                debugClearListFAB.setVisibility( FloatingActionButton.VISIBLE );
                debugSaveToClipboard.setVisibility( FloatingActionButton.VISIBLE );
                debugToggleBuildings.setVisibility( FloatingActionButton.VISIBLE );

                // Enable text field
                debugEntryField.setVisibility( EditText.VISIBLE );

                // Have that when the button is clicked. We create a function that listens for taps
                mMap.setOnMapClickListener( new GoogleMap.OnMapClickListener()
                {
                    @Override
                    public void onMapClick( LatLng latLng )
                    {
                        // Check to see if a marker was set
                        if( curMarker != null )
                        {
                            // Remove the last marker, so a new one is created below
                            curMarker.remove();
                            curMarker = null;
                        }

                        // Set a marker down to show where the LatLng is
                        MarkerOptions tmpMarkOpt = new MarkerOptions().icon( BitmapDescriptorFactory.fromResource( R.mipmap.xmarksthespot ) ).position( latLng ).anchor( 0.5f, 0.5f );
                        curMarker = mMap.addMarker( tmpMarkOpt );

                        // Announce!
                        Toast.makeText( getApplicationContext(), "Current LatLng set!", Toast.LENGTH_SHORT ).show();
                    }
                } );
            }
        } );

        // Set this button to disable LatLng mode
        debugDisableFAB.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                // This removes any function set for clicking on the map ( i.e. including what was set above this code )
                mMap.setOnMapClickListener( null );

                // Make the other button visible again
                debugFAB.setVisibility( FloatingActionButton.VISIBLE );

                // Make all the buttons invisible ( except the enable button of course )
                debugDisableFAB.setVisibility( FloatingActionButton.INVISIBLE );
                debugAddLocationFAB.setVisibility( FloatingActionButton.INVISIBLE );
                debugRemoveLastLocationFAB.setVisibility( FloatingActionButton.INVISIBLE );
                debugClearListFAB.setVisibility( FloatingActionButton.INVISIBLE );
                debugSaveToClipboard.setVisibility( FloatingActionButton.INVISIBLE );
                debugToggleBuildings.setVisibility( FloatingActionButton.INVISIBLE );

                // Disable text field
                debugEntryField.setVisibility( EditText.INVISIBLE );

                // Remove the 'X' marker if it is there
                if( curMarker != null )
                {
                    curMarker.remove();
                    curMarker = null;
                }

                // ANNOUNCE!
                Toast.makeText( getApplicationContext(), "LATLNG DEBUG MODE DISABLED", Toast.LENGTH_SHORT ).show();
            }
        } );

        // Set this button to save the current latlng to an ArrayList
        debugAddLocationFAB.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                // Save the location of the marker to the array
                if( curMarker != null )
                {
                    // Check to see if a label has been put in for this LatLng. Simply Continue if there is something in there!
                    String entFieldString = debugEntryField.getText().toString();
                    if( entFieldString.isEmpty() )
                    {
                        Toast.makeText( getApplicationContext(), "There's no name put in the field!", Toast.LENGTH_SHORT ).show();
                    }
                    else
                    {
                        // Create a temporary named location
                        NamedLocation tmpLoc = new NamedLocation( entFieldString, curMarker.getPosition() );

                        // Save label
                        tmpLoc.name = entFieldString;

                        // Save location
                        debugLatLngArray.add( tmpLoc );

                        // Remove the marker
                        curMarker.remove();
                        curMarker = null;

                        // ANNOUNCE
                        Toast.makeText( getApplicationContext(), "SAVED", Toast.LENGTH_SHORT ).show();
                    }
                }
                else
                {
                    Toast.makeText( getApplicationContext(), "Current LatLng has not been set", Toast.LENGTH_SHORT ).show();
                }
            }
        } );

        // Set this button to remove the last location in the arrayList
        debugRemoveLastLocationFAB.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                if( curMarker != null )
                {
                    // Remove last location from array
                    if( debugLatLngArray.size() > 0 )
                    {
                        debugLatLngArray.remove( debugLatLngArray.size() - 1 );

                        Toast.makeText( getApplicationContext(), "The most previous location has been removed", Toast.LENGTH_SHORT ).show();
                    }
                    else
                    {
                        Toast.makeText( getApplicationContext(), "There is nothing in the array!", Toast.LENGTH_SHORT ).show();
                    }
                }

            }
        } );

        // Set this button to clear the entire list
        debugClearListFAB.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                if( debugLatLngArray.size() > 0 )
                {
                    debugLatLngArray.clear();

                    Toast.makeText( getApplicationContext(), "Array completely cleared", Toast.LENGTH_SHORT ).show();
                }
                else
                {
                    Toast.makeText( getApplicationContext(), "Array already empty!", Toast.LENGTH_SHORT ).show();
                }
            }
        } );

        // Set this button to save the entire list to the clipboard
        debugSaveToClipboard.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                if( debugLatLngArray.size() > 0 )
                {
                    // String Builder for making the full LatLng string to paste to clipboard
                    StringBuilder stringToCopy = new StringBuilder( "" );

                    // Loop through the LatLng arrayList
                    for( NamedLocation n : debugLatLngArray )
                    {
                        stringToCopy.append( n.name );
                        stringToCopy.append( " " );
                        stringToCopy.append( Double.toString( n.location.latitude ) );
                        stringToCopy.append( ", " );
                        stringToCopy.append( Double.toString( n.location.longitude ) );
                        stringToCopy.append( "\n" );
                    }

                    // Got info from here: https://stackoverflow.com/questions/19253786/how-to-copy-text-to-clip-board-in-android
                    // This will copy the Location information into the clipboard
                    ClipboardManager cb = ( ClipboardManager ) getSystemService( Context.CLIPBOARD_SERVICE );
                    ClipData clip = ClipData.newPlainText( "Location information", stringToCopy.toString() );
                    cb.setPrimaryClip( clip );

                    Toast.makeText( getApplicationContext(), "Array saved to Clipboard!", Toast.LENGTH_SHORT ).show();
                }
                else
                {
                    Toast.makeText( getApplicationContext(), "Array is empty! Can't save!", Toast.LENGTH_SHORT ).show();
                }
            }
        } );

        // Set this button to toggle buildings on and off
        debugToggleBuildings.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                // Buildings are shown, toggle off
                if( showBuildings )
                {
                    // Loop through all the Building Polygons, making them invisible
                    for( Polygon p : polyBuildArr )
                    {
                        p.setVisible( false );
                    }

                    // Print a toast to success!
                    Toast.makeText( getApplicationContext(), "Disabling Buildings", Toast.LENGTH_SHORT ).show();

                    // Flip bool so that the button will have the opposite functionality when Clicked
                    showBuildings = false;
                }
                else
                {
                    // Loop through each element in Polygon array and show them!
                    for( Polygon p : polyBuildArr )
                    {
                        p.setVisible( true );
                    }

                    // Print a toast to success!
                    Toast.makeText( getApplicationContext(), "Enabling Buildings", Toast.LENGTH_SHORT ).show();

                    // Flip bool so that the button will have the opposite functionality when Clicked
                    showBuildings = true;
                }
            }
        } );

        // Set this button to enable Floor Plans Debug Mode
        FPFAB.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                FPFAB.setVisibility( FloatingActionButton.INVISIBLE );
                debugFAB.setVisibility( FloatingActionButton.INVISIBLE );

                FPDisableFAB.setVisibility( FloatingActionButton.VISIBLE );
                FPDownFAB.setVisibility( FloatingActionButton.VISIBLE );
                FPUpFAB.setVisibility( FloatingActionButton.VISIBLE );
                FPLeftFAB.setVisibility( FloatingActionButton.VISIBLE );
                FPRightFAB.setVisibility( FloatingActionButton.VISIBLE );
                FPSelectFAB.setVisibility( FloatingActionButton.VISIBLE );

                // Announce!
                Toast.makeText( getApplicationContext(), "Floor Plan Debug Mode Activated", Toast.LENGTH_SHORT ).show();
            }
        } );

        // Set this button to disable Floor Plans Debug Mode
        FPDisableFAB.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                FPFAB.setVisibility( FloatingActionButton.VISIBLE );
                debugFAB.setVisibility( FloatingActionButton.VISIBLE );

                FPDisableFAB.setVisibility( FloatingActionButton.INVISIBLE );
                FPDownFAB.setVisibility( FloatingActionButton.INVISIBLE );
                FPUpFAB.setVisibility( FloatingActionButton.INVISIBLE );
                FPLeftFAB.setVisibility( FloatingActionButton.INVISIBLE );
                FPRightFAB.setVisibility( FloatingActionButton.INVISIBLE );
                FPSelectFAB.setVisibility( FloatingActionButton.INVISIBLE );

                // Announce!
                Toast.makeText( getApplicationContext(), "Floor Plan Debug Mode Disabled", Toast.LENGTH_SHORT ).show();
            }
        } );

        // Set this button to select the next floor Plan
        FPSelectFAB.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                if( curFloorPlanSet == null )
                {
                    // Initialize entry set and iterator
                    curFloorPlanSet = floorPlansHM.entrySet();
                    cFIterator = curFloorPlanSet.iterator();

                }

                // Check to see if the index has exceeded the arrays size
                if( floorPlanArr != null )
                {
                    if( floorPlanArrayInd + 1 >= floorPlanArr.size() )
                    {
                        // See the floor plan index back to 0 and return
                        floorPlanArrayInd = 0;
                    }
                }

                // Check to see if we have started already reading the array
                // ( And if we need to continue reading it
                if( floorPlanArrayInd == 0 )
                {
                    if( cFIterator.hasNext() )
                    {
                        // Only get it ONCE
                        HashMap.Entry<String, ArrayList<FloorPlan>> tmpEntry = cFIterator.next();

                        floorPlanArr = tmpEntry.getValue();

                        // Set the Current Floor plan
                        curFloorPlan = floorPlanArr.get( floorPlanArrayInd );
                        curBuilding = tmpEntry.getKey();
                    }
                }
                // Continue Looping through array
                else
                {
                    // Get next one
                    curFloorPlan = floorPlanArr.get( floorPlanArrayInd );
                }

                // Print out the currently selected plan and building. Toast it!
                Toast.makeText( getApplicationContext(), "Current Selection: " + curBuilding + " " + curFloorPlan.floor, Toast.LENGTH_SHORT ).show();

                // Inc.
                floorPlanArrayInd++;
            }
        } );
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

        // Fill Floor plans hashmap
        floorPlansHM = dbBuildHelp.GetPlans();
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

        // Check permissions for locations!
        if( checkPermissions() )
        {
            googleMap.setMyLocationEnabled( true );
        }

        // Set Maximum and Minimum Zoom
        mMap.setMaxZoomPreference( 60 );
        mMap.setMinZoomPreference( 10 );

        // Add a marker in UC Campus
        LatLng Building22 = new LatLng( -35.240489, 149.088301 );
        LatLng UCBruceCampus = new LatLng( -35.238569, 149.086063 );
        mMap.addMarker( new MarkerOptions().position( Building22 ).title( "Building 22" ) );

        // Move camera to UC Bruce Campus and zoom in at a value of 16 ( up to 21 )
        float zoomLevel= 14;
        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( UCBruceCampus, zoomLevel ) );

        // Add floor plans
        for( HashMap.Entry<String, ArrayList<FloorPlan>> entry : floorPlansHM.entrySet() )
        {
            String key = entry.getKey();
            ArrayList<FloorPlan> value = entry.getValue();

            for( FloorPlan fp : value )
            {
                // Get full name of file
                StringBuilder tmpStringBuild = new StringBuilder( "f" );
                String[] tmpStringArr = key.split( " " );
                tmpStringBuild.append( tmpStringArr[1] );
                tmpStringBuild.append( fp.floor.toLowerCase() );

                int resID = getResources().getIdentifier( tmpStringBuild.toString(), "drawable", getPackageName() );
                Log.d( "UCDetailedMaps", "Resource String is: " + tmpStringBuild.toString() );
                GroundOverlayOptions tmpOverlay = new GroundOverlayOptions()
                        .image( BitmapDescriptorFactory.fromResource( resID ) )
                        .position( fp.latLng, fp.scale );
                fp.groundMapRef = mMap.addGroundOverlay( tmpOverlay );
            }
        }

        for( Building b : buildings )
        {
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
            Polygon bPoly = mMap.addPolygon( bOptions );

            // Add this to the Building Polygon Array for Future Use
            polyBuildArr.add( bPoly );
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
                    // Loop through all the Building Polygons, making them invisible
                    for( Polygon p : polyBuildArr )
                    {
                        p.setVisible( false );
                    }
                }
                else
                {
                    // Loop through all the Building Polygons, making them visible
                    for( Polygon p : polyBuildArr )
                    {
                        p.setVisible( true );
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
