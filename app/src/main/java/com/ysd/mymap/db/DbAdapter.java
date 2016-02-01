package com.ysd.mymap.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Administrator on 2016/1/26.
 */
public class DbAdapter {
    private static final String TAG = "DbAdapter";
    private static final String DATABASE_NAME = "iTracks.db";//数据库名称
    private static final int DATABASE_VERSION = 1;//数据库版本
    public class DatabaseHelper extends SQLiteOpenHelper{
        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String tracks_sql = "CREATE TABLE "+TrackDbAdapter.TABLE_NAME+"("
                    +TrackDbAdapter.ID+" INTEGER primary key autoincrement,"
                    +TrackDbAdapter.NAME+" text not null,"
                    +TrackDbAdapter.DESC+" text,"
                    +TrackDbAdapter.DIST+" lONG,"
                    +TrackDbAdapter.TRACKEDTIME+" LONG,"
                    +TrackDbAdapter.LOCATE_COUNT+" INTEGER,"
                    +TrackDbAdapter.CREATED+" text,"
                    +TrackDbAdapter.AVGSPEED+" LONG,"
                    +TrackDbAdapter.MAXSPEED+" LONG,"
                    +TrackDbAdapter.UPDATED+" text"
                    +");";
            Log.i(TAG, tracks_sql);
            db.execSQL(tracks_sql);
            String locats_sql = "CREATE TABLE "+LocalDbAdapter.TABLE_NAME+"("
                    +LocalDbAdapter.ID+" INTEGER primary key autoincrement,"
                    +LocalDbAdapter.TRACKID+" INTEGER not null,"
                    +LocalDbAdapter.LON+" DOUBLE,"
                    +LocalDbAdapter.LAT+" DOUBLE,"
                    +LocalDbAdapter.ALT+" DOUBLE,"
                    +LocalDbAdapter.CREATED+" text"
                    +");";
            Log.i(TAG, locats_sql);
            db.execSQL(locats_sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS"+LocalDbAdapter.TABLE_NAME+";");
            db.execSQL("DROP TABLE IF EXISTS"+LocalDbAdapter.TABLE_NAME+";");
            onCreate(db);
        }
    }
}
