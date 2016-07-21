package com.accordhk.SnapNEat.sqlhelpers;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.accordhk.SnapNEat.utils.Constants;

/**
 * Created by jm on 8/2/16.
 */
public class HotSearchFilterSQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "HotSearchFilter";
    public static final String COL_PK_ID = "pkId";
    public static final String COL_ID = "id";
    public static final String COL_CATEGORY = "category";
    public static final String COL_VALUE = "cValue";
    public static final String COL_FROM = "cFrom";
    public static final String COL_TO = "cTo";
    public static final String COL_TYPE = "type";
    public static final String COL_IS_SHOWN = "isShown";

    public static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME + "("
            + COL_PK_ID + " integer primary key, "
            + COL_ID + " integer not null, "
            + COL_CATEGORY + " integer not null, "
            + COL_VALUE + " text not null, "
            + COL_FROM + " float null, "
            + COL_TO + " float null, "
            + COL_TYPE + " int not null, "
            + COL_IS_SHOWN + " int not null"
            + ");";

    public HotSearchFilterSQLiteHelper(Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
    }

    public HotSearchFilterSQLiteHelper(Context context, DatabaseErrorHandler errorHandler) {
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
