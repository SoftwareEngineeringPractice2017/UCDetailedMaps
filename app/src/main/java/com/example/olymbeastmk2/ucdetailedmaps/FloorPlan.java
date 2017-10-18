package com.example.olymbeastmk2.ucdetailedmaps;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Olymbeastmk2 on 18-Oct-17.
 */

public class FloorPlan
{
    public LatLng cornerLatLng;
    public double rotationDegrees;


    public FloorPlan( LatLng cll, double rD )
    {
        cornerLatLng = cll;
        rotationDegrees = rD;
    }
}
