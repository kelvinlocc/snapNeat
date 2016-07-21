package com.accordhk.SnapNEat.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.accordhk.SnapNEat.models.Restaurant;
import com.accordhk.SnapNEat.sqlhelpers.RestaurantSQLiteHelper;

/**
 * Created by jm on 8/2/16.
 */
public class RestaurantDataSource extends BaseDataSource {

    private static String LOGGER_TAG = "RestaurantDataSource";

    private SQLiteDatabase database;
    private RestaurantSQLiteHelper dbHelper;
    private String[] allColumns = {
            RestaurantSQLiteHelper.COL_ID,
            RestaurantSQLiteHelper.COL_NAME,
            RestaurantSQLiteHelper.COL_LOCATION,
            RestaurantSQLiteHelper.COL_LATITUDE,
            RestaurantSQLiteHelper.COL_LONGITUDE
    };

    public RestaurantDataSource(Context c) {
        dbHelper = new RestaurantSQLiteHelper(c);
    }

    public void open() throws Exception {
        database = dbHelper.getWritableDatabase();
    }

    public void close() throws Exception {
        dbHelper.close();
    }

    public Restaurant createRow(int id, String name, String location, float latitude, float longitude) {
        ContentValues values = new ContentValues();
        values.put(RestaurantSQLiteHelper.COL_ID, id);
        values.put(RestaurantSQLiteHelper.COL_NAME, name);
        values.put(RestaurantSQLiteHelper.COL_LOCATION, location);
        values.put(RestaurantSQLiteHelper.COL_LATITUDE, latitude);
        values.put(RestaurantSQLiteHelper.COL_LONGITUDE, longitude);

        database.insert(RestaurantSQLiteHelper.TABLE_NAME, null, values);

        // get newly inserted row
        String where = RestaurantSQLiteHelper.COL_ID+"='"+id+"'";
        Cursor cursor = database.query(RestaurantSQLiteHelper.TABLE_NAME, allColumns, where, null, null, null, null);
        cursor.moveToFirst();

        Restaurant row = cursorToObj(cursor);
        cursor.close();

        return row;
    }

    public Restaurant getRowById(int id) {
        String where = RestaurantSQLiteHelper.COL_ID+"='"+id+"'";
        Cursor cursor = database.query(RestaurantSQLiteHelper.TABLE_NAME, allColumns, where, null, null, null, null);
        cursor.moveToFirst();

        Restaurant row = cursorToObj(cursor);
        cursor.close();

        return row;
    }

    public void deleteRow(Restaurant row) {
        String id = String.valueOf(row.getId());

        String where = RestaurantSQLiteHelper.COL_ID+"='"+id+"'";

        database.delete(RestaurantSQLiteHelper.TABLE_NAME, where, null);
    }

    public void truncateTable() {
        Log.d(LOGGER_TAG, "Truncate Table");
        super.truncateTable(database, RestaurantSQLiteHelper.TABLE_NAME);
    }

    public void createTable() {
        super.createTable(database, RestaurantSQLiteHelper.DATABASE_CREATE);
    }

    public void deleteTable() {
        super.deleteTable(database, RestaurantSQLiteHelper.TABLE_NAME);
    }

    public boolean isTableExist() {
        return super.isTableExists(database, dbHelper, RestaurantSQLiteHelper.TABLE_NAME);
    }

    private Restaurant cursorToObj(Cursor c) {
        Restaurant data = new Restaurant();

        data.setId(c.getInt(0));
        data.setName(c.getString(1));
        data.setLocation(c.getString(2));
        data.setLatitude(c.getFloat(3));
        data.setLongitude(c.getFloat(4));

        return data;
    }
}
