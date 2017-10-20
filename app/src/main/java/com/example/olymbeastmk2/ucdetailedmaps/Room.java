package com.example.olymbeastmk2.ucdetailedmaps;

import android.database.Cursor;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Riley on 20/10/2017.
 */

public class Room {

    private int id;
    private DbHelper parent;

    private int buildingID;
    private boolean hasBuildingID;

    private int floor;
    private boolean hasFloor;

    private LatLng location;
    private boolean hasLocation;

    private String title;
    private boolean hasTitle;

//    public static final String ROOMS_TABLE = "rooms";
//    public static final String ROOMS_ID = "id";
//    public static final String ROOMS_BUILDING = "buildingid";
//    public static final String ROOMS_FLOOR = "floor";
//    public static final String ROOMS_LAT = "lat";
//    public static final String ROOMS_LNG = "lng";
//    public static final String ROOMS_TITLE = "title";

    public Room(int _ID, DbHelper _Parent)
    {
        id = _ID;
        parent = _Parent;
    }

    public void Load()
    {
        getBuildingID();
        getFloor();
        getLocation();
        getTitle();
    }

    public int getBuildingID()
    {
        if(hasBuildingID)
        {
            return buildingID;
        }
        Cursor res = parent.getReadableDatabase().rawQuery("select * from " + DbHelper.ROOMS_TABLE + " where " + DbHelper.ROOMS_ID + "=" + Integer.toString( id ), null);
        res.moveToFirst();
        buildingID = res.getInt( res.getColumnIndex( DbHelper.BUILDING_NAME ) );
        hasBuildingID = true;
        return buildingID;
    }

    public int getFloor()
    {
        if(hasFloor)
        {
            return floor;
        }
        Cursor res = parent.getReadableDatabase().rawQuery("select * from " + DbHelper.ROOMS_TABLE + " where " + DbHelper.ROOMS_ID + "=" + Integer.toString( id ), null);
        res.moveToFirst();
        floor = res.getInt( res.getColumnIndex( DbHelper.ROOMS_FLOOR ) );
        hasFloor = true;
        return floor;
    }

    public LatLng getLocation()
    {
        if(hasLocation)
        {
            return location;
        }

        Cursor res = parent.getReadableDatabase().rawQuery("select * from " + DbHelper.ROOMS_TABLE + " where " + DbHelper.ROOMS_ID + "=" + Integer.toString( id ), null);
        res.moveToFirst();
        double latitude = res.getDouble( res.getColumnIndex( DbHelper.ROOMS_LAT ) );
        double longitude = res.getDouble( res.getColumnIndex( DbHelper.ROOMS_LNG ) );

        location = new LatLng(latitude, longitude);
        hasLocation = true;
        return location;
    }

    public String getTitle()
    {
        if(hasTitle)
        {
            return title;
        }
        Cursor res = parent.getReadableDatabase().rawQuery("select * from " + DbHelper.ROOMS_TABLE + " where " + DbHelper.ROOMS_ID + "=" + Integer.toString( id ), null);
        res.moveToFirst();
        title = res.getString( res.getColumnIndex( DbHelper.BUILDING_NAME ) );
        hasTitle = true;
        return title;
    }








}
