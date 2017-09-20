package com.example.olymbeastmk2.ucdetailedmaps;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Riley on 20/09/2017.
 */

public class LatLngTools {


    //Average the points together.
    public  static LatLng GetCenter(ArrayList<LatLng> polygon)
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
    public static double GetDistance(LatLng pointA, LatLng pointB)
    {
        double a2 = pointA.latitude * pointA.latitude;
        double b2 = pointB.longitude * pointB.longitude;

        return Math.abs(Math.sqrt(a2 + b2));
    }








}
