package app.todo.todosqliteapp.dao;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by tudelft on 3/21/2017.
 */

public class Database {

    private static final String TAG = "Database";
    private static final String DATABASE_NAME = "tasksDb";
    private DatabaseHelper mDbHelper;

    // Increment DB Version on any schema change
    private static final int DATABASE_VERSION = 1;
    private final Context mContext;
    public static TaskDao mTaskDao;

    public Database open() throws SQLException {
        mDbHelper = new DatabaseHelper(mContext);
        SQLiteDatabase mDb = mDbHelper.getWritableDatabase();

        mTaskDao = new TaskDao(mDb);

        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public Database(Context context) {
        this.mContext = context;
    }


    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(ITaskSchema.USER_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion) {
            Log.w(TAG, "Upgrading database from version "
                    + oldVersion + " to "
                    + newVersion + " which destroys all old data");

            db.execSQL("DROP TABLE IF EXISTS "
                    + ITaskSchema.TASK_TABLE);
            onCreate(db);

        }
    }

}
