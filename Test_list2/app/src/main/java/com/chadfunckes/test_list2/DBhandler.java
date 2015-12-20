package com.chadfunckes.test_list2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.chadfunckes.test_list2.Containers.group;
import com.chadfunckes.test_list2.Containers.listItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DBhandler extends SQLiteOpenHelper {
    private final static String TAG = "DBhandler"; // debug tag
    public static SQLiteDatabase db;
    private Context dbContext;  // universal context item
    private static final int DATABASE_VERSION = 1;          //db version number
    private static final String DATABASE_NAME = "master";
    private static final String GROUP_TABLE = "GROUPS";
    private static final String ITEMS_TABLE = "ITEMS";

    public DBhandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.dbContext = context;
        this.db = this.getWritableDatabase();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        createGroups();
        createItems();
        createSampleGroups();
        createSampleItems();
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    private void createGroups(){
        String CMD = "CREATE TABLE " + GROUP_TABLE +
                " ( _ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "NAME TEXT NOT NULL, " +
                "GPS_LAT DOUBLE, " +
                "GPS_LNG DOUBLE, " +
                "ALARM TEXT);";
        db.execSQL(CMD);
    }
    private void createSampleGroups(){
        ContentValues cv = new ContentValues();
        cv.put("NAME", "Groceries");
        db.insert(GROUP_TABLE, null, cv);
        cv.put("NAME", "Gifts");
        db.insert(GROUP_TABLE, null, cv);
        cv.put("NAME", "Camping this weekend");
        db.insert(GROUP_TABLE, null, cv);
    }
    private void createItems(){
        String CMD = "CREATE TABLE " + ITEMS_TABLE +
                " ( _ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "GROUP_ID INTEGER NOT NULL, " +
                "NAME TEXT NOT NULL, " +
                "FINISHED INTEGER NOT NULL, " +
                "HAS_EXTRA INTEGER NOT NULL, " +
                "NOTES TEXT, " +
                "IMAGE TEXT, " +
                "GPS_LAT DOUBLE, "+
                "GPS_LNG DOUBLE, " +
                "ALARM TEXT);";
        db.execSQL(CMD);
    }
    private void createSampleItems(){
        ContentValues cv = new ContentValues();
        cv.put("GROUP_ID", 1);
        cv.put("NAME", "eggs");
        cv.put("FINISHED", 0);
        cv.put("HAS_EXTRA", 0);
        db.insert(ITEMS_TABLE, null, cv);
        cv.put("GROUP_ID", 1);
        cv.put("NAME", "milk");
        cv.put("FINISHED", 0);
        db.insert(ITEMS_TABLE, null, cv);
        cv.put("GROUP_ID", 1);
        cv.put("NAME", "bacon");
        cv.put("FINISHED", 0);
        db.insert(ITEMS_TABLE, null, cv);

        cv.put("GROUP_ID", 2);
        cv.put("NAME", "table saw");
        cv.put("FINISHED", 0);
        cv.put("HAS_EXTRA", 0);
        db.insert(ITEMS_TABLE, null, cv);
        cv.put("GROUP_ID", 2);
        cv.put("NAME", "salad shooter");
        cv.put("FINISHED", 0);
        cv.put("HAS_EXTRA", 0);
        db.insert(ITEMS_TABLE, null, cv);
        cv.put("GROUP_ID", 2);
        cv.put("NAME", "Lego Set");
        cv.put("FINISHED", 0);
        cv.put("HAS_EXTRA", 0);
        db.insert(ITEMS_TABLE, null, cv);

        cv.put("GROUP_ID", 3);
        cv.put("NAME", "marshmellows");
        cv.put("FINISHED", 0);
        cv.put("HAS_EXTRA", 0);
        db.insert(ITEMS_TABLE, null, cv);
        cv.put("GROUP_ID", 3);
        cv.put("NAME", "tent");
        cv.put("FINISHED", 0);
        cv.put("HAS_EXTRA", 0);
        db.insert(ITEMS_TABLE, null, cv);
        cv.put("GROUP_ID", 3);
        cv.put("NAME", "Camp Stove");
        cv.put("FINISHED", 0);
        cv.put("HAS_EXTRA", 0);
        db.insert(ITEMS_TABLE, null, cv);
    }
    // get a list of group object from the database for display headings
    public List<group> getGroups(){
        List<group> listData = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM " + GROUP_TABLE +";", null);
        if (c == null) return listData; // if no groups to get return empty list

        c.moveToFirst();
        while (!c.isAfterLast()){
            group g = new group();
            g._id = c.getInt(0);
            g.name = c.getString(1);
            g.GPS_LAT = c.getDouble(2);
            g.GPS_LNG = c.getDouble(3);
            g.alarm = c.getString(4);
            listData.add(g);
            c.moveToNext();
        }

        return listData;
    }
    // get a hash with group item keys that match the list taken as the parameter
    public HashMap<group, List<listItem>> getItems(List<group> g){
        HashMap<group, List<listItem>> childMap = new HashMap<group, List<listItem>>();
        List<listItem> il;
        Cursor c;

        for (int i = 0; i < g.size(); i++){
            c = db.rawQuery("SELECT * FROM "+ ITEMS_TABLE + " WHERE GROUP_ID =" + g.get(i)._id, null);
            if (c == null) break;
            il = new ArrayList<>();
            c.moveToFirst();
            while (!c.isAfterLast()) {
                listItem item = new listItem();
                item._id = c.getInt(0);
                item.groupID = c.getInt(1);
                item.name = c.getString(2);
                item.finished = c.getInt(3);
                item.has_extra = c.getInt(4);
                item.notes = c.getString(5);
                item.image = c.getString(6);
                item.GPS_LAT = c.getDouble(7);
                item.GPS_LNG = c.getDouble(8);
                item.alarm = c.getString(9);
                il.add(item);
                c.moveToNext();
            }
            childMap.put(g.get(i), il);
        }

        return childMap;
    }

    public void addGroup(final group grp){
        ContentValues cv = new ContentValues();
        cv.put("NAME", grp.name);
        cv.put("GPS_LAT", grp.GPS_LAT);
        cv.put("GPS_LNG", grp.GPS_LNG);
        cv.put("ALARM", grp.alarm);
        db.insert(GROUP_TABLE, null, cv);
    }
    public void removeGroup(final int grpID){
        // delete all children
        db.delete(ITEMS_TABLE, "GROUP_ID=" + grpID, null);
        // delete group
        db.delete(GROUP_TABLE, "_ID=" + grpID, null);
    }

    public void addItem(final int groupID, final listItem item){
        ContentValues cv = new ContentValues();

        cv.put("GROUP_ID", groupID);
        cv.put("NAME", item.name);
        cv.put("FINISHED", item.finished);
        cv.put("HAS_EXTRA", item.has_extra);
        cv.put("NOTES", item.notes);
        cv.put("IMAGE", item.image);
        cv.put("GPS_LAT", item.GPS_LAT);
        cv.put("GPS_LNG", item.GPS_LNG);
        cv.put("ALARM", item.alarm);

        db.insert(ITEMS_TABLE, null, cv);
    }
    public boolean removeItem(final int itemID){
        return db.delete(ITEMS_TABLE, "_ID="+itemID, null) > 0;
    }
    public boolean itemToggleFinished(final listItem thisItem){ // will return true if the item was given the finished value, false if it was changed to unfinished
        String CMD = "SELECT * FROM " + ITEMS_TABLE + " WHERE _ID = " + thisItem._id;
        Cursor c = db.rawQuery(CMD, null);
        if (c == null){
            Toast.makeText(dbContext, "Internal DB error on toggle item finished state", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            ContentValues cv = new ContentValues();
            String[] args = {String.valueOf(thisItem._id)};
            c.moveToFirst();
            Log.d(TAG, "this is item id " + thisItem._id + " name " + thisItem.name);
            Log.d(TAG, "finished value is " + c.getInt(3));
            if (c.getInt(3) == 0) {
                cv.put("FINISHED", 1);
                int xx = db.update(ITEMS_TABLE, cv, "_ID=?", args);
                Log.d(TAG, "update returned code " + xx);
                return true;
            }
            else {
                cv.put("FINISHED", 0);
                db.update(ITEMS_TABLE, cv, "_ID=?", args);
                return false;
            }
        }
    }
    public void destroyDB(){
        dbContext.deleteDatabase(DATABASE_NAME);
    }
}