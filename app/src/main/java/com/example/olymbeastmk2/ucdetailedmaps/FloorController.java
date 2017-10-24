package com.example.olymbeastmk2.ucdetailedmaps;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Riley on 24/10/2017.
 */

public class FloorController {

    public int floor;

    public TextView floorDisplay;
    public Button upButton;
    public Button downButton;

    private Building focus;

    private Activity activity;

    private MapsActivity mapsActivity;

    private Context context;
    private GoogleMap map;

    public FloorController(final Activity MapsActivity, MapsActivity _MapsActivity, Context _Context, GoogleMap _Map)
    {
        floor = 0;
        floorDisplay = (TextView) MapsActivity.findViewById( R.id.floordisp) ;
        upButton = (Button) MapsActivity.findViewById(R.id.upbutton) ;
        downButton = (Button) MapsActivity.findViewById(R.id.downbutton);

        activity = MapsActivity;
        mapsActivity = _MapsActivity;
        context = _Context;
        map = _Map;

        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goUp(focus);
            }
        });

        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goDown(focus);
            }
        });

        hide();
    }

    public void show(Building _Building)
    {
        floorDisplay.setVisibility(TextView.VISIBLE);
        upButton.setVisibility(Button.VISIBLE);
        downButton.setVisibility(Button.VISIBLE);

        focus = _Building;
        HashMap<Integer, Floor> floors = _Building.getFloors();
        Floor focusedFloor = getAppropriateFloor(floors);

        if(focusedFloor == null)
        {
            floorDisplay.setText("No floors.");
            upButton.setVisibility(Button.INVISIBLE);
            downButton.setVisibility(Button.INVISIBLE);
            return;
        }

        if(focusedFloor.hasUpper)
        {
            upButton.setVisibility(Button.VISIBLE);
        }
        else
        {
            upButton.setVisibility(Button.INVISIBLE);
        }

        if(focusedFloor.hasLower)
        {
            downButton.setVisibility(Button.VISIBLE);
        }
        else
        {
            downButton.setVisibility(Button.INVISIBLE);
        }

        floorDisplay.setText("Floor " + focusedFloor.getFloorPlan().floorName);

        _Building.showFloorPlans(focusedFloor.key);
        _Building.showRooms(focusedFloor.key, context, map);
    }

    private Floor getAppropriateFloor(HashMap<Integer, Floor> floors)
    {
        if(floors.isEmpty())
        {
            return null;
        }

        int highestFloor = -50;
        int lowestFloor = 50;

        int closest = 50;

        for(int floorKey : floors.keySet())
        {
            if(highestFloor < floorKey)
            {
                highestFloor = floorKey;
            }
            if(lowestFloor > floorKey)
            {
                lowestFloor = floorKey;
            }
            if(Math.abs(floorKey - closest) < Math.abs(floor - closest))
            {
                closest = floorKey;
            }
        }

        if(floors.containsKey(floor))
        {
            return floors.get(floor);
        }

        if(floor < lowestFloor)
        {
            return floors.get(lowestFloor);
        }

        if(floor > highestFloor)
        {
            return floors.get(highestFloor);
        }

        if(closest != 50)
        {
            return floors.get(closest);
        }
        else
        {
            return null;
        }
    }

    public void hide()
    {
        floorDisplay.setVisibility(TextView.INVISIBLE);
        upButton.setVisibility(Button.INVISIBLE);
        downButton.setVisibility(Button.INVISIBLE);
    }

    public void goUp(Building _Building)
    {
        int oldFloor = floor;
        for(FloorPlan fp : _Building.getFloorPlans())
        {
            if(fp.floor == floor + 1)
            {
                floor = fp.floor;
            }
        }

        if(floor == oldFloor)
        {
            Toast toast = Toast.makeText(activity.getApplicationContext(), "Cannot go up.", Toast.LENGTH_SHORT);
            toast.show();
        }

        show(_Building);

    }

    public void goDown(Building _Building)
    {
        int oldFloor = floor;
        for(FloorPlan fp : _Building.getFloorPlans())
        {
            if(fp.floor == floor - 1)
            {
                floor = fp.floor;
            }
        }
        if(floor == oldFloor)
        {
            Toast toast = Toast.makeText(activity.getApplicationContext(), "Cannot go down.", Toast.LENGTH_SHORT);
            toast.show();
        }

        show(_Building);
    }

    public void showFloor(Building _Building, int _Floor)
    {
        ArrayList<FloorPlan> floorPlanArrayList = _Building.getFloorPlans();

        int floorToShow = closestFloor(_Building);


        for (FloorPlan fp : floorPlanArrayList)
        {
            if(fp.floor == floorToShow)
            {
                fp.groundMapRef.setVisible(true);
            }
            else
            {
                fp.groundMapRef.setVisible(false);
            }
        }
    }

    public int closestFloor(Building _Building)
    {
        ArrayList<FloorPlan> floorPlanArrayList = _Building.getFloorPlans();

        if(floorPlanArrayList.isEmpty())
        {
            return 0;
        }

        int topFloor = floorPlanArrayList.get(0).floor;
        int bottomFloor = floorPlanArrayList.get(0).floor;

        for(FloorPlan fp : floorPlanArrayList)
        {
            if(topFloor > fp.floor)
            {
                topFloor = fp.floor;
            }

            if(bottomFloor < fp.floor)
            {
                bottomFloor = fp.floor;
            }
        }

        if(floor < bottomFloor)
        {
            return bottomFloor;
        }

        if(floor > topFloor)
        {
            return topFloor;
        }

        return floor;
    }

    public String floorString(int _Floor)
    {
        switch (_Floor)
        {
            case -1 :
                return "Basement";
            case 0 :
                return "Ground Floor";
            case 1 :
                return "First Floor";
            case 2 :
                return "Second Floor";
            case 3 :
                return "Third Floor";
            case 4 :
                return "Fourth Floor";
            case 5 :
                return "Fifth Floor";
            case 6 :
                return "Sixth Floor";
            case 7 :
                return "Seventh Floor";
            case 8 :
                return "Eighth Floor";
            case 9 :
                return "Ninth Floor";
            default:
                return "Error Floor";
        }
    }

}
