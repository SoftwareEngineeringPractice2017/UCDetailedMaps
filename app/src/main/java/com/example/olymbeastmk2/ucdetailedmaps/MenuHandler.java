package com.example.olymbeastmk2.ucdetailedmaps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Riley on 05/10/2017.
 */

public class MenuHandler {

    private ArrayList< MenuItem > menuItems;
    private ArrayList< MenuItem > currentMenu;

    public void populate(ArrayList< Building > _buildings, HashMap< String, ArrayList< Icon > > _icons)
    {
        //ArrayList to be loaded and returned.
        ArrayList< MenuItem > iconOutput = new ArrayList< MenuItem >();
        ArrayList< MenuItem > buildingOutput = new ArrayList< MenuItem >();

        //Each key in icons represents an IconType.
        //Cycle through each IconType and add each as a new MenuItem.
        for( String s : _icons.keySet() )
        {
            iconOutput.add( new MenuItem( s ) );
        }

        //Sort output alphabetically.
        Collections.sort( iconOutput );

        //Add each building name as a MenuItem.
        for( Building b : _buildings )
        {
            buildingOutput.add( new MenuItem( b.getID(), b.getName() ) );
        }

        Collections.sort( buildingOutput );

        ArrayList< MenuItem > output = new ArrayList< MenuItem >();
        output.addAll(iconOutput);
        output.addAll(buildingOutput);

        int location = 0;
        for(MenuItem m : output)
        {
            m.location = location++;
            m.checked = location % 2 == 0;
        }

        menuItems = output;
        currentMenu = menuItems;
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
        menuItems.get(index).checked = value;
    }

    public int currentIndexToActualIndex(int index)
    {
        return currentMenu.get(index).location;
    }

}
