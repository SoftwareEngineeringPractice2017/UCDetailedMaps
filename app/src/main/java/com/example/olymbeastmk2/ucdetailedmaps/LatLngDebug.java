package com.example.olymbeastmk2.ucdetailedmaps;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by Olymbeastmk2 on 22-Oct-17.
 */

public class LatLngDebug
{
    private FloatingActionButton debugFAB;
    private FloatingActionButton debugDisableFAB;
    private FloatingActionButton debugAddLocationFAB;
    private FloatingActionButton debugRemoveLastLocationFAB;
    private FloatingActionButton debugClearListFAB;
    private FloatingActionButton debugSaveToClipboard;
    private FloatingActionButton debugToggleBuildings;
    private EditText debugEntryField;

    private FloatingActionButton FPFAB;

    // Bool var for indicating whether we want buildings on or off
    boolean showBuildings = true;

    // The temporary placeholder for the current LatLng on the map selected
    Marker curMarker = null;

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

    // Array that holds all LatLngs to be sent by email
    ArrayList<NamedLocation> debugLatLngArray = new ArrayList<NamedLocation>();

    public LatLngDebug( final Activity MapsActivity, final GoogleMap mMap, final Context context, final ArrayList<Building> buildings )
    {
        debugFAB = ( FloatingActionButton ) MapsActivity.findViewById( R.id.debugFAB );
        debugDisableFAB = ( FloatingActionButton ) MapsActivity.findViewById( R.id.debugDisableFAB );
        debugAddLocationFAB = ( FloatingActionButton ) MapsActivity.findViewById( R.id.debugAddLocationFAB );
        debugRemoveLastLocationFAB = ( FloatingActionButton ) MapsActivity.findViewById( R.id.debugRemoveLastLocationFAB );
        debugClearListFAB = ( FloatingActionButton ) MapsActivity.findViewById( R.id.debugClearListFAB );
        debugSaveToClipboard = ( FloatingActionButton ) MapsActivity.findViewById( R.id.debugSaveToClipboardFAB );
        debugToggleBuildings = ( FloatingActionButton ) MapsActivity.findViewById( R.id.debugToggleBuildingsFAB );
        FPFAB = ( FloatingActionButton ) MapsActivity.findViewById( R.id.FPFAB );

        // Text field for inputting a label for the LatLng
        debugEntryField = ( EditText ) MapsActivity.findViewById( R.id.debugEntryField );

        // Set floating button to enable LatLng find mode
        debugFAB.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                // ANNOUNCE YOURSELF!
                Toast.makeText( context, "LATLNG DEBUG MODE ACTIVATED", Toast.LENGTH_SHORT ).show();

                // Disable these buttons
                debugFAB.setVisibility( FloatingActionButton.INVISIBLE );
                FPFAB.setVisibility( FloatingActionButton.INVISIBLE );

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
                        Toast.makeText( context, "Current LatLng set!", Toast.LENGTH_SHORT ).show();
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
                FPFAB.setVisibility( FloatingActionButton.VISIBLE );

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
                Toast.makeText( context, "LATLNG DEBUG MODE DISABLED", Toast.LENGTH_SHORT ).show();
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
                        Toast.makeText( context, "There's no name put in the field!", Toast.LENGTH_SHORT ).show();
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
                        Toast.makeText( context, "SAVED", Toast.LENGTH_SHORT ).show();
                    }
                }
                else
                {
                    Toast.makeText( context, "Current LatLng has not been set", Toast.LENGTH_SHORT ).show();
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

                        Toast.makeText( context, "The most previous location has been removed", Toast.LENGTH_SHORT ).show();
                    }
                    else
                    {
                        Toast.makeText( context, "There is nothing in the array!", Toast.LENGTH_SHORT ).show();
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

                    Toast.makeText( context, "Array completely cleared", Toast.LENGTH_SHORT ).show();
                }
                else
                {
                    Toast.makeText( context, "Array already empty!", Toast.LENGTH_SHORT ).show();
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
                    ClipboardManager cb = ( ClipboardManager ) MapsActivity.getSystemService( Context.CLIPBOARD_SERVICE );
                    ClipData clip = ClipData.newPlainText( "Location information", stringToCopy.toString() );
                    cb.setPrimaryClip( clip );

                    Toast.makeText( context, "Array saved to Clipboard!", Toast.LENGTH_SHORT ).show();
                }
                else
                {
                    Toast.makeText( context, "Array is empty! Can't save!", Toast.LENGTH_SHORT ).show();
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
                    for( Building b : buildings )
                    {
                        b.polygon.setVisible( false );
                    }

                    // Print a toast to success!
                    Toast.makeText( context, "Disabling Buildings", Toast.LENGTH_SHORT ).show();

                    // Flip bool so that the button will have the opposite functionality when Clicked
                    showBuildings = false;
                }
                else
                {
                    // Loop through each element in Polygon array and show them!
                    for( Building b : buildings )
                    {
                        b.polygon.setVisible( true );
                    }

                    // Print a toast to success!
                    Toast.makeText( context, "Enabling Buildings", Toast.LENGTH_SHORT ).show();

                    // Flip bool so that the button will have the opposite functionality when Clicked
                    showBuildings = true;
                }
            }
        } );
    }
}
