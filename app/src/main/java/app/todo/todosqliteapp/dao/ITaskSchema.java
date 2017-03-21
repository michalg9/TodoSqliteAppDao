package app.todo.todosqliteapp.dao;

/**
 * Created by tudelft on 3/21/2017.
 */

public interface ITaskSchema {
    String TASK_TABLE = "tasks";
    String COLUMN_ID = "_id";
    String COLUMN_TITLE = "title";
    String USER_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + TASK_TABLE
            + " ("
            + COLUMN_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_TITLE
            + " TEXT NOT NULL"
            + ")";

    String[] USER_COLUMNS = new String[] { COLUMN_ID,
            COLUMN_TITLE };
}