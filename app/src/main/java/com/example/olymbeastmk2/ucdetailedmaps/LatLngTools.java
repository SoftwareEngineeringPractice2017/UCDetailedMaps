package com.example.olymbeastmk2.ucdetailedmaps;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Riley on 20/09/2017.
 */

public class LatLngTools
{
    // Radius for showing Buildings
    final static double B_SHOW_RADIUS_VAL = 0.0001;

    //Average the points together.
    public static LatLng getCenter(ArrayList<LatLng> polygon)
    {
        double latitude = 0;
        double longitude = 0;

        for(LatLng point : polygon)
        {
            latitude += point.latitude;
            longitude += point.longitude;
        }

        latitude /= polygon.size();
        longitude /= polygon.size();

        return new LatLng(latitude, longitude);
    }

    //Pythagorean triangle.
    public static double getDistance(LatLng pointA, LatLng pointB)
    {
        double a = Math.abs(pointA.latitude - pointB.latitude);
        double b = Math.abs(pointA.longitude - pointB.longitude);

        return Math.sqrt((a * a) + (b * b));
    }

    public static Icon findClosestIcon(LatLng location, ArrayList<Icon> icons)
    {
        Icon closest = icons.get(0);
        double distance = getDistance(location, closest.getLocation());

        for(Icon i : icons)
        {
            double newDistance = getDistance(location, i.getLocation());
            if(newDistance < distance)
            {
                closest = i;
                distance = newDistance;
            }
        }

        return closest;
    }

    public static Building findClosestBuilding(LatLng location, ArrayList<Building> buildings)
    {
        Building closest = buildings.get(0);
        double distance = getDistance(location, getCenter(closest.getOutline()));

        for(Building b : buildings)
        {
            double newDistance = getDistance(location, getCenter(b.getOutline()));
            if(newDistance < distance)
            {
                closest = b;
                distance = newDistance;
            }
        }

        return closest;
    }

    public static ArrayList<Building> findClosestBuildings( LatLng location, ArrayList<Building> buildings )
    {
        ArrayList<Building> retBuildArr = new ArrayList<Building>();

        for( Building b : buildings )
        {
            double curDist = getDistance( location, getCenter( b.getOutline() ) );
            if( curDist < B_SHOW_RADIUS_VAL )
            {
                retBuildArr.add( b );
            }
        }

        return retBuildArr;
    }
}
