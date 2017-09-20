package com.example.olymbeastmk2.ucdetailedmaps;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PatternItem;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;

/**
 * Created by Olymbeastmk2 on 09-Sep-17.
 */
public class DbHelper extends SQLiteOpenHelper
{
    public static final String BUILDING_TABLE = "buildings";
    public static final String BUILDING_ID = "buildingid";
    public static final String BUILDING_NAME = "name";

    public static final String OUTLINE_TABLE = "outlines";
    public static final String OUTLINE_ID = "outlineid";
    public static final String OUTLINE_BUILDING_ID = "buildingid";
    public static final String OUTLINE_LAT = "lat";
    public static final String OUTLINE_LNG = "lng";

    public static final String ENTRANCES_TABLE = "entrances";
    public static final String ENTRANCES_BUILDING = "buildingid";
    public static final String ENTRANCES_LAT = "lat";
    public static final String ENTRANCES_LNG = "lng";

    public static final String ICONS_TABLE = "icons";
    public static final String ICONS_ID = "id";
    public static final String ICONS_TYPE_ID = "typeid";
    public static final String ICONS_LABEL = "label";
    public static final String ICONS_LAT = "lat";
    public static final String ICONS_LNG = "lng";

    public static final String ICONTYPES_TABLE = "icontypes";
    public static final String ICONTYPES_ID = "id";
    public static final String ICONTYPES_NAME = "name";


    public DbHelper( Context context, String name, SQLiteDatabase.CursorFactory factory, int version )
    {
        super( context, name, factory, version );
    }

    @Override
    public void onCreate( SQLiteDatabase db )
    {
       BuildEverything();
    }

