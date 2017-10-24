package com.example.olymbeastmk2.ucdetailedmaps;

import java.util.ArrayList;

/**
 * Created by Riley on 24/10/2017.
 */

public class Floor {

    private ArrayList<Room> rooms;
    private boolean roomsSet;

    private FloorPlan floorPlan;
    private boolean floorPlanSet;

    public boolean hasUpper;
    public boolean hasLower;

    public int key;

    public Floor(int _Key)
    {
        roomsSet = false;
        floorPlanSet = false;
        key = _Key;

        hasUpper = false;
        hasLower = false;
    }

    public Floor(ArrayList<Room> _Rooms, int _Key)
    {
        rooms = _Rooms;
        roomsSet = true;
        key = _Key;

        hasUpper = false;
        hasLower = false;
    }

    public Floor(FloorPlan _FloorPlan, int _Key)
    {
        floorPlan = _FloorPlan;
        floorPlanSet = true;
        key = _Key;

        hasUpper = false;
        hasLower = false;
    }


    public boolean hasRooms()
    {
        return roomsSet;
    }

    public boolean hasFloorPlan()
    {
        return floorPlanSet;
    }

    public void setRooms(ArrayList<Room> _Rooms)
    {
        rooms = _Rooms;
        roomsSet = true;
    }

    public void setFloorPlan(FloorPlan _FloorPlan)
    {
        floorPlan = _FloorPlan;
        floorPlanSet = true;
    }

    public FloorPlan getFloorPlan()
    {
        if(floorPlanSet)
        {
            return floorPlan;
        }
        else
        {
            return null;
        }
    }

    public ArrayList<Room> getRooms()
    {
        if(roomsSet)
        {
            return rooms;
        }
        else
        {
            return null;
        }
    }

}
