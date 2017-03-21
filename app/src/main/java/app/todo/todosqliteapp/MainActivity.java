package app.todo.todosqliteapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


import app.todo.todosqliteapp.db.TaskContract;
import app.todo.todosqliteapp.db.TaskHelper;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    TaskHelper mHelper;
    SQLiteDatabase db;
    Cursor currentCursor = null;
    SimpleCursorAdapter cursorAdapter = null;
    ListView taskListView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHelper = new TaskHelper(this);
        taskListView = (ListView) findViewById(R.id.todo_list);

        cursorAdapter =
                new SimpleCursorAdapter(this,
                        R.layout.item_todo, currentCursor,
                        new String[] {TaskContract.TaskEntry._ID,
                                TaskContract.TaskEntry.COL_TASK_TITLE},
                        new int[] {R.id.task_id, R.id.task_title}, 0);

        taskListView.setAdapter(cursorAdapter);
        updateUI();
    }

    public void updateUI() {
        new LoadTasks().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_add_task:
                Log.d(TAG, "Action add task");

                final EditText taskEditText = new EditText(this);
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Add new task")
                        .setMessage("Jakie zadanie chcesz dodac?")
                        .setView(taskEditText)
                        .setPositiveButton("Dodaj", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String taskText = String.valueOf(taskEditText.getText());

                                ContentValues values = new ContentValues();
                                values.put(TaskContract.TaskEntry.COL_TASK_TITLE, taskText);

                                new InsertTask().execute(values);

                                Log.d(TAG, "Zadanie dodane: " + taskText);

                            }
                        })
                        .setNegativeButton("Anuluj", null)
                        .create();

                dialog.show();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void deleteTask(View view) {
        View relativeView = (View) view.getParent();
        TextView textView = (TextView)
                relativeView.findViewById(R.id.task_title);
        String taskToRemove = String.valueOf(textView.getText());

        Log.d(TAG, "usuwamy task " + taskToRemove);

        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.delete(TaskContract.TaskEntry.TABLE,
                TaskContract.TaskEntry.COL_TASK_TITLE + " = ?",
                new String[] {taskToRemove});

        db.close();
        updateUI();

        //
    }

    abstract private class BaseTask<T> extends AsyncTask<T, Void, Cursor> {

        @Override
        public void onPostExecute(Cursor result) {
            currentCursor = result;
            cursorAdapter.changeCursor(currentCursor);
            cursorAdapter.notifyDataSetChanged();
        }

        protected Cursor doQuery() {
            Cursor newCursor = mHelper.getReadableDatabase()
                    .query(TaskContract.TaskEntry.TABLE,
                            new String[] {
                                    TaskContract.TaskEntry._ID,
                                    TaskContract.TaskEntry.COL_TASK_TITLE},
                            null, null, null, null, null);

            return newCursor;
        }
    }

    private class LoadTasks extends BaseTask<Void> {
        @Override
        protected Cursor doInBackground(Void... params) {
            return(doQuery());
        }
    }

    private class InsertTask extends BaseTask<ContentValues> {
        @Override
        protected Cursor doInBackground(ContentValues... values) {

            // SQL INSERT
            mHelper.getWritableDatabase().insertWithOnConflict(
                    TaskContract.TaskEntry.TABLE,
                    null,
                    values[0],
                    SQLiteDatabase.CONFLICT_REPLACE
            );

            return(doQuery());
        }
    }

    private class DeleteTask extends BaseTask<String> {
        @Override
        protected Cursor doInBackground(String... values) {

            // SQL DELETE

            return(doQuery());
        }
    }

}
