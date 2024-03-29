package com.chadfunckes.test_list2.View_Adapters;

import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chadfunckes.test_list2.Alarm_Activity;
import com.chadfunckes.test_list2.Models.Group;
import com.chadfunckes.test_list2.Models.ListItem;
import com.chadfunckes.test_list2.MainActivity;
import com.chadfunckes.test_list2.MapsActivity;
import com.chadfunckes.test_list2.R;

public class MainExpandableListAdapter extends BaseExpandableListAdapter {

    private final String TAG = "MainExpandableListAdapter";
    private final Context _context;
    private final List<Group> _listDataHeader; // header groups
    // child data in format of header Group, child object
    private final HashMap<Group, List<ListItem>> _listDataChild;

    public MainExpandableListAdapter(Context context, List<Group> listDataHeader,
                                     HashMap<Group, List<ListItem>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        Log.d(TAG, "on get child, Group pos is: " + groupPosition + " child position is " + childPosititon);
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final ListItem thisChild = (ListItem) getChild(groupPosition,childPosition);
        final String childText = thisChild.name;

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }
        // set text for the title
        TextView txtListChild = (TextView) convertView.findViewById(R.id.item_text);
        txtListChild.setText(childText); // set text to the name of the object
        if (thisChild.finished == 1) txtListChild.setPaintFlags(txtListChild.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); // set strikethough if item is finished
        else txtListChild.setPaintFlags(txtListChild.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
        // set image button onClicks for child items
        ImageView trash = (ImageView) convertView.findViewById(R.id.deleteItem);
        trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(_context).setTitle("Confirm Delete")
                        .setMessage("Are you sure you want to delete " + thisChild.name + "?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MainActivity.database.removeItem(thisChild._id);
                                MainActivity.redrawList();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // canceled
                            }
                        }).show();
            }
        });
        ImageView alarm = (ImageView)convertView.findViewById(R.id.addAlarm);
        alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "alarm hit on child " + childText);
                Intent intent = new Intent(_context, Alarm_Activity.class);
                intent.putExtra("IID", thisChild._id);
                intent.putExtra("GID", thisChild.groupID);
                intent.putExtra("GROUP_NAME", MainActivity.database.getGroupName(thisChild.groupID));
                intent.putExtra("ITEM_NAME", thisChild.name);
                intent.putExtra("CALLED_ON", "ITEM");
                _context.startActivity(intent);
            }
        });
        ImageView map = (ImageView)convertView.findViewById(R.id.addMap);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "map hit on child " + childText);
                Intent intent = new Intent(_context, MapsActivity.class);
                intent.putExtra("CALLED_ON", "ITEM");
                intent.putExtra("GID", thisChild.groupID);
                intent.putExtra("GROUP_NAME", MainActivity.database.getGroupName(thisChild.groupID));
                intent.putExtra("IID", thisChild._id);
                intent.putExtra("ITEM_NAME", thisChild.name);
                _context.startActivity(intent);
            }
        });
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return _listDataHeader.get(groupPosition)._id;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return _listDataChild.get(_listDataHeader.get(groupPosition)).get(childPosition)._id;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        final Group thisGroup = (Group) getGroup(groupPosition);
        String headerTitle = thisGroup.name;

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }
        // set the text to be used in the list
        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
        // set onClicks for the groups
        ImageView trash = (ImageView) convertView.findViewById(R.id.trashcan);
        trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(_context).setTitle("Confirm Delete")
                        .setMessage("Are you sure you want to delete " + thisGroup.name + " and all sub tasks?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MainActivity.database.removeGroup(thisGroup._id);
                                MainActivity.redrawList();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // cancell
                            }
                        }).show();
            }
        });
        ImageView alarm = (ImageView) convertView.findViewById(R.id.alarmclock);
        alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "alarm button for Group " + thisGroup.name);
                Intent intent = new Intent(_context, Alarm_Activity.class);
                intent.putExtra("IID", -1);
                intent.putExtra("GID", thisGroup._id);
                intent.putExtra("GROUP_NAME", thisGroup.name);
                intent.putExtra("CALLED_ON", "GROUP");
                _context.startActivity(intent);
            }
        });
        ImageView map = (ImageView) convertView.findViewById(R.id.map);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Map clicked for Group " + thisGroup.name);
                Intent intent = new Intent(_context, MapsActivity.class);
                intent.putExtra("CALLED_ON", "GROUP");
                intent.putExtra("GID", thisGroup._id);
                intent.putExtra("GROUP_NAME", thisGroup.name);
                _context.startActivity(intent);
            }
        });
        ImageView addItem = (ImageView) convertView.findViewById(R.id.addItem);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add item into Group via dialog box
                final EditText input = new EditText(_context);
                new AlertDialog.Builder(_context).setView(input)
                        .setTitle("Add Item")
                        .setMessage("Enter new Item")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (input.getText().toString().equals("")){
                                    Toast.makeText(_context, "You must enter a name", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    ListItem newItem = new ListItem();
                                    newItem.name = input.getText().toString();
                                    MainActivity.database.addItem(thisGroup._id, newItem);
                                    MainActivity.redrawList();
                                }
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).show();
            }
        });
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}