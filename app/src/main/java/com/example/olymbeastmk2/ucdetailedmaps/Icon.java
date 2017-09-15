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
        Cursor res = parent.getReadableDatabase().rawQuery("select * from " + DbHelper.ICONS_TABLE + " where " + DbHelper.ICONS_ID + "=" + Integer.toString( id ), null);
        res.moveToFirst();
        return res.getString( res.getColumnIndex( DbHelper.ICONS_TYPE ) );
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
