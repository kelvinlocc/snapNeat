package com.accordhk.SnapNEat.sqlhelpers;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.accordhk.SnapNEat.utils.Constants;

/**
 * Created by jm on 8/2/16.
 */
public class RestaurantSQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "Restaurant";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_LOCATION = "location";
    public static final String COL_LATITUDE = "latitude";
    public static final String COL_LONGITUDE = "longitude";

    public static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME + "("
            + COL_ID + " integer primary key, "
            + COL_NAME + " string not null, "
            + COL_LOCATION + " string not null, "
            + COL_LATITUDE + " float null, "
            + COL_LONGITUDE + " float null"
            + ");";

    public RestaurantSQLiteHelper(Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
    }

    public RestaurantSQLiteHelper(Context context, DatabaseErrorHandler errorHandler) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
