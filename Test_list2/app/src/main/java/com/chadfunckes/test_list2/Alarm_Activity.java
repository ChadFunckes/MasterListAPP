package com.chadfunckes.test_list2;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.chadfunckes.test_list2.Adapters.AlarmListAdapter;
import com.chadfunckes.test_list2.Containers.alarms;

import java.util.List;

public class Alarm_Activity extends Activity {
    private final static String TAG = "Alarm Activity";
    private String CALLED_ON;
    int GID, IID;
    static int alYear, alMonth, alDay, alHour, alMinute = -1;

    private static ListView list; // reference for the listview
    private static ListAdapter adapter; // array adapter for the list
    private List<alarms> alarmList; // the list of alarms for this item

     FragmentManager fm = getFragmentManager();

    public Alarm_Activity(){
        // empty constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        // @TODO change CALLED_ON to an int type here and in the list adapter to save memory space
        CALLED_ON = getIntent().getStringExtra("CALLED_ON"); // called on GROUP or ITEM
        GID = getIntent().getIntExtra("GID", -1);
        IID = getIntent().getIntExtra("IID", -1);
        Log.d(TAG, "alarm list called on " + CALLED_ON + " With group ID " + GID + " and item ID " + IID);
        if (CALLED_ON.equals("GROUP")) {
            Log.d(TAG, "fill list on group");
            fillList(GID, 0);
        }
        else if (CALLED_ON.equals("ITEM")) {
            fillList(IID, 1);
        }

        list = (ListView) findViewById(R.id.alarmList);
        adapter = new AlarmListAdapter(this, alarmList);
        list.setAdapter(adapter);
    }

    public void setDate(View v){
        DialogFragment dialogFragment = new DatePicker();
        dialogFragment.show(fm, "date");
    }

    public void setTime(View v){
        DialogFragment dialogFragment = new TimePicker();
        dialogFragment.show(fm, "time");
    }

    public void setAlarm(View v){
        // check it proper time exists
        if (alYear == 1 || alMinute == -1){
            Toast toast = Toast.makeText(this, "Enter a valid alarm time", Toast.LENGTH_SHORT);
            toast.show();
            return;
        };
        // set into db
        MainActivity.database.addAlarm(GID, IID, alYear, alMonth, alDay, alHour, alMinute);
        // change list
        refreshList();
    }

    private void refreshList(){
        if (IID != -1)
            fillList(IID, 1);
        else
            fillList(GID, 0);

        adapter = new AlarmListAdapter(this, alarmList);
        list.setAdapter(adapter);
    }

    public void fillList(int ID, int from){
        // get alarm list from GID or IID
        alarmList = MainActivity.database.getAlarms(ID, from);
        Log.d(TAG, "list is " + alarmList.toString());
    }
}
