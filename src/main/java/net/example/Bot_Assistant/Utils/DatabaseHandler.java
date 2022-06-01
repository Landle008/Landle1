package net.example.Bot_Assistant.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.example.Bot_Assistant.Model.TaskModel;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String NAME = "BOTAsisstantDatabase";
    private static final String TASK_TABLE = "toTask";
    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String STATUS = "status";
    private static final String DESCRIPTION = "description";
    private static final String DATE = "date";
    private static final String TIME = "time";
    private static final String CREATE_TASK_TABLE ="CREATE TABLE " + TASK_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TITLE + " TEXT, "+ DESCRIPTION + " TEXT, " +  DATE + " TEXT, " + TIME + " TEXT, " + STATUS + " INTEGER)";


    private SQLiteDatabase db;

    public DatabaseHandler(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TASK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TASK_TABLE);

        onCreate(db);
    }

    public void openDatabase() {
        db = this.getWritableDatabase();
    }

    public void insertTask(TaskModel task){
        ContentValues cv = new ContentValues();
        cv.put(TITLE, task.getTitle());
        cv.put(DESCRIPTION, task.getDescription());
        cv.put(DATE, task.getDate());
        cv.put(TIME, task.getTime());
        cv.put(STATUS, 0);
        db.insert(TASK_TABLE, null, cv);
    }

    public List<TaskModel> getAllTasks(){
        List<TaskModel> taskList = new ArrayList<>();
        Cursor cur = null;
        db.beginTransaction();
        try{
            cur = db.query(TASK_TABLE, null, null, null, null, null, null, null);
            if(cur != null){
                if(cur.moveToFirst()){
                    do{
                        TaskModel task = new TaskModel();
                        task.setId(cur.getInt(cur.getColumnIndex(ID)));
                        task.setTitle(cur.getString(cur.getColumnIndex(TITLE)));
                       task.setDescription(cur.getString(cur.getColumnIndex(DESCRIPTION)));
                        task.setDate(cur.getString(cur.getColumnIndex(DATE)));
                        task.setTime(cur.getString(cur.getColumnIndex(TIME)));
                        task.setStatus(cur.getInt(cur.getColumnIndex(STATUS)));
                        taskList.add(task);
                    }
                    while(cur.moveToNext());
                }
            }
        }
        finally {
            db.endTransaction();
            assert cur != null;
            cur.close();
        }
        return taskList;
    }

    public void updateStatus(int id, int status){
        ContentValues cv = new ContentValues();
        cv.put(STATUS, status);
        db.update(TASK_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void updateTitle(int id, String title) {
        ContentValues cv = new ContentValues();
        cv.put(TITLE, title);
        db.update(TASK_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void updateDescription(int id, String description) {
        ContentValues cv = new ContentValues();
        cv.put(DESCRIPTION, description);
        db.update(TASK_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
       }

    public void updateDate(int id, String date) {
        ContentValues cv = new ContentValues();
        cv.put(DATE, date);
        db.update(TASK_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void updateTime(int id, String time) {
        ContentValues cv = new ContentValues();
        cv.put(TIME, time);
        db.update(TASK_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void deleteTask(int id){

        db.delete(TASK_TABLE, ID + "= ?", new String[] {String.valueOf(id)});

    }
}
