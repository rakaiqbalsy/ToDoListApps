package com.bso.informatika.todolistapps.activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;


import com.bso.informatika.todolistapps.R;
import com.bso.informatika.todolistapps.db.TaskContract;
import com.bso.informatika.todolistapps.db.TaskDBHelper;
public class MainActivity extends AppCompatActivity {

    private ListAdapter listAdapter;
    private TaskDBHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        refreshList();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTask();
            }
        });
    }

    public void addTask () {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tambah Task");
        builder.setMessage("Apa yang ingin anda lakukan ?");
        final EditText inputField = new EditText(this);
        builder.setView(inputField);
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String task = inputField.getText().toString();

                helper = new TaskDBHelper(MainActivity.this);
                SQLiteDatabase db = helper.getWritableDatabase();
                ContentValues values = new ContentValues();

                values.clear();
                values.put(TaskContract.Columns.TASK, task);

                db.insertWithOnConflict(TaskContract.TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                refreshList();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }
    private void refreshList() {
        helper = new TaskDBHelper(MainActivity.this);
        SQLiteDatabase sqlDB = helper.getReadableDatabase();
        Cursor cursor = sqlDB.query(TaskContract.TABLE,
                new String[]{TaskContract.Columns.ID, TaskContract.Columns.TASK},
                null, null, null, null, null);

        listAdapter = new SimpleCursorAdapter(
                this,
                R.layout.item_task,
                cursor,
                new String[]{TaskContract.Columns.TASK},
                new int[]{R.id.list},
                0
        );
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(listAdapter);

    }

    public void deleteTask(View v){
        View view = (View) v.getParent();
        TextView taskTextView = (TextView) view.findViewById(R.id.list);
        String task = taskTextView.getText().toString();


        String sql = String.format("DELETE FROM %s WHERE %s = '%s'",
                TaskContract.TABLE, TaskContract.Columns.TASK, task);


        helper = new TaskDBHelper(MainActivity.this);
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        sqlDB.execSQL(sql);
        refreshList();
    }

    public void editTask(View v){
        View view = (View) v.getParent();
        TextView taskUpdateView = (TextView) view.findViewById(R.id.list);
        final int id = taskUpdateView.getId();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Data");
        final EditText inputField = new EditText(this);
        builder.setView(inputField);
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String update = inputField.getText().toString();

                String sql = String.format("REPLACE INTO %s WHERE %s VALUES %s", TaskContract.TABLE, TaskContract.Columns.TASK, update);

                helper = new TaskDBHelper(MainActivity.this);
                SQLiteDatabase db = helper.getWritableDatabase();
                //db.execSQL(sql);
                ContentValues values = new ContentValues();

                values.clear();
                values.put(TaskContract.Columns.ID, id);
                values.put(TaskContract.Columns.TASK, update);
                db.replace(TaskContract.TABLE, null, values);
                refreshList();
            }
        });

        builder.setNegativeButton("Cancel", null);

        builder.create().show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_task:
                Log.d("MainActivity","Add a new task");
                return true;

            default:
                return false;
        }
    }
}