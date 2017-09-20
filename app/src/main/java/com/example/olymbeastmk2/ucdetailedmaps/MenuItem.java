package com.example.olymbeastmk2.ucdetailedmaps;

/**
 * Created by Riley on 19/09/2017.
 */

public class MenuItem implements Comparable<MenuItem> {

    public enum ItemType {Icon, Building}

    int id;
    ItemType type;
    String text;

    public MenuItem(int _id,  String _text)
    {
        id = _id;
        type = ItemType.Building;
        text = _text;
    }

    public MenuItem(String _text)
    {
        id = -1;
        type = ItemType.Icon;
        text = _text;
    }

    @Override
    public int compareTo(MenuItem m)
    {
        return text.compareTo(m.text);
    }

}
