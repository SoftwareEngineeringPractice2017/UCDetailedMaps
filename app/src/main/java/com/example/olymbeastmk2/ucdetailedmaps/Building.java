package com.example.olymbeastmk2.ucdetailedmaps;

import android.database.Cursor;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;

import java.util.ArrayList;
import java.util.HashMap;

// HELLO RILEY, ITS GREG HERE. THE CODE IS 'PINEAPPLE' :P

/**
 * Created by Olymbeastmk2 on 09-Sep-17.
 */
public class Building
{
    // The absolute maximum buildings that there can be in UC
    public static final int MAX_BUILDINGS = 30;

    private int id;
    private DbHelper parent;

    private String name;
    private boolean hasName;

    private ArrayList<LatLng> outline;
    private boolean hasOutline;

    private ArrayList<LatLng> entries;
    private boolean hasEntries;

    private HashMap<Integer, ArrayList<Room>> rooms;
    private boolean hasRooms;

    public Polygon polygon;


    public Building(int _id, DbHelper _parent)
    {
        id = _id;
        parent = _parent;

        hasName = false;
        hasOutline = false;
        hasEntries = false;
    }

    public void Load()
    {
        getName();
        getOutline();
        getEntries();
        getRooms();
    }

    public int getID()
    {
        //will be saved locally.
        return id;
    }

    public String getName()
    {
        if(hasName)
        {
            return name;
        }
        Cursor res = parent.getReadableDatabase().rawQuery("select * from " + DbHelper.BUILDING_TABLE + " where " + DbHelper.BUILDING_ID + "=" + Integer.toString( id ), null);
        res.moveToFirst();
        name = res.getString( res.getColumnIndex( DbHelper.BUILDING_NAME ) );
        hasName = true;
        return name;
    }

    public ArrayList<LatLng> getOutline()
    {
        if(hasOutline)
        {
            return outline;
        }

        Cursor res = parent.getReadableDatabase().rawQuery("select * from " + DbHelper.OUTLINE_TABLE + " where " + DbHelper.OUTLINE_BUILDING_ID + "=" + Integer.toString( id ), null );
        res.moveToFirst();

        outline = new ArrayList<LatLng>();
        while(res.isAfterLast() == false)
        {
            double latitude, longitude;
            latitude = res.getDouble( res.getColumnIndex( DbHelper.OUTLINE_LAT ) );
            longitude = res.getDouble( res.getColumnIndex( DbHelper.OUTLINE_LNG ) );
            outline.add(new LatLng( latitude, longitude ));
            res.moveToNext();
        }

        hasOutline = true;
        return outline;
    }

    public ArrayList<LatLng> getEntries()
    {
        if(hasEntries)
        {
            return entries;
        }
        Cursor res = parent.getReadableDatabase().rawQuery("select * from " + DbHelper.ENTRANCES_TABLE + " where " + DbHelper.ENTRANCES_BUILDING + "=" + Integer.toString( id ), null );
        res.moveToFirst();

        entries = new ArrayList<LatLng>();
        while(res.isAfterLast() == false)
        {
            double latitude, longitude;
            latitude = res.getDouble( res.getColumnIndex( DbHelper.ENTRANCES_LAT ) );
            longitude = res.getDouble( res.getColumnIndex( DbHelper.ENTRANCES_LNG ) );
            entries.add(new LatLng( latitude, longitude ));
            res.moveToNext();
        }

        hasEntries = true;
        return entries;
    }

    public HashMap<Integer, ArrayList<Room>> getRooms()
    {
        if(hasRooms)
        {
            return rooms;
        }
        HashMap<Integer, ArrayList<Room>> output = new HashMap<Integer, ArrayList<Room>>();


        Cursor res = parent.getReadableDatabase().rawQuery("select * from " + DbHelper.ROOMS_TABLE + " where " + DbHelper.ROOMS_BUILDING + "=" + Integer.toString( id ), null);
        res.moveToFirst();

        while(res.isAfterLast() == false)
        {
            int floor = res.getInt( res.getColumnIndex( DbHelper.ROOMS_FLOOR ) );
            if(!output.containsKey(floor))
            {
                output.put(floor, new ArrayList<Room>());
            }
            output.get(floor).add(new Room(res.getInt( res.getColumnIndex( DbHelper.ROOMS_ID ) ), parent));

            res.moveToNext();
        }

        return output;
    }

}