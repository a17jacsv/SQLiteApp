package org.brohede.marcus.sqliteapp;

import android.provider.BaseColumns;

/**
 * Created by marcus on 2018-04-25.
 */

public class MountainReaderContract {
    // This class should contain your database schema.
    // See: https://developer.android.com/training/data-storage/sqlite.html#DefineContract

    private MountainReaderContract() {}

    // Inner class that defines the Mountain table contents
    public static class MountainEntry implements BaseColumns {
        public static final String TABLE_NAME = "MOUNTAINFACTS";
        public static final String COLUMN_NAME_NAME = "NAME";
        public static final String COLUMN_NAME_LOCATION = "LOCATION";
        public static final String COLUMN_NAME_HEIGHT = "HEIGHT";
        public static final String COLUMN_NAME_IMAGEURL = "IMAGEURL";
        public static final String COLUMN_NAME_WIKIURL = "WIKIURL";
    }

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + MountainEntry.TABLE_NAME + " (" +
                    MountainEntry._ID + " INTEGER PRIMARY KEY," +
                    MountainEntry.COLUMN_NAME_NAME + " TEXT," +
                    MountainEntry.COLUMN_NAME_LOCATION + " TEXT," +
                    MountainEntry.COLUMN_NAME_HEIGHT + " INTEGER," +
                    MountainEntry.COLUMN_NAME_IMAGEURL + " TEXT," +
                    MountainEntry.COLUMN_NAME_WIKIURL + " TEXT" + ") ";

}
