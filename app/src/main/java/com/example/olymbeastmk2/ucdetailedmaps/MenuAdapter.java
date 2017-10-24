package com.example.olymbeastmk2.ucdetailedmaps;

import android.content.Context;
import android.opengl.Visibility;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Riley on 19/09/2017.
 */

public class MenuAdapter extends ArrayAdapter<String>{

    //ArrayList<MenuItem> elements = new ArrayList<MenuItem>();

    MenuHandler menuHandler;

    public MenuAdapter(Context context, int resource, MenuHandler objects) {
        super (context, resource, objects.menuItemsNames());
        menuHandler = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.drawer_list_item, parent, false);
        }


        final int actualLocation = menuHandler.currentIndexToActualIndex(position);
        String addSpaces = "";

        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);

        if(menuHandler.get(actualLocation).type != MenuItem.ItemType.Icon)
        {
            checkBox.setVisibility(View.GONE);
            addSpaces = "  ";
        }
        else
        {
            checkBox.setVisibility(View.VISIBLE);
        }

        checkBox.setChecked(menuHandler.getCheckedState(actualLocation));
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuHandler.setCheckedState(actualLocation, !menuHandler.getCheckedState(actualLocation));
            }
        });

        TextView label = (TextView) convertView.findViewById(R.id.textView);
        label.setText(addSpaces + menuHandler.getCurrentMenu().get(position).text);
        label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuHandler.setCheckedState(actualLocation, true);
                menuHandler.focus(actualLocation);
            }
        });

        return convertView;
    }

}
