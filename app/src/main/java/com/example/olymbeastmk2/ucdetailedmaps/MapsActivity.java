package com.example.olymbeastmk2.ucdetailedmaps;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback
{

    //Map Stuff
    private GoogleMap mMap;

    //Database Stuff
    // A Handle to the applications resources
    public Resources resources;
    // The package name for easy use
    public static String PACKAGE_NAME;

    // Drawer stuff
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ArrayList<MenuItem> menuItems;
    private ArrayList<MenuItem> currentMenu;

    //Local stuff
    private ArrayList<Building> buildings;
    private HashMap<String, ArrayList<Icon>> icons;

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

        InitializeDrawer();

        mapFragment.getMapAsync( this );

    }

    private void InitializeDrawer()
    {
        //Drawer Layout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);



        FloatingActionButton mFAB = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(Gravity.START);
            }
        });

        //Clear the drawer when it is opened.
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {


            }

            @Override
            public void onDrawerClosed(View drawerView) {
                EditText SearchBox = (EditText) findViewById(R.id.editText);
                SearchBox.setText("");
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                refreshDrawerListView();
            }
        });

        EditText SearchBox = (EditText) findViewById(R.id.editText);
        SearchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                refreshDrawerListView();
            }
        });


        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MenuItemClicked(i);
            }
        });
    }

    public void refreshDrawerListView()
    {
        EditText SearchBox = (EditText) findViewById(R.id.editText);

        String search = SearchBox.getText().toString();

        //This should be moved at a later point. Should only be called once.
        menuItems = GetMenuItems();

        currentMenu = FilterMenuItems(menuItems, search);

        MenuAdapter menuAdapter = new MenuAdapter(this,
                R.layout.drawer_list_item,
                MenuItemsNames(currentMenu),
                currentMenu);

        mDrawerList.setAdapter(menuAdapter);
    }

    private ArrayList<MenuItem> GetMenuItems()
    {
        //ArrayList to be loaded and returned.
        ArrayList<MenuItem> output = new ArrayList<MenuItem>();

        //Add each building name as a MenuItem.
        for(Building b : buildings)
        {
            output.add(new MenuItem(b.getID(), b.getName()));
        }

        //Each key in icons represents an IconType.
        //Cylce through each IconType and add each as a new MenuItem.
        for(String s : icons.keySet())
        {
            output.add(new MenuItem(s));
        }

        //Sort output alphabetically.
        Collections.sort(output);

        return output;
    }

    private ArrayList<MenuItem> FilterMenuItems(ArrayList<MenuItem> input, String filter)
    {
        filter = filter.toLowerCase();
        //ArrayList to be loaded with MenuItems that start with filter.
        ArrayList<MenuItem> output = new ArrayList<MenuItem>();

        //Cycle through each MenuItem in input.
        for(MenuItem m : input)
        {
            if(m.text.toLowerCase().contains(filter))
            {
                output.add(m);
            }
        }

        return output;
    }

    private ArrayList<String> MenuItemsNames(ArrayList<MenuItem> input)
    {
        ArrayList<String> objects = new ArrayList<String>();
        for(MenuItem m : input)
        {
            objects.add(m.text);
        }
        return objects;
    }

    private void MenuItemClicked(int position)
    {
        float zoomLevel = 9;
        LatLng location = new LatLng(0,0);;

        MenuItem menuItem = currentMenu.get(position);

        if(menuItem.type == MenuItem.ItemType.Building)
        {
            for (Building b : buildings)
            {
                if(b.getID() == menuItem.id)
                {
                    location = LatLngTools.getCenter(b.getOutline());
                }
            }
        }
        else
        {

        }

        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( location, zoomLevel ) );
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

        // Add a marker in UC Campus
        LatLng Building22 = new LatLng( -35.240489, 149.088301 );
        LatLng UCBruceCampus = new LatLng( -35.238569, 149.086063 );
        mMap.addMarker( new MarkerOptions().position( Building22 ).title( "Building 22" ) );

        // Move camera to UC Bruce Campus and zoom in at a value of 16 ( up to 21 )
        float zoomLevel= 14;
        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( UCBruceCampus, zoomLevel ) );

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

        // Building Array
        buildings = dbBuildHelp.GetBuildings();

        for( Building b : buildings )
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



        icons = dbBuildHelp.GetIcons();

        for(String s : icons.keySet())
        {
            for(Icon i : icons.get(s))
            {
                //The icon to display for this location.
                int icon_icon = R.mipmap.ic_launcher; //R.mpmap.ic_launcher is temporary until icons are added.

            /* CODE TEMPLATE FOR ICON FILES
            //Set icon_icon to the right icon.
            switch(i.getType())
            {
                case "ATM" :
                    //icon_icon = R.mipmap.XXXXX;
                    break;
                case "Elevator" :
                    //icon_icon = R.mipmap.XXXXX;
                    break;
                default :
                    //icon_icon = R.mipmap.ERROR_ICON;
                    break;
            }
            */
                final Marker iconMarker = mMap.addMarker(new MarkerOptions()
                        .position(i.getLocation())
                        .icon(BitmapDescriptorFactory.fromResource(icon_icon))
                );
            }
        }



    }

}
