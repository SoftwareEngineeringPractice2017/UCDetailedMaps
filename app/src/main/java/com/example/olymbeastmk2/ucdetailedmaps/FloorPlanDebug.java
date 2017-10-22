package com.example.olymbeastmk2.ucdetailedmaps;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Olymbeastmk2 on 22-Oct-17.
 */

public class FloorPlanDebug
{
    private FloatingActionButton FPFAB;
    private FloatingActionButton FPDisableFAB;
    private FloatingActionButton FPDownFAB;
    private FloatingActionButton FPLeftFAB;
    private FloatingActionButton FPRightFAB;
    private FloatingActionButton FPUpFAB;
    private FloatingActionButton FPSelectFAB;
    private FloatingActionButton FPSaveClipFAB;
    private FloatingActionButton FPScaleIncreaseFAB;
    private FloatingActionButton FPScaleDecreaseFAB;
    private FloatingActionButton debugFAB;
    private TextView FPBuildingFloorText;
    private TextView FPScaleText;

    // An Iterator will be used to loop through this set
    Iterator<Building> bIt = null;
    Iterator<FloorPlan> fIt = null;

    // Current Floor Plan Array num and the temp array
    FloorPlan curFloorPlan = null;
    Building curBuilding = null;

    // Move constant
    final double FP_MOVE_DIST = 0.000001;
    final float FP_SCALE_VAL = 0.5f;

    // Bools
    boolean floorPlansExist = true;

