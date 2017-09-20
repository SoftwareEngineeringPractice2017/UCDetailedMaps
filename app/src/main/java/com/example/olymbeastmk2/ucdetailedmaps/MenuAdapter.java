package com.example.olymbeastmk2.ucdetailedmaps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Riley on 19/09/2017.
 */

public class MenuAdapter extends ArrayAdapter<String>{

    ArrayList<MenuItem> elements = new ArrayList<MenuItem>();

    public MenuAdapter(Context context, int resource, ArrayList<String> superObjects, ArrayList<MenuItem> objects) {
        super (context, resource, superObjects);
        elements = objects;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.drawer_list_item, parent, false);
        }

        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
        checkBox.setChecked(true);

        TextView label = (TextView) convertView.findViewById(R.id.textView);
        label.setText(elements.get(position).text);

        return convertView;
    }

}
