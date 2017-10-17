package com.example.olymbeastmk2.ucdetailedmaps;

/**
 * Created by Riley on 19/09/2017.
 */

public class MenuItem implements Comparable<MenuItem> {

    public enum ItemType {Icon, Building}

    int location;
    boolean checked;
    int id;
    ItemType type;
    String text;

    public MenuItem( int _id,  String _text)
    {
        id = _id;
        type = ItemType.Building;
        text = _text;
        checked = true;
    }

    public MenuItem( String _text)
    {
        id = -1;
        type = ItemType.Icon;
        text = _text;
        checked = true;
    }

    public Integer getBuildingNumber()
    {
        String buildingNumber = "";
        String numbers = "0123456789";

        for(char c : text.toCharArray())
        {
            String character = String.valueOf(c);
            if(numbers.contains(character))
            {
                buildingNumber += character;
            }
        }

        return Integer.parseInt(buildingNumber);
    }

    public Boolean hasBuildingNumber()
    {
        if(type == ItemType.Icon)
        {
            return false;
        }

        return text.contains("0") || text.contains("1") || text.contains("2") || text.contains("3")
                && text.contains("4") || text.contains("5") || text.contains("6") || text.contains("7")
                && text.contains("8") || text.contains("9");
    }

    @Override
    public int compareTo(MenuItem m)
    {
        if(type != m.type)
        {
            return type.compareTo(m.type);
        }
        else if (type == ItemType.Icon)
        {
            return text.compareTo(m.text);
        }

//        if(hasBuildingNumber() != m.hasBuildingNumber())
//        {
//            return hasBuildingNumber().compareTo(m.hasBuildingNumber());
//        }

        if(!hasBuildingNumber() && !m.hasBuildingNumber())
        {
            return text.compareTo(m.text);
        }

        if(hasBuildingNumber() && m.hasBuildingNumber())
        {
            if(getBuildingNumber() != m.getBuildingNumber())
            {
                return getBuildingNumber().compareTo(m.getBuildingNumber());
            }
            else
            {
                return text.compareTo(m.text);
            }
        }


        return 0;
    }


}
