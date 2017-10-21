package com.example.olymbeastmk2.ucdetailedmaps;

import android.graphics.drawable.VectorDrawable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.LatLng;

import static android.R.attr.key;

/**
 * Created by Olymbeastmk2 on 18-Oct-17.
 */

public class FloorPlan
{
    public LatLng latLng;
    public double rotationDegrees;
    public int floor;
    public String floorName;
    public float scale;
    public GroundOverlay groundMapRef;

    public FloorPlan( LatLng cll, double rD, int f, String fN, float s )
    {
        latLng = cll;
        rotationDegrees = rD;
        floor = f;
        floorName = fN;
        scale = s;
    }

    // This gets the proper resource name string from the full name pulled from the database
    public String GetResourceString( String buildingName )
    {
        StringBuilder tmpStringBuild = new StringBuilder( "" );

        // Replace all information: https://stackoverflow.com/questions/5455794/removing-whitespace-from-strings-in-java
        // More info: https://stackoverflow.com/questions/23332146/remove-punctuation-preserve-letters-and-white-space-java-regex
        tmpStringBuild.append( buildingName.toLowerCase().replaceAll( "\\W","" ) );

        tmpStringBuild.append( "f" );
        tmpStringBuild.append( floor );

        return tmpStringBuild.toString();
    }
}
