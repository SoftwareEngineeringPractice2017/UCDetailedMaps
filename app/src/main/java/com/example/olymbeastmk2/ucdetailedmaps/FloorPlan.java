package com.example.olymbeastmk2.ucdetailedmaps;

import android.graphics.drawable.VectorDrawable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import static android.R.attr.key;

/**
 * Created by Olymbeastmk2 on 18-Oct-17.
 */

public class FloorPlan
{
    public LatLng cornerLatLng;
    public double rotationDegrees;
    public String floor;
    public float scale;

    public FloorPlan( LatLng cll, double rD, String f, float s )
    {
        cornerLatLng = cll;
        rotationDegrees = rD;
        floor = f;
        scale = s;
    }
}