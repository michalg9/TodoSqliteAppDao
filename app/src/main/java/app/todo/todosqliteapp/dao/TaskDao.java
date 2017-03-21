package app.todo.todosqliteapp.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class TaskDao extends DbContentProvider
    implements ITaskSchema, ITaskDao {


    private Cursor cursor;
    private ContentValues initialValues;

    public TaskDao(SQLiteDatabase db) {
        super(db);
    }

    @Override
    public Task fetchTaskById(int userId) {
        return null;
    }

    @Override
    public List<Task> fetchAllTasks() {
        List<Task> taskList = new ArrayList<Task>();
        cursor = super.query(TASK_TABLE, USER_COLUMNS, null,
                null, COLUMN_ID);

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Task task = cursorToEntity(cursor);
                taskList.add(task);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return taskList;
    }

    @Override
    public boolean addTask(Task task) {
        // set values
        setContentValue(task);
        try {
            return super.insert(TASK_TABLE, getContentValue()) > 0;
        } catch (SQLiteConstraintException ex){
            Log.w("Database", ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteTask(Task task) {
        return false;
    }

    @Override
    public boolean deleteTaskById(int taskId) {
        return false;
    }

    @Override
    public boolean deleteTaskByTitle(String title) {
        try {
            return super.delete(TASK_TABLE, COLUMN_TITLE + " = ?",
                    new String[] {title}) == 0;
        } catch (SQLiteConstraintException ex){
            Log.w("Database", ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteAllUsers() {
        return false;
    }

    @Override
    protected Task cursorToEntity(Cursor cursor) {

        Task task = new Task();

        int idIndex;
        int titleIndex;

        if (cursor != null) {
            if (cursor.getColumnIndex(COLUMN_ID) != -1) {
                idIndex = cursor.getColumnIndexOrThrow(COLUMN_ID);
                task.id = cursor.getInt(idIndex);
            }
            if (cursor.getColumnIndex(COLUMN_TITLE) != -1) {
                titleIndex = cursor.getColumnIndexOrThrow(
                        COLUMN_TITLE);
                task.title = cursor.getString(titleIndex);
            }
        }

        return task;
    }


    private void setContentValue(Task task) {
        initialValues = new ContentValues();
        //initialValues.put(COLUMN_ID, task.id);
        initialValues.put(COLUMN_TITLE, task.title);
    }

    private ContentValues getContentValue() {
        return initialValues;
    }
}