    @Override
    public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion )
    {
        ClearEverything();
    }

    public void ClearEverything()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL( "drop table if exists " + BUILDING_TABLE );
        db.execSQL( "drop table if exists " + OUTLINE_TABLE );
        db.execSQL( "drop table if exists " + ENTRANCES_TABLE );
        db.execSQL( "drop table if exists " + ICONS_TABLE );
        db.execSQL( "drop table if exists " + ICONTYPES_TABLE );
    }

    public void BuildEverything()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        //Create Buildings table.
        db.execSQL( "create table " + BUILDING_TABLE + "(" +
                BUILDING_ID + " integer primary key, " +
                BUILDING_NAME + " text)" );

        //Create Outlines table.
        db.execSQL( "create table " + OUTLINE_TABLE + "(" +
                OUTLINE_ID + " integer primary key autoincrement, " +
                OUTLINE_BUILDING_ID + " integer, " +
                OUTLINE_LAT + " double, " +
                OUTLINE_LNG + " double," +
                // OUTLINE_BUILDING_ID is a foreign key to say which points belong to which build
                " FOREIGN KEY (" + OUTLINE_BUILDING_ID + ") REFERENCES " + BUILDING_TABLE + "(" + BUILDING_ID + "));" );

        //Create Entries table.
        db.execSQL( "create table " + ENTRANCES_TABLE + "(" +
                ENTRANCES_BUILDING + " integer, " +
                ENTRANCES_LAT + " double, " +
                ENTRANCES_LNG + " double)");


        //Create Icons table.
        db.execSQL( "create table " + ICONS_TABLE + "(" +
                ICONS_ID + " integer primary key, " +
                ICONS_TYPE_ID + " integer, " +
                ICONS_LABEL + " text, " +
                ICONS_LAT + " double, " +
                ICONS_LNG + " double," +
                // ICONS_TYPE_ID is a foreign key to link to a location on ICONTYPES table.
                " FOREIGN KEY (" + ICONS_TYPE_ID + ") REFERENCES " + ICONTYPES_TABLE + "(" + ICONTYPES_ID + "));" );

        // Create IconTypes table.
        db.execSQL( "create table " + ICONTYPES_TABLE + "(" +
                ICONTYPES_ID + " integer primary key autoincrement, " +
                ICONTYPES_NAME + " text)");

    }

    // Removes trailing commas in a line and returns the final string
    private String RemoveTrailingCommas( String line )
    {
        while(line.endsWith(","))
        {
            line = line.substring(0, line.length() - 2);
        }

        return line;
    }

    // Imports a CSV file about building information ( Outlines and centre ) and loads it into the database
    // The second paramater is the database that the CSV file will be loaded into
    // Returns true on success
    // Returns false on failure ( Failure to load )
    public boolean ImportCSVBuildings( int rID, Resources resources )
    {
        // Get the database that we will be writing to
        SQLiteDatabase db = this.getWritableDatabase();

        // Where all that file data be coming from
        InputStream fileStream;

        // Attempt to open the file
        fileStream = resources.openRawResource( rID );

        // A Buffered reader to easily read the file
        BufferedReader buffer = new BufferedReader( new InputStreamReader( fileStream ) );

        // Begin transaction with Database so we can send them queries!
        db.beginTransaction();

        // Delete all records in the Database ( Buildings table AND Outlines Table ) to make room for the new data
        db.execSQL( "delete from " + BUILDING_TABLE );
        db.execSQL( "delete from " + OUTLINE_TABLE );
        db.execSQL( "delete from " + ENTRANCES_TABLE );
        db.execSQL( "delete from " + ICONS_TABLE );
        db.execSQL( "delete from " + ICONTYPES_TABLE );

        try
        {
            // Boolean that indicates whether the '###' denoting string has been hit or not
            boolean tripleHashHit = false;
            boolean tripleMoneyHit = false;

            // 'Read in' one line to skip it as the first line is just titles for the fields
            buffer.readLine();

            // Loop through the CSV file, reading in each line one by one
            String line = "";
            while( ( line = buffer.readLine() ) != null )
            {
                // This will check for denoting string '###', which indicates that Icon coordinates will be read in
                if( line.startsWith( "###" ) )
                {
                    // Flip associated boolean to indicate that we have passed '###'
                    tripleHashHit = true;

                    // Break the loop as we are done loading in building coordinates
                    break;
                }

                //Remove trailing commas
                line = RemoveTrailingCommas( line );

                Log.d( "UCDetailedMaps", line );

                // Temporary string array to hold each seperated element from the line
                String[] tmpString = line.split( "," );

                // This is the query to send to the building table to create a new building record
                // The building number will be the building's ID
                // tmpString[1] is where the building's ID is ( Second field )
                // Currently there is no name implemented in the CSV file

                /* OLD CODE
                String buildingQuery = "INSERT INTO " + BUILDING_TABLE + " VALUES( \"" + tmpString[0] + "\", 'NO NAME'" + ");";
                db.execSQL( buildingQuery );
                */

                ContentValues contentValues = new ContentValues();
                contentValues.put(BUILDING_NAME, tmpString[0]);
                int rowID = (int) db.insert(BUILDING_TABLE, null, contentValues);

                // Get number of elements in the tmpString array to know how many points will be added to the outlines table
                int pointNum = tmpString.length;

                // The tmpString length should be an even number, if not, there is a problem!
                if( pointNum % 2 == 1 )
                {
                    // Throw error message and return false
                    Log.e( "UCDetailedMapsApp", "pointNum is not even!" );
                    return false;
                }

                // Loop through each point, adding it to the outline table
                // i starts at 4, as that is where the points start ( and after the center point
                for( int i = 4; i < pointNum; i += 2 )
                {
                    // Build the query to add this record information to the outlines table
                    // Note that the OUTLINE_ID is not needed as it is auto incremented
                    // tmpString[ i ] is the latitude, tmpString[ i + 1 ] is the longitude

                    /* OLD CODE
                    String outlinePointQuery = "INSERT INTO " + OUTLINE_TABLE + "( " +
                            OUTLINE_BUILDING_ID + ", " + OUTLINE_LAT + ", " + OUTLINE_LNG + ") " +
                            "VALUES( " + tmpString[1] + ", " + tmpString[ i ] + ", " + tmpString[ i + 1 ] + ");";
                    */

                    ContentValues contentValues1 = new ContentValues();
                    contentValues1.put(OUTLINE_BUILDING_ID, Integer.toString(rowID));
                    contentValues1.put(OUTLINE_LAT, tmpString[i]);
                    contentValues1.put(OUTLINE_LNG, tmpString[i + 1]);
                    db.insert(OUTLINE_TABLE, null, contentValues1);

                    // Submit the query to add a new point for that building!
                    //db.execSQL( outlinePointQuery );
                }
            }

            // Check to see if a '###' has been found, if not, something is wrong
            if( !tripleHashHit )
            {
                Log.e( "UCDetailedMaps", "### was NOT found after buildings, aborting loading process");
                return false;
            }

            Log.d( "UCDetailedMaps", "NOW LOADING ICON TYPES" );

            // Now start reading in Icon Types
            while( ( line = buffer.readLine() ) != null )
            {
                // This will check for denoting string '###', which indicates that Icon coordinates will be read in
                if( line.startsWith( "$$$" ) )
                {
                    // Flip associated boolean to indicate that we have passed '###'
                    tripleMoneyHit = true;

                    // Break the loop as we are done loading in building coordinates
                    break;
                }

                // Remove Trailing Commas
                line = RemoveTrailingCommas( line );

                Log.d( "UCDetailedMaps", line );

                // Temp string to hold seperated elements
                String[] tmpString = line.split( "," );

                // Create a contentValue to push to the Icon table in the database
                ContentValues contentValues = new ContentValues();

                // Read in ID and associated icon label
                contentValues.put( ICONS_TYPE_ID, tmpString[0] );
                contentValues.put( ICONS_LABEL, tmpString[1] );

                // PUSH IT
                db.insert(ICONTYPES_TABLE, null, contentValues);
            }

            // Check to see if a '$$$' has been found, if not, something is wrong
            if( !tripleHashHit )
            {
                Log.e( "UCDetailedMaps", "$$$ was NOT found after icon types, aborting loading process");
                return false;
            }

            Log.d( "UCDetailedMaps", "NOW LOADING ICONS" );

            // Now start reading in the Icons themselves
            while( ( line = buffer.readLine() ) != null )
            {
                // Remove Trailing Commas
                line = RemoveTrailingCommas( line );

                Log.d( "UCDetailedMaps", line );

                // Temp string to hold seperated elements
                String[] tmpString = line.split( "," );

                // Create a contentValue to push to the Icon table in the database
                ContentValues contentValues = new ContentValues();

                // Read in ID and associated icon label
                contentValues.put( ICONS_TYPE_ID, tmpString[1] );
                contentValues.put( ICONS_LABEL, tmpString[0] );
                contentValues.put( ICONS_LAT, tmpString[2] );
                contentValues.put( ICONS_LNG, tmpString[3] );

                db.insert(BUILDING_TABLE, null, contentValues);
            }
        }
        catch( IOException exception )
        {
            // IOException! Print an error and Return false
            Log.e( "UCDetailedMapsApp", "An IO Exception was thrown while reading CSV file ");
            return false;
        }

        // Everything succeeded! Return true
        return true;
    }

    public ArrayList<Building> GetBuildings()
    {
        ArrayList<Building> output = new ArrayList<Building>();

        Cursor res = getReadableDatabase().rawQuery("select * from " + BUILDING_TABLE, null);
        res.moveToFirst();

        while(res.isAfterLast() == false)
        {
            //Log.e( "UCDetailedMapsApp", res.getString(0) );
            output.add(new Building( res.getInt( res.getColumnIndex( BUILDING_ID ) ), this));
            res.moveToNext();
        }

        return output;
    }

    public HashMap<String, ArrayList<Icon>> GetIcons()
    {
        HashMap<String, ArrayList<Icon>> output = new HashMap<String, ArrayList<Icon>>();

        Cursor res = getReadableDatabase().rawQuery("select * from " + ICONS_TABLE, null);
        res.moveToFirst();

        while(res.isAfterLast() == false)
        {
            Icon tempIcon = new Icon(res.getInt( res.getColumnIndex( ICONS_ID )), this);
            String iconType = tempIcon.getType();

            if(output.containsKey(iconType))
            {
                output.get(iconType).add(tempIcon);
            }
            else
            {
                output.put(iconType, new ArrayList<Icon>());
                output.get(iconType).add(tempIcon);
            }

            res.moveToNext();
        }

        return output;
    }
}
