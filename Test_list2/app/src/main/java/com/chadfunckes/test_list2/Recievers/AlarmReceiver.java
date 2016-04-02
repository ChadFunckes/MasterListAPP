package com.chadfunckes.test_list2.Recievers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.ListView;

import com.chadfunckes.test_list2.DBhandler;
import com.chadfunckes.test_list2.MainActivity;
import com.chadfunckes.test_list2.R;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class AlarmReceiver extends BroadcastReceiver {

    public static int NOTIFICATION_ID;
    public static DBhandler database;

    @Override
    public void onReceive(Context k1, Intent k2) {
        // set notification ID based on the intent data ID .. should be GID for group alarm GID+IID for item alarm
        NOTIFICATION_ID = k2.getIntExtra("AID", -1);
        String messageText;
        Intent intent = new Intent(k1, MainActivity.class);

        // this puts the intent into the stackbuilder (required to put an intent in the notification)
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(k1);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);

        // this sets up the pending intent for the nitifcation manager and updates the notification with the same ID
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Ibox Style multi Line notification
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.addLine("Reminder Due!");
        if (k2.getStringExtra("CALLED_ON").equals("GROUP")){
            inboxStyle.addLine("On Group: " + k2.getStringExtra("GROUP_NAME"));
        }
        if (k2.getStringExtra("CALLED_ON").equals("ITEM")){
            inboxStyle.addLine("On Group: " + k2.getStringExtra("GROUP_NAME"))
                    .addLine("On Item: " + k2.getStringExtra("ITEM_NAME"));
        }
        // Basic message text
        messageText = "Reminder due!";
        // this builds the notification, sets the message, the icon...etc...
        // see developer/reference/android/app/notification.builder.html for all options here
        Notification notification = new NotificationCompat.Builder(k1)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Master List Notification")
                .setContentText(messageText)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(pendingIntent)
                .setStyle(inboxStyle)
                .build();

        // get the systems notification manager
        NotificationManager notificationManager = (NotificationManager) k1.getSystemService(k1.NOTIFICATION_SERVICE);

        //send the actual notification
        notificationManager.notify(NOTIFICATION_ID, notification);
        // *** once notification is given alarm is deleted *** @TODO possibly add dismiss and snooze buttons to alarm??
        database = new DBhandler(k1);
        database.removeAlarm(NOTIFICATION_ID);

    }

}
