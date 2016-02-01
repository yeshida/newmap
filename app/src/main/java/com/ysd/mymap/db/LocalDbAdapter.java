package com.ysd.mymap.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;
import java.util.Calendar;

/**
 * Created by Administrator on 2016/1/27.
 */
public class LocalDbAdapter extends DbAdapter {
    private static final String TAG = "LocalDbAdapter";
    public static final String TABLE_NAME = "locates";
    public static final String ID = "_id";//主键
    public static final String TRACKID = "track_id";//跟踪的目标ID
    public static final String LON = "longitude";//维度
    public static final String LAT = "latitude";//经度
    public static final String ALT = "altitude";//偏差
    public static final String CREATED = "created_at";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private final Context mCtx;


    public LocalDbAdapter(Context mCtx) {
        this.mCtx = mCtx;
    }
    public LocalDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    public void close(){
        mDbHelper.close();
    }
    public Cursor getLocate(long rowId) throws SQLException {
        Cursor mCursor = mDb.query(true, TABLE_NAME, new String[]{ID,LON,LAT,ALT,CREATED}, ID + "=" + rowId, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    //增加
    public long createLocate(int track_id,Double longitude,Double latitude,Double altitude) {
        Log.d(TAG, "createLocate");
        ContentValues initialValues = new ContentValues();
        initialValues.put(TRACKID,track_id);
        initialValues.put(LON,longitude);
        initialValues.put(LAT,latitude);
        initialValues.put(ALT,altitude);
        Calendar calendar = Calendar.getInstance();
        String created = calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND);
        initialValues.put(CREATED,created);
        return mDb.insert(TABLE_NAME, null, initialValues);
    }
    //删除
    public boolean deleteLocate(long rowId) {
        return mDb.delete(TABLE_NAME, ID + "=" + rowId, null) > 0;
    }

    public Cursor getTrackAllLocates(int trackId) {
        return mDb.query(TABLE_NAME, new String[]{ID, TRACKID, LON, LAT, ALT, CREATED}, "track_id=?", new String[]{String.valueOf(trackId)}, null, null, "created_at asc");
    }
}
