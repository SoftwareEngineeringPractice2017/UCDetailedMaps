package com.example.olymbeastmk2.ucdetailedmaps;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    private ArrayList<FloorPlan> floorPlans;
    private boolean hasFloorPlans;

    private HashMap<Integer, Floor> floors;
    private boolean hasFloors;

    public Polygon polygon;

    public boolean isFocused;

    public Building(int _id, DbHelper _parent)
    {
        id = _id;
        parent = _parent;

        hasName = false;
        hasOutline = false;
        hasEntries = false;
        hasRooms = false;
        hasFloorPlans = false;
        hasFloors = false;

        isFocused = false;
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
        res.close();
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

        res.close();

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

        res.close();

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
            output.get(floor).add(new Room(res.getInt( res.getColumnIndex( DbHelper.ROOMS_ID ) ), parent, getBuildingNumber()));

            res.moveToNext();
        }

        res.close();

        rooms = output;
        hasRooms = true;
        return output;
    }

    public HashMap<Integer, Floor> getFloors()
    {
        if(hasFloors)
        {
            return floors;
        }

        HashMap<Integer, Floor> output = new HashMap<Integer, Floor>();

        for(FloorPlan fp : getFloorPlans())
        {
            output.put(fp.floor, new Floor(fp, fp.floor));
        }

        for(int floorKey : getRooms().keySet())
        {
            if(output.containsKey(floorKey))
            {
                output.get(floorKey).setRooms(getRooms().get(floorKey));
            }
            else
            {
                output.put(floorKey, new Floor(getRooms().get(floorKey), floorKey));
            }
        }


        for(int key : output.keySet())
        {
            if(output.containsKey(key + 1))
            {
                output.get(key).hasUpper = true;
            }

            if(output.containsKey(key - 1))
            {
                output.get(key).hasLower = true;
            }
        }

        floors = output;
        hasFloors = true;
        return floors;
    }




    public ArrayList<FloorPlan> getFloorPlans()
    {
        if( hasFloorPlans )
        {
            return floorPlans;
        }

        ArrayList<FloorPlan> output = new ArrayList<FloorPlan>();

        Cursor res = parent.getReadableDatabase().rawQuery( "select * from " + DbHelper.PLANS_TABLE + " where " + DbHelper.PLANS_BUILDING_FK + "=" + Integer.toString( id ), null );
        res.moveToFirst();

        while( res.isAfterLast() == false )
        {
            int floor = res.getInt( res.getColumnIndex( DbHelper.PLANS_FLOOR ) );

            FloorPlan tmpPlan = new FloorPlan(
                    new LatLng( res.getDouble( res.getColumnIndex( DbHelper.PLANS_LAT ) ), res.getDouble( res.getColumnIndex( DbHelper.PLANS_LNG ) ) ),
                    res.getDouble( res.getColumnIndex( DbHelper.PLANS_ROT ) ),
                    res.getInt( res.getColumnIndex( DbHelper.PLANS_FLOOR ) ),
                    res.getString( res.getColumnIndex( DbHelper.PLANS_FLOOR_NAME ) ),
                    res.getFloat( res.getColumnIndex( DbHelper.PLANS_SCALE ) ) );

            output.add( tmpPlan );

            res.moveToNext();
        }

        res.close();

        floorPlans = output;
        hasFloorPlans = true;
        return output;
    }

    public void showRooms(int floor, Context context, GoogleMap map)
    {
        hideRooms();

        if(getRooms().containsKey(floor))
        {
            ArrayList<Room> rooms = getRooms().get(floor);

            for(Room r : rooms)
            {
                r.showMarker(context, map);
            }
        }
    }

    public void hideRooms()
    {
        for( int floor : getRooms().keySet() )
        {
            for(Room r : getRooms().get(floor))
            {
                r.hideMarker();
            }
        }
    }

    public Integer getBuildingNumber()
    {
        String buildingNumber = "";
        String numbers = "0123456789";
        String text = getName();

        for(char c : text.toCharArray())
        {
            String character = String.valueOf(c);
            if(numbers.contains(character))
            {
                buildingNumber += character;
            }
        }

        return Integer.parseInt(buildingNumber);
    }

    public Boolean hasBuildingNumber()
    {
        String text = getName();
        return text.contains("0") || text.contains("1") || text.contains("2") || text.contains("3")
                && text.contains("4") || text.contains("5") || text.contains("6") || text.contains("7")
                && text.contains("8") || text.contains("9");
    }

    public void hideFloorPlans()
    {
        for( FloorPlan fp : getFloorPlans() )
        {
            fp.groundMapRef.setVisible( false );
        }
    }

    public void showFloorPlans(int _Floor)
    {
        for( FloorPlan fp : getFloorPlans() )
        {
            if(fp.floor == _Floor)
            {
                fp.groundMapRef.setVisible( true );
            }
            else
            {
                fp.groundMapRef.setVisible( false );
            }
        }
    }

    public void pushOverFloorPlans()
    {
        for( FloorPlan fp : getFloorPlans() )
        {
            fp.groundMapRef.setZIndex( 2f );
        }
    }

    public void pullUnderFloorPlans()
    {
        for( FloorPlan fp : getFloorPlans() )
        {
            fp.groundMapRef.setZIndex( 0f );
        }
    }
}