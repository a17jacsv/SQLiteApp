package org.brohede.marcus.sqliteapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by marcus on 2018-04-25.
 */

public class MountainReaderDbHelper extends SQLiteOpenHelper {

    public MountainReaderDbHelper(Context context) {
        super(context, "databas", null, 1);
    }
    public void onCreate(SQLiteDatabase db) {
        Log.d("jacobsdata", MountainReaderContract.SQL_CREATE_ENTRIES);
        db.execSQL(MountainReaderContract.SQL_CREATE_ENTRIES);

    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        //db.execSQL(SQL_DELETE_ENTRIES);
        //onCreate(db);
    }
}

// TODO: You need to add member variables and methods to this helper class
// See: https://developer.android.com/training/data-storage/sqlite.html#DbHelper