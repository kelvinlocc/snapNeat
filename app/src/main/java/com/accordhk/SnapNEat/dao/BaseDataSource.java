package com.accordhk.SnapNEat.dao;

/**
 * Created by jm on 8/2/16.
 */
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BaseDataSource {

    public void truncateTable(SQLiteDatabase database, String tableName){
        database.delete(tableName, null, null);
    }

    public void createTable(SQLiteDatabase database, String query){
        database.execSQL(query);
    }

    public void deleteTable(SQLiteDatabase database, String tableName){
        database.execSQL("DROP TABLE IF EXISTS " + tableName);
    }

    public boolean isTableExists(SQLiteDatabase database, SQLiteOpenHelper dbHelper, String tableName) {
        Cursor cursor = database.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+tableName+"'", null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }
}