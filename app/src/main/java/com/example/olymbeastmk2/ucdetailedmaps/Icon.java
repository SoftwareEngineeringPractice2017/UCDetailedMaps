package com.example.olymbeastmk2.ucdetailedmaps;

import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Olymbeastmk2 on 09-Sep-17.
 */
public class Icon
{
    private int id;
    private DbHelper parent;

    private String type;
    private boolean hasType;

    private String label;
    private boolean hasLabel;

    private LatLng location;
    private boolean hasLocation;

    public Marker marker = null;

    public Icon(int _id, DbHelper _parent)
    {
        id = _id;
        parent = _parent;

        hasType = false;
        hasLabel = false;
        hasLocation = false;
    }

    public void Load()
    {
        getType();
        getLabel();
        getLocation();
    }

    public int getID()
    {
        return id;
    }

    public String getType()
    {
        if(hasType)
        {
            return type;
        }

        //Get the TYPE_ID
        Cursor res = parent.getReadableDatabase().rawQuery("select * from " + DbHelper.ICONS_TABLE + " where " + DbHelper.ICONS_ID + "=" + Integer.toString( id ), null);
        res.moveToFirst();
        int typeID = res.getInt( res.getColumnIndex( DbHelper.ICONS_TYPE_ID ) );

        //Use the TYPE_ID to get the ICONTYPES_NAME.
        Cursor res2 = parent.getReadableDatabase().rawQuery("select * from " + DbHelper.ICONTYPES_TABLE + " where " + DbHelper.ICONTYPES_ID + "=" + Integer.toString( typeID ), null);
        res2.moveToFirst();

        type = res2.getString(res2.getColumnIndex( DbHelper.ICONTYPES_NAME ) );
        hasType = true;
        return type;
    }

    public String getLabel()
    {
        if(hasLabel)
        {
            return label;
        }

        Cursor res = parent.getReadableDatabase().rawQuery("select * from " + DbHelper.ICONS_TABLE + " where " + DbHelper.ICONS_ID + "=" + Integer.toString( id ), null);
        res.moveToFirst();

        label = res.getString( res.getColumnIndex( DbHelper.ICONS_LABEL ) );
        hasLabel = true;
        return label;
    }

    public LatLng getLocation()
    {
        if(hasLocation)
        {
            return location;
        }

        Cursor res = parent.getReadableDatabase().rawQuery("select * from " + DbHelper.ICONS_TABLE + " where " + DbHelper.ICONS_ID + "=" + Integer.toString( id ), null);
        res.moveToFirst();
        double latitude = res.getDouble( res.getColumnIndex( DbHelper.ICONS_LAT ) );
        double longitude = res.getDouble( res.getColumnIndex( DbHelper.ICONS_LNG ) );

        location = new LatLng(latitude, longitude);
        hasLocation = true;
        return location;
    }

    // This will make sure that an icon has not been added once
    boolean hasBeenAddedToMap = false;
    

    // This adds an icon as a marker to the map and stores the marker information ( For Future reference )
    public void AddAsMarker( GoogleMap mMap, Resources resources )
    {
        Bitmap iconBitmap = parent.GetIconTypeBitmap( getType() );

        MarkerOptions mkrOptPass = new MarkerOptions().position( getLocation() ).title(getType());
        mkrOptPass.icon( BitmapDescriptorFactory.fromBitmap( parent.GetIconTypeBitmap( getType() ) ) );

        marker = mMap.addMarker( mkrOptPass );

        // Indicate that this icon is on the map.
        hasBeenAddedToMap = true;
    }

    public void showTitle()
    {
        marker.showInfoWindow();
    }


    public void setHidden(boolean hidden)
    {
        marker.setVisible(!hidden);
    }

}