    public FloorPlanDebug( final Activity MapsActivity, final Context context, final ArrayList<Building> buildings )
    {
        FPFAB = ( FloatingActionButton ) MapsActivity.findViewById( R.id.FPFAB );
        FPDisableFAB = ( FloatingActionButton ) MapsActivity.findViewById( R.id.FPDisableFAB );
        FPDownFAB = ( FloatingActionButton ) MapsActivity.findViewById( R.id.FPDownFAB );
        FPLeftFAB = ( FloatingActionButton ) MapsActivity.findViewById( R.id.FPLeftFAB );
        FPRightFAB = ( FloatingActionButton ) MapsActivity.findViewById( R.id.FPRightFAB );
        FPUpFAB = ( FloatingActionButton ) MapsActivity.findViewById( R.id.FPUpFAB );
        FPSelectFAB = ( FloatingActionButton ) MapsActivity.findViewById( R.id.FPSelectFAB );
        FPSaveClipFAB = ( FloatingActionButton ) MapsActivity.findViewById( R.id.FPSaveClipFAB );
        FPScaleIncreaseFAB = ( FloatingActionButton ) MapsActivity.findViewById( R.id.FPScaleIncreaseFAB );
        FPScaleDecreaseFAB = ( FloatingActionButton ) MapsActivity.findViewById( R.id.FPScaleDecreaseFAB );

        FPBuildingFloorText = ( TextView ) MapsActivity.findViewById( R.id.FPBuildingFloorText );
        FPScaleText = ( TextView ) MapsActivity.findViewById( R.id.FPScaleText );

        debugFAB = ( FloatingActionButton ) MapsActivity.findViewById( R.id.debugFAB );

        bIt = buildings.iterator();
        curBuilding = bIt.next();

        while( curBuilding.getFloorPlans().isEmpty() )
        {
            if( bIt.hasNext() )
            {
                curBuilding = bIt.next();
            }
            else
            {
                Log.e( "UCDetailedMaps", "There are no Floor Plans :(" );
                floorPlansExist = false;
            }
        }

        if( floorPlansExist )
        {
            fIt = curBuilding.getFloorPlans().iterator();
            curFloorPlan = fIt.next();
        }

        // Set this button to enable Floor Plans Debug Mode
        FPFAB.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                FPFAB.setVisibility( FloatingActionButton.INVISIBLE );
                debugFAB.setVisibility( FloatingActionButton.INVISIBLE );

                FPDisableFAB.setVisibility( FloatingActionButton.VISIBLE );
                FPDownFAB.setVisibility( FloatingActionButton.VISIBLE );
                FPUpFAB.setVisibility( FloatingActionButton.VISIBLE );
                FPLeftFAB.setVisibility( FloatingActionButton.VISIBLE );
                FPRightFAB.setVisibility( FloatingActionButton.VISIBLE );
                FPSelectFAB.setVisibility( FloatingActionButton.VISIBLE );
                FPSaveClipFAB.setVisibility( FloatingActionButton.VISIBLE );
                FPScaleDecreaseFAB.setVisibility( FloatingActionButton.VISIBLE );
                FPScaleIncreaseFAB.setVisibility( FloatingActionButton.VISIBLE );

                FPBuildingFloorText.setVisibility( TextView.VISIBLE );
                FPScaleText.setVisibility( TextView.VISIBLE );

                // Announce!
                Toast.makeText( context, "Floor Plan Debug Mode Activated", Toast.LENGTH_SHORT ).show();
            }
        } );

        // Set this button to disable Floor Plans Debug Mode
        FPDisableFAB.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                FPFAB.setVisibility( FloatingActionButton.VISIBLE );
                debugFAB.setVisibility( FloatingActionButton.VISIBLE );

                FPDisableFAB.setVisibility( FloatingActionButton.INVISIBLE );
                FPDownFAB.setVisibility( FloatingActionButton.INVISIBLE );
                FPUpFAB.setVisibility( FloatingActionButton.INVISIBLE );
                FPLeftFAB.setVisibility( FloatingActionButton.INVISIBLE );
                FPRightFAB.setVisibility( FloatingActionButton.INVISIBLE );
                FPSelectFAB.setVisibility( FloatingActionButton.INVISIBLE );
                FPSaveClipFAB.setVisibility( FloatingActionButton.INVISIBLE );
                FPScaleDecreaseFAB.setVisibility( FloatingActionButton.INVISIBLE );
                FPScaleIncreaseFAB.setVisibility( FloatingActionButton.INVISIBLE );

                FPBuildingFloorText.setVisibility( TextView.INVISIBLE );
                FPScaleText.setVisibility( TextView.INVISIBLE );

                // Announce!
                Toast.makeText( context, "Floor Plan Debug Mode Disabled", Toast.LENGTH_SHORT ).show();
            }
        } );

        // Set this button to select the next floor Plan
        FPSelectFAB.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {

                if( floorPlansExist )
                {
                    if( fIt.hasNext() )
                    {
                        curFloorPlan = fIt.next();
                    }
                    else if( bIt.hasNext() )
                    {
                        curBuilding = bIt.next();

                        while( curBuilding.getFloorPlans().isEmpty() )
                        {
                            if( bIt.hasNext() )
                            {
                                curBuilding = bIt.next();
                            }
                            else
                            {
                                bIt = buildings.iterator();
                                curBuilding = bIt.next();
                            }
                        }

                        fIt = curBuilding.getFloorPlans().iterator();
                        curFloorPlan = fIt.next();
                    }
                    else
                    {
                        bIt = buildings.iterator();
                        curBuilding = bIt.next();
                        fIt = curBuilding.getFloorPlans().iterator();
                        curFloorPlan = fIt.next();
                    }

                    FPScaleText.setText( Float.toString( curFloorPlan.scale ) );
                    FPBuildingFloorText.setText( curBuilding.getName() + curFloorPlan.floorName );
                }
                else
                {
                    Toast.makeText( context, "No Floor plans exist :(", Toast.LENGTH_SHORT ).show();
                }
            }
        } );

        // Set this button to move the Floor plan 'Up' ( Lng- )
        FPUpFAB.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                if( curFloorPlan == null )
                {
                    Toast.makeText( context, "No Floor Plan Selected", Toast.LENGTH_LONG ).show();
                }
                else
                {
                    GroundOverlay gRef = curFloorPlan.groundMapRef;
                    LatLng curPos = gRef.getPosition();

                    // Move it!
                    gRef.setPosition( new LatLng( curPos.latitude, curPos.longitude - FP_MOVE_DIST ) );
                }
            }
        } );

        // Set this button to move the Floor plan 'Down' ( Lng+ )
        FPDownFAB.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                if( curFloorPlan == null )
                {
                    Toast.makeText( context, "No Floor Plan Selected", Toast.LENGTH_LONG ).show();
                }
                else
                {
                    GroundOverlay gRef = curFloorPlan.groundMapRef;
                    LatLng curPos = gRef.getPosition();

                    // Move it!
                    gRef.setPosition( new LatLng( curPos.latitude, curPos.longitude + FP_MOVE_DIST ) );
                }
            }
        } );

        // Set this button to move the Floor plan 'Left' ( Lat- )
        FPLeftFAB.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                if( curFloorPlan == null )
                {
                    Toast.makeText( context, "No Floor Plan Selected", Toast.LENGTH_LONG ).show();
                }
                else
                {
                    GroundOverlay gRef = curFloorPlan.groundMapRef;
                    LatLng curPos = gRef.getPosition();

                    // Move it!
                    gRef.setPosition( new LatLng( curPos.latitude - FP_MOVE_DIST, curPos.longitude ) );
                }
            }
        } );

        // Set this button to move the Floor plan 'Right' ( Lat+ )
        FPRightFAB.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                if( curFloorPlan == null )
                {
                    Toast.makeText( context, "No Floor Plan Selected", Toast.LENGTH_LONG ).show();
                }
                else
                {
                    GroundOverlay gRef = curFloorPlan.groundMapRef;
                    LatLng curPos = gRef.getPosition();

                    // Move it!
                    gRef.setPosition( new LatLng( curPos.latitude + FP_MOVE_DIST, curPos.longitude ) );
                }
            }
        } );

        // Set this button to increase scale for the current Floor Plan
        FPScaleDecreaseFAB.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                if( curFloorPlan == null )
                {
                    Toast.makeText( context, "No Floor Plan Selected", Toast.LENGTH_LONG ).show();
                }
                else
                {
                    GroundOverlay gRef = curFloorPlan.groundMapRef;

                    gRef.setDimensions( gRef.getWidth() - FP_SCALE_VAL );

                    FPScaleText.setText( Float.toString( gRef.getWidth() ) );
                }
            }
        } );

        // Set this button to decrease scale for the current Floor Plan
        FPScaleIncreaseFAB.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                if( curFloorPlan == null )
                {
                    Toast.makeText( context, "No Floor Plan Selected", Toast.LENGTH_LONG ).show();
                }
                else
                {
                    GroundOverlay gRef = curFloorPlan.groundMapRef;

                    gRef.setDimensions( gRef.getWidth() + FP_SCALE_VAL );

                    FPScaleText.setText( Float.toString( gRef.getWidth() ) );
                }
            }
        } );


        // Set this button to Save the LatLng and Scale to Clipboard
        FPSaveClipFAB.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                if( curFloorPlan == null )
                {
                    Toast.makeText( context, "No Floor Plan Selected", Toast.LENGTH_LONG ).show();
                }
                else
                {
                    GroundOverlay gRef = curFloorPlan.groundMapRef;
                    LatLng gLatLng = gRef.getPosition();

                    StringBuilder stringToCopy = new StringBuilder( "" );
                    stringToCopy.append( FPBuildingFloorText.getText().toString() );
                    stringToCopy.append( ", " );
                    stringToCopy.append( gLatLng.latitude );
                    stringToCopy.append( ", " );
                    stringToCopy.append( gLatLng.longitude );
                    stringToCopy.append( ", " );
                    stringToCopy.append( gRef.getWidth() );

                    // Got info from here: https://stackoverflow.com/questions/19253786/how-to-copy-text-to-clip-board-in-android
                    // This will copy the Location information and scale information of the floor plans to the ClipBoard
                    ClipboardManager cb = ( ClipboardManager ) MapsActivity.getSystemService( Context.CLIPBOARD_SERVICE );
                    ClipData clip = ClipData.newPlainText( "Location information", stringToCopy.toString() );
                    cb.setPrimaryClip( clip );

                    Toast.makeText( context, "FP info saved to Clipboard!", Toast.LENGTH_SHORT ).show();
                }


            }
        } );
    }
}
