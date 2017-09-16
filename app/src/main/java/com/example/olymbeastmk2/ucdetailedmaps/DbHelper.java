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
    public static final String ICONS_LAT = "lat";
    public static final String ICONS_LNG = "lng";

    public static final String ICON_TYPE_TABLE = "icontypes";
    public static final String ICON_LABEL = "iconlabel";


    public DbHelper( Context context, String name, SQLiteDatabase.CursorFactory factory, int version )
    {
        super( context, name, factory, version );
    }

    @Override
    public void onCreate( SQLiteDatabase db )
    {
        //Create Buildings table.
        db.execSQL( "create table " + BUILDING_TABLE + "(" +
                BUILDING_ID + " integer primary key, " +
                BUILDING_NAME + "text)" );

        //Create Outlines table.
        db.execSQL( "create table " + OUTLINE_TABLE + "(" +
                OUTLINE_ID + " integer primary key autoincrement, " +
                OUTLINE_BUILDING_ID + " integer, " +
                OUTLINE_LAT + " double, " +
                OUTLINE_LNG + " double,"
                // OUTLINE_BUILDING_ID is a foreign key to say which points belong to which build
                + " FOREIGN KEY (" + OUTLINE_BUILDING_ID + ") REFERENCES " + BUILDING_TABLE + "(" + BUILDING_ID + "));" );

        //Create Entries table.
        db.execSQL( "create table " + ENTRANCES_TABLE + "(" +
                ENTRANCES_BUILDING + " integer, " +
                ENTRANCES_LAT + " double, " +
                ENTRANCES_LNG + " double)");

        //Create Icons table.
        db.execSQL( "create table " + ICONS_TABLE + "(" +
                ICONS_ID + " integer primary key, " +
                ICONS_TYPE_ID + " text, " +
                ICONS_LAT + " double, " +
                ICONS_LNG + " double)");

        // Create Icons Type Table
        //db.execSQL( "create table " + ICONS_TABLE + "(" +
            //    ICON_TYPE_ID + " integer primary key")

        // Commenting out Fill for the moment as we are getting data from the CSV
        // Fill();
    }

    @Override
    public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion )
    {
        db.execSQL( "drop table if exists " + BUILDING_TABLE );
        db.execSQL( "drop table if exists " + OUTLINE_TABLE );
        db.execSQL( "drop table if exists " + ENTRANCES_TABLE );
        db.execSQL( "drop table if exists " + ICONS_TABLE );
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

        try
        {
            // 'Read in' one line to skip it as the first line is just titles for the fields
            buffer.readLine();

            // Loop through the CSV file, reading in each line one by one
            String line = "";
            while( ( line = buffer.readLine() ) != null )
            {
                // Temporary string to hold each element separated by a comma in the CSV file
                String[] tmpString = line.split( "," );

                // This is the query to send to the building table to create a new building record
                // The building number will be the building's ID
                // tmpString[1] is where the building's ID is ( Second field )
                // Currently there is no name implemented in the CSV file
                String buildingQuery = "INSERT INTO " + BUILDING_TABLE + " VALUES( " + tmpString[1] + ", 'NO NAME'" + ");";
                db.execSQL( buildingQuery );

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
                // i starts at 2, as that is where the points start
                for( int i = 2; i < pointNum; i += 2 )
                {
                    // Build the query to add this record information to the outlines table
                    // Note that the OUTLINE_ID is not needed as it is auto incremented
                    // tmpString[ i ] is the latitude, tmpString[ i + 1 ] is the longitude
                    String outlinePointQuery = "INSERT INTO " + OUTLINE_TABLE + "( " +
                            OUTLINE_BUILDING_ID + ", " + OUTLINE_LAT + ", " + OUTLINE_LNG + ") " +
                            "VALUES( " + tmpString[1] + ", " + tmpString[ i ] + ", " + tmpString[ i + 1 ] + ");";

                    // Submit the query to add a new point for that building!
                    db.execSQL( outlinePointQuery );
                }
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
            output.add(new Building(res.getInt( res.getColumnIndex( BUILDING_ID )), this));
            res.moveToNext();
        }

        return output;
    }

    public ArrayList<Icon> GetIcons()
    {
        ArrayList<Icon> output = new ArrayList<Icon>();

        Cursor res = getReadableDatabase().rawQuery("select * from " + ICONS_TABLE, null);
        res.moveToFirst();

        while(res.isAfterLast() == false)
        {
            output.add(new Icon(res.getInt( res.getColumnIndex( ICONS_ID )), this));
            res.moveToNext();
        }

        return output;


    }
}
