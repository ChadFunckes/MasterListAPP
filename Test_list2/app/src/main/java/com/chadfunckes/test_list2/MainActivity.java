package com.chadfunckes.test_list2;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Toast;

import com.chadfunckes.test_list2.Adapters.MainExpandableListAdapter;
import com.chadfunckes.test_list2.Containers.group;
import com.chadfunckes.test_list2.Containers.listItem;

public class MainActivity extends Activity {
    private final String TAG = "MainActivity"; // debug log tag
    private static int lastExpandedPosition = -1; // used for collapsing unused groups
    // expandable list and dadapters
    static MainExpandableListAdapter listAdapter;
    static ExpandableListView expListView;
    // List Containers
    static List<group> listDataHeader;
    static HashMap<group, List<listItem>> listDataChild;
    static Context mContext;
    public static DBhandler database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main);
        // get access to database
        database = new DBhandler(this);
        //database.destroyDB();
        // preparing list data
        fillList();
        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        expListView.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                listItem thisItem = (listItem)listAdapter.getChild(groupPosition, childPosition); // get the child item
                Log.d(TAG, "child item modified was id: " + thisItem._id + " named " + thisItem.name);
                boolean finished = database.itemToggleFinished(thisItem);
                Log.d(TAG, "finished thanged to " + finished);
                redrawList();
                return false;
            }
        });

        expListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "GROUP LONG CLICK");
                int childPosition, groupPosition;
// @TODO this section gets long clicks of the group and the child, use for editing.
                int itemType = ExpandableListView.getPackedPositionType(id);

                if (itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    Log.d(TAG, "long child click");
                    childPosition = ExpandableListView.getPackedPositionChild(id);
                    groupPosition = ExpandableListView.getPackedPositionGroup(id);

                    //@TODO do your per-item callback here (edit text dialog to change name?
                    return true; //true if we consumed the click, false if not

                } else if (itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                    Log.d(TAG, "long group click");
                    groupPosition = ExpandableListView.getPackedPositionGroup(id);
                    // @TODO do your per-group callback here
                    return true; //true if we consumed the click, false if not

                } else {
                    // null item, we don't consume the click
                    return false;
                }
            }
        });

        // Listview Group expanded listener, collapse other groups when this one is expanded
        expListView.setOnGroupExpandListener(new OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                // this section collapses the other open groups
                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    expListView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });

        listAdapter = new MainExpandableListAdapter(this, listDataHeader, listDataChild);
        // setting list adapter
        expListView.setAdapter(listAdapter);
    }

    public static void redrawList(){
        fillList();
        listAdapter = new MainExpandableListAdapter(mContext, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);
        // if expanded was last reset to avoid out of bounds expansion
        if (lastExpandedPosition >= listDataHeader.size()) lastExpandedPosition = -1;
        // expand the last set open before redraw
        if (lastExpandedPosition != -1) expListView.expandGroup(lastExpandedPosition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case (R.id.addGroup):
                // start add new group dialog
                final EditText input = new EditText(MainActivity.this);
                new AlertDialog.Builder(MainActivity.this)
                        .setView(input)
                        .setTitle("Add Group")
                        .setMessage("Enter the name of the new group")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (input.getText().toString().equals("")) {
                                    Toast.makeText(MainActivity.this, "You must enter a name", Toast.LENGTH_SHORT).show();
                                    dialog.cancel();
                                } else {
                                    group grp = new group();
                                    grp.name = input.getText().toString();
                                    database.addGroup(grp);
                                    redrawList();
                                }
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
        }
        return super.onOptionsItemSelected(item);
    }

    // fill the list
    private static void fillList(){
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        listDataHeader = database.getGroups();
        if (listDataHeader.size() == 0) return; // if no groups then no items
        listDataChild = database.getItems(listDataHeader);
    }

}


