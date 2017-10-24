package com.example.olymbeastmk2.ucdetailedmaps;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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

    private Marker marker;
    private boolean hasMarker;

    private int buildingNumber;

    public Room(int _ID, DbHelper _Parent, int _BuildingNumber)
    {
        id = _ID;
        parent = _Parent;
        marker = null;

        hasBuildingID = false;
        hasFloor = false;
        hasLocation = false;
        hasTitle = false;
        hasMarker = false;

        buildingNumber = _BuildingNumber;
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
        buildingID = res.getInt( res.getColumnIndex( DbHelper.ROOMS_BUILDING ) );
        res.close();
        hasBuildingID = true;
        return buildingID;
    }

    public String getFullName()
    {
        return Integer.toString(buildingNumber) + getTitle();
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
        res.close();
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
        res.close();
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
        title = res.getString( res.getColumnIndex( DbHelper.ROOMS_TITLE ) );
        res.close();
        hasTitle = true;
        return title;
    }

    // Whole function retrieved from: https://stackoverflow.com/questions/30173397/show-text-on-polygon-android-google-map-v2

    private Marker addText(final Context context, final GoogleMap map,
                          final LatLng location, final String text, final int padding,
                          final int fontSize) {

        if (context == null || map == null || location == null || text == null
                || fontSize <= 0) {
            return marker;
        }

        final TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextSize(fontSize);

        final Paint paintText = textView.getPaint();

        final Rect boundsText = new Rect();
        paintText.getTextBounds(text, 0, textView.length(), boundsText);
        paintText.setTextAlign(Paint.Align.CENTER);

        final Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        final Bitmap bmpText = Bitmap.createBitmap(boundsText.width() + 2
                * padding, boundsText.height() + 2 * padding, conf);

        final Canvas canvasText = new Canvas(bmpText);
        paintText.setColor(Color.BLACK);

        canvasText.drawText(text, canvasText.getWidth() / 2,
                canvasText.getHeight() - padding - boundsText.bottom, paintText);

        final MarkerOptions markerOptions = new MarkerOptions()
                .position(location)
                .icon(BitmapDescriptorFactory.fromBitmap(bmpText))
                .anchor(0.5f, 1);

        marker = map.addMarker(markerOptions);
        hasMarker = true;

        return marker;
    }

    // End borrowed function.

    public void showMarker(Context context, GoogleMap map)
    {
        if(hasMarker)
        {
            marker.setVisible(true);
        }
        else
        {
            addText(context, map, getLocation(), getTitle(), 0, 12);
        }
    }

    public void hideMarker()
    {
        if(!hasMarker)
        {
            return;
        }
        else
        {
            marker.setVisible(false);
        }
    }







}
