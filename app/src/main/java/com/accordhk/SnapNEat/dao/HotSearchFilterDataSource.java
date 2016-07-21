package com.accordhk.SnapNEat.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.accordhk.SnapNEat.models.HotSearch;
import com.accordhk.SnapNEat.sqlhelpers.HotSearchFilterSQLiteHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jm on 8/2/16.
 */
public class HotSearchFilterDataSource extends BaseDataSource {

    private static String LOGGER_TAG = "HotSearchFilterDataSource";

    private SQLiteDatabase database;
    private HotSearchFilterSQLiteHelper dbHelper;
    private String[] allColumns = {
            HotSearchFilterSQLiteHelper.COL_PK_ID,
            HotSearchFilterSQLiteHelper.COL_ID,
            HotSearchFilterSQLiteHelper.COL_CATEGORY,
            HotSearchFilterSQLiteHelper.COL_VALUE,
            HotSearchFilterSQLiteHelper.COL_FROM,
            HotSearchFilterSQLiteHelper.COL_TO,
            HotSearchFilterSQLiteHelper.COL_TYPE,
            HotSearchFilterSQLiteHelper.COL_IS_SHOWN
    };

    public HotSearchFilterDataSource(Context c) {
        dbHelper = new HotSearchFilterSQLiteHelper(c);
    }

    public void open() throws Exception {
        database = dbHelper.getWritableDatabase();
    }

    public void close() throws Exception {
        dbHelper.close();
    }

    public HotSearch createRow(int pkId, int id, int category, String value, float from, float to, int type, int isShown) {
        ContentValues values = new ContentValues();
        values.put(HotSearchFilterSQLiteHelper.COL_PK_ID, pkId);
        values.put(HotSearchFilterSQLiteHelper.COL_ID, id);
        values.put(HotSearchFilterSQLiteHelper.COL_CATEGORY, category);
        values.put(HotSearchFilterSQLiteHelper.COL_VALUE, value);
        values.put(HotSearchFilterSQLiteHelper.COL_FROM, from);
        values.put(HotSearchFilterSQLiteHelper.COL_TO, to);
        values.put(HotSearchFilterSQLiteHelper.COL_TYPE, type);
        values.put(HotSearchFilterSQLiteHelper.COL_IS_SHOWN, isShown);

        database.insert(HotSearchFilterSQLiteHelper.TABLE_NAME, null, values);

        // get newly inserted row
        String where = HotSearchFilterSQLiteHelper.COL_PK_ID+"='"+pkId+"'";
        Cursor cursor = database.query(HotSearchFilterSQLiteHelper.TABLE_NAME, allColumns, where, null, null, null, null);
        cursor.moveToFirst();

        HotSearch row = cursorToObj(cursor);
        Log.d("Hotsearch row id: ", row.getId()+" row category: "+row.getCategory()+" row value: "+row.getValue()+" row shown: "+row.getIsShown());
        cursor.close();

        return row;
    }

    public List<HotSearch> getRowsByIsShown(int category, int isShown) {
        List<HotSearch> rows = new ArrayList<HotSearch>();

        String where = HotSearchFilterSQLiteHelper.COL_CATEGORY+"="+String.valueOf(category)+" AND "+HotSearchFilterSQLiteHelper.COL_IS_SHOWN+"="+String.valueOf(isShown);

        Cursor cursor = database.query(HotSearchFilterSQLiteHelper.TABLE_NAME, allColumns, where, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            HotSearch row = cursorToObj(cursor);
            rows.add(row);
            cursor.moveToNext();
        }

        cursor.close();
        return rows;
    }

    public HotSearch getRowByIdAndCategory(int id, int category) {
        // get newly inserted row
        String where = HotSearchFilterSQLiteHelper.COL_ID+"="+String.valueOf(id)+" AND "+HotSearchFilterSQLiteHelper.COL_CATEGORY+"="+String.valueOf(category);
        Cursor cursor = database.query(HotSearchFilterSQLiteHelper.TABLE_NAME, allColumns, where, null, null, null, null);
        cursor.moveToFirst();

        HotSearch row = cursorToObj(cursor);
        cursor.close();

        return row;
    }

    public void deleteRow(HotSearch row) {
        String id = String.valueOf(row.getId());

        String where = HotSearchFilterSQLiteHelper.COL_ID+"='"+id+"'";

        database.delete(HotSearchFilterSQLiteHelper.TABLE_NAME, where, null);
    }

    public void truncateTable() {
        super.truncateTable(database, HotSearchFilterSQLiteHelper.TABLE_NAME);
    }

    public void createTable() {
        super.createTable(database, HotSearchFilterSQLiteHelper.DATABASE_CREATE);
    }

    public void deleteTable() {
        super.deleteTable(database, HotSearchFilterSQLiteHelper.TABLE_NAME);
    }

    public boolean isTableExist() {
        return super.isTableExists(database, dbHelper, HotSearchFilterSQLiteHelper.TABLE_NAME);
    }

    private HotSearch cursorToObj(Cursor c) {
        HotSearch data = new HotSearch();

        data.setId(c.getInt(1));
        data.setCategory(c.getInt(2));
        data.setValue(c.getString(3));
        data.setFrom(c.getFloat(4));
        data.setTo(c.getFloat(5));
        data.setType(c.getInt(6));
        data.setIsShown(c.getInt(7));

        return data;
    }
}
