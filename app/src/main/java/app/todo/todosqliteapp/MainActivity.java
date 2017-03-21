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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import app.todo.todosqliteapp.dao.Database;
import app.todo.todosqliteapp.dao.Task;
import app.todo.todosqliteapp.db.TaskContract;
import app.todo.todosqliteapp.db.TaskHelper;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static Database mDb;
    ListView taskListView = null;
    ArrayAdapter<String> taskListAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDb = new Database(this);
        mDb.open();

        taskListAdapter = new ArrayAdapter<String>(this, R.layout.item_todo);
        taskListView = (ListView) findViewById(R.id.todo_list);

        taskListView.setAdapter(taskListAdapter);

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

                                Task task = new Task();
                                task.title = taskText;

                                new InsertTask().execute(task);

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

        mDb.mTaskDao.deleteTaskByTitle(taskToRemove);

        updateUI();

        //
    }

    abstract private class BaseTask<T> extends AsyncTask<T, Void, List<Task>> {

        @Override
        public void onPostExecute(List<Task> result) {
            List<String> titleList = new ArrayList<>();
            for (Task task :
                 result) {
                titleList.add(task.title);
            }
            taskListAdapter.clear();
            taskListAdapter.addAll(titleList);
            taskListAdapter.notifyDataSetChanged();

        }

        protected List<Task> doQuery() {
            return mDb.mTaskDao.fetchAllTasks();
        }
    }

    private class LoadTasks extends BaseTask<Void> {
        @Override
        protected List<Task> doInBackground(Void... params) {
            return(doQuery());
        }
    }

    private class InsertTask extends BaseTask<Task> {
        @Override
        protected List<Task> doInBackground(Task... values) {



            mDb.mTaskDao.addTask(values[0]);

            return(doQuery());
        }
    }

    private class DeleteTask extends BaseTask<String> {
        @Override
        protected List<Task> doInBackground(String... values) {

            // SQL DELETE

            return(doQuery());
        }
    }

}
