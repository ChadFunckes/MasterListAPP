package com.chadfunckes.test_list2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.chadfunckes.test_list2.Models.Alarm;
import com.chadfunckes.test_list2.Models.Fence;
import com.chadfunckes.test_list2.Models.Group;
import com.chadfunckes.test_list2.Models.ListItem;

import java.util.ArrayList;
import java.util.Collections;
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
    private static final String ALARM_TABLE = "ALARMS";
    private static final String LOC_TABLE = "LOCATIONS";
    private static final String FENCES_TABLE = "FENCES";

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
        createAlarms();
        createLocations();
        createFences();
        createSampleGroups();
        createSampleItems();
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    private void createGroups(){
        String CMD = "CREATE TABLE " + GROUP_TABLE +
                " ( _ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "NAME TEXT NOT NULL);";
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
                "IMAGE TEXT);";
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
    private void createAlarms(){
        String CMD = "CREATE TABLE " + ALARM_TABLE +
                "(GID INTEGER NOT NULL, " +
                "IID INTEGER, " +
                "YEAR INTEGER, " +
                "MONTH INTEGER, " +
                "DAY INTEGER, " +
                "HOUR INTEGER, " +
                "MINUTE INTEGER, " +
                "AID INTEGER PRIMARY KEY AUTOINCREMENT);";

        db.execSQL(CMD);
    }
    private void createLocations(){
        // ARR_DEP VALUE IS TRUE ON DEPART, FALSE ON ARRIVE
        String CMD = "CREATE TABLE " + LOC_TABLE +
                " (GID INTEGER NOT NULL, " +
                "IID INTEGER, " +
                "LAT DOUBLE, " +
                "LNG DOUBLE, " +
                "ADDRESS TEXT, " +
                "ARR_DEP INTEGER);";
        db.execSQL(CMD);
    }
    private void createFences(){
        // ARR_DEP VALUE IS TRUE ON DEPART, FALSE ON ARRIVE
        String CMD = "CREATE TABLE " + FENCES_TABLE +
                " (FID TEXT NOT NULL, " +
                "ADDRESS TEXT NOT NULL, " +
                "LAT DOUBLE, " +
                "LNG DOUBLE, " +
                "ARR_DEP INT, " +
                "DIST INT, " +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT);";
        db.execSQL(CMD);
    }

    // fences functions
    public void addFence(final Fence fence){
        ContentValues cv = new ContentValues();
        cv.put("FID", fence.FID);
        cv.put("ADDRESS", fence.ADD);
        cv.put("LAT", fence.LAT);
        cv.put("LNG", fence.LNG);
        cv.put("ARR_DEP", fence.ARR_DEP);
        cv.put("DIST", fence.DIST);
        db.insert(FENCES_TABLE, null, cv);
        Log.d(TAG, "Fence ID " + fence.FID + " Inserted in DB");
    }
    public void removeFence(final String FID){
        db.delete(FENCES_TABLE, "FID='" + FID + "'", null);
    }
    public Fence getFence(int GID, int IID){
        Cursor c = db.rawQuery("SELECT * FROM " + FENCES_TABLE + " WHERE FID = 'G"+GID+"I"+IID+"'", null);

        c.moveToFirst();
        if (!c.isAfterLast()){
            Fence fence = new Fence();
            fence.FID = c.getString(0);
            fence.ADD = c.getString(1);
            fence.LAT = c.getDouble(2);
            fence.LNG = c.getDouble(3);
            fence.ARR_DEP = c.getInt(4);
            fence.DIST = c.getInt(5);
            fence.SYSTEMID = c.getInt(6);
            return fence;
        }
        return null;
    }
    public int getFenceSystemID(Fence fence){
        Cursor c = db.rawQuery("SELECT * FROM " + FENCES_TABLE + " WHERE FID = '"+ fence.FID + "'", null);
        c.moveToFirst();
        if (!c.isAfterLast()){
            return c.getInt(6);
        }
        return 0;
    }
    // Alarm functions
    public int addAlarm(final int GID, final int IID, final int alYear, final int alMonth, final int alDay, final int alHour, final int alMinute){
        long AID;
        ContentValues cv = new ContentValues();
        cv.put("GID", GID);
        if (IID != -1) cv.put("IID", IID);
        cv.put("YEAR", alYear);
        cv.put("MONTH", alMonth);
        cv.put("DAY", alDay);
        cv.put("HOUR", alHour);
        cv.put("MINUTE", alMinute);
        AID = db.insert(ALARM_TABLE, null, cv);
        Log.d(TAG, "ID inserted was: " + AID);
        return (int) AID;
    }
    public void removeAlarm(final int AID){
        db.delete(ALARM_TABLE, "AID=" + AID, null);
    }
    public List<Alarm> getAlarms(final int ID, final int from){
        // list paramaters are ID - for the ID to search and and from - for the column to search in
        // 0 for Group, 1 for item
        List<Alarm> alarmList = new ArrayList<>();
        Cursor c;
        if (from == 0) { // IF 0 CAME FROM GROUP IF 1 CAME FROM ITEM
            c = db.rawQuery("SELECT * FROM " + ALARM_TABLE + " WHERE GID=" + ID + ";", null);
            if (c == null) {Log.d(TAG, "list returned empty");   return alarmList;}
        }   else {
            c = db.rawQuery("SELECT * FROM " + ALARM_TABLE + " WHERE IID=" + ID + ";", null);
            if (c == null) return alarmList;
        }
            c.moveToFirst();
            while (!c.isAfterLast()) {
                Alarm a = new Alarm();
                a.GID = c.getInt(0);
                a.IID = c.getInt(1);
                a.year = c.getInt(2);
                a.month = c.getInt(3);
                a.day = c.getInt(4);
                a.hour = c.getInt(5);
                a.minute = c.getInt(6);
                a.AID = c.getInt(7);
                alarmList.add(a);
                c.moveToNext();
            }
        Log.d(TAG, alarmList.toString());
        return alarmList;
    }
    public List<Alarm> getALLAlarms(){
        List<Alarm> alarmList = new ArrayList<>();
        Cursor c;
        c = db.rawQuery("SELECT * FROM " + ALARM_TABLE + ";", null);
        if (c == null) return alarmList;
        c.moveToFirst();
        while (!c.isAfterLast()) {
            Alarm a = new Alarm();
            a.GID = c.getInt(0);
            a.IID = c.getInt(1);
            a.year = c.getInt(2);
            a.month = c.getInt(3);
            a.day = c.getInt(4);
            a.hour = c.getInt(5);
            a.minute = c.getInt(6);
            a.AID = c.getInt(7);
            alarmList.add(a);
            c.moveToNext();
        }
        return alarmList;
    }

    //groups functions
    // get a list of Group object from the database for display headings
    public List<Group> getGroups(){
        List<Group> listData = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM " + GROUP_TABLE +";", null);
        if (c == null) return listData; // if no groups to get return empty list

        c.moveToFirst();
        while (!c.isAfterLast()){
            Group g = new Group();
            g._id = c.getInt(0);
            g.name = c.getString(1);
            listData.add(g);
            c.moveToNext();
        }

        return listData;
    }
    public String getGroupName(final int GID){
        Cursor c = db.rawQuery("SELECT * FROM " + GROUP_TABLE + " WHERE _ID=" + GID, null);
        c.moveToFirst();
        return c.getString(1);
    }
    public void addGroup(final Group grp){
        ContentValues cv = new ContentValues();
        cv.put("NAME", grp.name);
        db.insert(GROUP_TABLE, null, cv);
    }
    public void removeGroup(final int grpID){
        // delete all children
        db.delete(ITEMS_TABLE, "GROUP_ID=" + grpID, null);
        // delete all Alarm with Group
        db.delete(ALARM_TABLE, "GID=" + grpID, null);
        // @TODO delete any pending Alarm...
        // delete all location with Group
        db.delete(LOC_TABLE, "GID=" + grpID, null);
        // @TODO delete any pending geofences....
        // delete Group
        db.delete(GROUP_TABLE, "_ID=" + grpID, null);
    }

    // get a hash with Group item keys that match the list taken as the parameter
    public HashMap<Group, List<ListItem>> getItems(List<Group> g){
        HashMap<Group, List<ListItem>> childMap = new HashMap<Group, List<ListItem>>();
        List<ListItem> il;
        Cursor c;

        for (int i = 0; i < g.size(); i++){
            c = db.rawQuery("SELECT * FROM "+ ITEMS_TABLE + " WHERE GROUP_ID =" + g.get(i)._id, null);
            if (c == null) break;
            il = new ArrayList<>();
            c.moveToFirst();
            while (!c.isAfterLast()) {
                ListItem item = new ListItem();
                item._id = c.getInt(0);
                item.groupID = c.getInt(1);
                item.name = c.getString(2);
                item.finished = c.getInt(3);
                item.has_extra = c.getInt(4);
                item.notes = c.getString(5);
                item.image = c.getString(6);
                il.add(item);
                c.moveToNext();
            }
            Collections.sort(il, ListItem.ByName);
            childMap.put(g.get(i), il);
        }

        return childMap;
    }

    //Item Functions
    public String getItemName(final int IID){
        Cursor c = db.rawQuery("SELECT * FROM "+ ITEMS_TABLE + " WHERE _ID=" + IID, null);
        if (c == null) return "Shits Broke";
        c.moveToFirst();
        return c.getString(2);
    }
    public void addItem(final int groupID, final ListItem item){
        ContentValues cv = new ContentValues();

        cv.put("GROUP_ID", groupID);
        cv.put("NAME", item.name);
        cv.put("FINISHED", item.finished);
        cv.put("HAS_EXTRA", item.has_extra);
        cv.put("NOTES", item.notes);
        cv.put("IMAGE", item.image);

        db.insert(ITEMS_TABLE, null, cv);
    }
    public void removeItem(final int itemID){
        db.delete(ITEMS_TABLE, "_ID="+itemID, null); // delete the item from items
        db.delete(ALARM_TABLE, "IID="+itemID, null); // delete any id that matches the items
        db.delete(LOC_TABLE, "IID="+itemID, null); // delete and location that matches the item
        // @TODO remove any item specific Alarm and geofences
    }
    public boolean itemToggleFinished(final ListItem thisItem){ // will return true if the item was given the finished value, false if it was changed to unfinished
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
