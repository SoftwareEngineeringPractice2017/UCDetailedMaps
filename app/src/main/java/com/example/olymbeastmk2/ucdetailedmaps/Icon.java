package com.example.olymbeastmk2.ucdetailedmaps;

import android.database.Cursor;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Olymbeastmk2 on 09-Sep-17.
 */
public class Icon
{
    private int id;
    private DbHelper parent;

    public Icon(int _id, DbHelper _parent)
    {
        id = _id;
        parent = _parent;
    }

    public int getID()
    {
        return id;
    }

    public String getType()
    {
        //Get the TYPE_ID
        Cursor res = parent.getReadableDatabase().rawQuery("select * from " + DbHelper.ICONS_TABLE + " where " + DbHelper.ICONS_ID + "=" + Integer.toString( id ), null);
        res.moveToFirst();
        int typeID = res.getInt( res.getColumnIndex( DbHelper.ICONS_TYPE_ID ) );

        //Use the TYPE_ID to get the ICONTYPES_NAME.
        Cursor res2 = parent.getReadableDatabase().rawQuery("select * from " + DbHelper.ICONTYPES_TABLE + " where " + DbHelper.ICONTYPES_ID + "=" + Integer.toString( typeID ), null);
        res2.moveToFirst();
        return res2.getString(res.getColumnIndex( DbHelper.ICONTYPES_NAME ) );
    }

    public String getLabel()
    {
        Cursor res = parent.getReadableDatabase().rawQuery("select * from " + DbHelper.ICONS_TABLE + " where " + DbHelper.ICONS_ID + "=" + Integer.toString( id ), null);
        res.moveToFirst();
        return res.getString( res.getColumnIndex( DbHelper.ICONS_LABEL ) );
    }

    public LatLng getLocation()
    {
        Cursor res = parent.getReadableDatabase().rawQuery("select * from " + DbHelper.ICONS_TABLE + " where " + DbHelper.ICONS_ID + "=" + Integer.toString( id ), null);
        res.moveToFirst();
        double latitude = res.getDouble( res.getColumnIndex( DbHelper.ICONS_LAT ) );
        double longitude = res.getDouble( res.getColumnIndex( DbHelper.ICONS_LNG ) );
        return new LatLng(latitude, longitude);
    }

}
