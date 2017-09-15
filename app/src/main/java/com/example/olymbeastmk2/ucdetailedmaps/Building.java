package com.example.olymbeastmk2.ucdetailedmaps;

import android.database.Cursor;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


/**
 * Created by Olymbeastmk2 on 09-Sep-17.
 */
public class Building
{
    // The absolute maximum buildings that there can be in UC
    public static final int MAX_BUILDINGS = 30;

    private int id;
    private DbHelper parent;

    public Building(int _id, DbHelper _parent)
    {
        id = _id;
        parent = _parent;
    }

    public int getID()
    {
        //will be saved locally.
        return id;
    }

    public String getName()
    {
        Cursor res = parent.getReadableDatabase().rawQuery("select * from " + DbHelper.BUILDING_TABLE + " where " + DbHelper.BUILDING_ID + "=" + Integer.toString( id ), null);
        res.moveToFirst();
        return res.getString( res.getColumnIndex( DbHelper.BUILDING_NAME ) );
    }

    public ArrayList<LatLng> getOutline()
    {
        Cursor res = parent.getReadableDatabase().rawQuery("select * from " + DbHelper.OUTLINE_TABLE + " where " + DbHelper.OUTLINE_BUILDING_ID + "=" + Integer.toString( id ), null );
        res.moveToFirst();

        ArrayList<LatLng> outline = new ArrayList<LatLng>();
        while(res.isAfterLast() == false)
        {
            double latitude, longitude;
            latitude = res.getDouble( res.getColumnIndex( DbHelper.OUTLINE_LAT ) );
            longitude = res.getDouble( res.getColumnIndex( DbHelper.OUTLINE_LNG ) );
            outline.add(new LatLng( latitude, longitude ));
            res.moveToNext();
        }
        return outline;
    }

    public ArrayList<LatLng> getEntries()
    {
        Cursor res = parent.getReadableDatabase().rawQuery("select * from " + DbHelper.ENTRANCES_TABLE + " where " + DbHelper.ENTRANCES_BUILDING + "=" + Integer.toString( id ), null );
        res.moveToFirst();

        ArrayList<LatLng> entries = new ArrayList<LatLng>();
        while(res.isAfterLast() == false)
        {
            double latitude, longitude;
            latitude = res.getDouble( res.getColumnIndex( DbHelper.ENTRANCES_LAT ) );
            longitude = res.getDouble( res.getColumnIndex( DbHelper.ENTRANCES_LNG ) );
            entries.add(new LatLng( latitude, longitude ));
            res.moveToNext();
        }
        return entries;
    }
}