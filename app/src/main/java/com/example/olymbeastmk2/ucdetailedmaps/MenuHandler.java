package com.example.olymbeastmk2.ucdetailedmaps;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Riley on 05/10/2017.
 */

public class MenuHandler {

    private ArrayList< MenuItem > menuItems;
    private ArrayList< MenuItem > currentMenu;

    private MapsActivity parent;

    public MenuHandler(MapsActivity _Parent)
    {
        parent = _Parent;
    }

    public void populate(ArrayList< Building > _buildings, HashMap< String, ArrayList< Icon > > _icons)
    {
        //ArrayList to be loaded and returned.
        ArrayList< MenuItem > output = new ArrayList< MenuItem >();

        //Each key in icons represents an IconType.
        //Cycle through each IconType and add each as a new MenuItem.
        for( String s : _icons.keySet() )
        {
            output.add( new MenuItem( s ) );
        }

        //Add each building name as a MenuItem.
        for( Building b : _buildings )
        {
            output.add( new MenuItem( b.getID(), b.getName() ) );
            for(int floor : b.getRooms().keySet())
            {
                for(Room r : b.getRooms().get(floor))
                {
                    output.add(new MenuItem(r));
                }
            }
        }

        Collections.sort(output);

        int location = 0;
        for(MenuItem m : output)
        {
            m.location = location++;
            m.checked = false;
        }

        menuItems = output;
        currentMenu = menuItems;

        //filter("");
    }

    public void filter(String filter )
    {
        filter = filter.toLowerCase();
        //ArrayList to be loaded with MenuItems that start with filter.
        ArrayList< MenuItem > output = new ArrayList< MenuItem >();

        //Cycle through each MenuItem in input.
        for( MenuItem m : menuItems )
        {
            if( m.text.toLowerCase().contains( filter ) )
            {
                output.add( m );

            }
        }

        if(filter.isEmpty())
        {
            ArrayList< MenuItem > output2 = new ArrayList<>();
            for(MenuItem m : output)
            {
                if(m.type != MenuItem.ItemType.Room)
                {
                    output2.add(m);
                }
                else
                {
                    Log.d( "UCDetailedMaps", "Skipped " + m.text);
                }
            }
            currentMenu = output2;
            return;
        }

        currentMenu = output;
    }

    public ArrayList< String > menuItemsNames()
    {
        ArrayList< String > objects = new ArrayList< String >();
        for( MenuItem m : currentMenu )
        {
            objects.add( m.text );
        }
        return objects;
    }

    public ArrayList< MenuItem > getMenuItems()
    {
        return menuItems;
    }

    public ArrayList< MenuItem > getCurrentMenu()
    {
        return currentMenu;
    }

    public MenuItem get(int index)
    {
        return menuItems.get(index);
    }

    public boolean getCheckedState(int index)
    {
        return menuItems.get(index).checked;
    }

    public void setCheckedState(int index, boolean value)
    {
        MenuItem target = menuItems.get(index);
        target.checked = value;
        if(target.type == MenuItem.ItemType.Icon)
        {
            parent.setVisibleIconsWithType(target.text, !value);
        }
    }

    public void focus(int position)
    {
        parent.FocusOnMenuItem(position);
    }

    public int currentIndexToActualIndex(int index)
    {
        return currentMenu.get(index).location;
    }

}
