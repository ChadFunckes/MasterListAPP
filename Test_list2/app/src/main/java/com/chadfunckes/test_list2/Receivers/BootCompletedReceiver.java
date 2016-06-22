package com.chadfunckes.test_list2.Receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.chadfunckes.test_list2.DBhandler;
import com.chadfunckes.test_list2.Models.Alarm;

import java.util.Calendar;
import java.util.List;

public class BootCompletedReceiver extends BroadcastReceiver {

    public static DBhandler database;

    @Override
    public void onReceive(Context context, Intent intent) {
        // DO WORK HERE TO RE-ESTABLISH ALARMS
        database = new DBhandler(context);
        Alarm thisAlarm;
        final List<Alarm> alarmList = database.getALLAlarms();
        if (alarmList.size() < 1) return;

        Calendar cal = (Calendar) Calendar.getInstance().clone();
        Intent AlarmIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        for (int i = 0; i < alarmList.size(); i++){
            thisAlarm = alarmList.get(i);
            AlarmIntent.putExtra("AID", thisAlarm.AID);
            if (thisAlarm.IID == 0){ // then this alarm was called on an entire Group
                AlarmIntent.putExtra("CALLED_ON", "GROUP");
            }
            else { // this alram was called in an item
                AlarmIntent.putExtra("CALLED_ON", "ITEM");
                String item = database.getItemName(thisAlarm.IID);
                AlarmIntent.putExtra("ITEM_NAME", item);
            }
            // set Group name (will always have Group name)
            AlarmIntent.putExtra("GROUP_NAME", database.getGroupName(thisAlarm.GID));
            // set the alarm time based on what is stored
            cal.set(thisAlarm.year, thisAlarm.month, thisAlarm.day, thisAlarm.hour, thisAlarm.minute, 0);
            // set pending intent
            pendingIntent = PendingIntent.getBroadcast(
                    context, thisAlarm.AID, AlarmIntent, 0);
            // set the alarm
            alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                    pendingIntent);

            thisAlarm = null; // free alarm info when done w it...
        } // loop for all Alarm
    }
}