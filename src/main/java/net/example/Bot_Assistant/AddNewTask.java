package net.example.Bot_Assistant;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import net.example.Bot_Assistant.Model.TaskModel;
import net.example.Bot_Assistant.Utils.DatabaseHandler;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class AddNewTask extends BottomSheetDialogFragment {

    public static final String TAG = "ActionBottomDialog";
    private EditText newTaskTitle;
    private EditText newTaskDescription;
    private EditText newTaskDate;
    private EditText newTaskTime;
    private Button newTaskSaveButton;
    private Button botbtn;

    private DatabaseHandler db;

    public static AddNewTask newInstance(){
        return new AddNewTask();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.DialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.new_task, container, false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newTaskTitle = Objects.requireNonNull(getView()).findViewById(R.id.title_id);
        newTaskDescription = Objects.requireNonNull(getView()).findViewById(R.id.description_id);
        newTaskDate = Objects.requireNonNull(getView()).findViewById(R.id.date_text);
        newTaskTime = Objects.requireNonNull(getView()).findViewById(R.id.time_text);
        newTaskSaveButton = getView().findViewById(R.id.addTaskButton);
        botbtn=getView().findViewById(R.id.bot_button);
        boolean isUpdate = false;

        final Bundle bundle = getArguments();
        if(bundle != null){
            isUpdate = true;
            String title = bundle.getString("title");
            String description = bundle.getString("description");
            String date = bundle.getString("date");
            String time = bundle.getString("time");
            newTaskTitle.setText(title);
            newTaskDescription.setText(description);
            newTaskDate.setText(date);
            newTaskTime.setText(time);
            assert title != null;
            assert description != null;
            assert date != null;
            assert time != null;

        }

        db = new DatabaseHandler(getActivity());
        db.openDatabase();

        botbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Search();
            }
        });

        final boolean finalIsUpdate = isUpdate;
        newTaskSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = newTaskTitle.getText().toString();
                String description = newTaskDescription.getText().toString();
                String date = newTaskDate.getText().toString();
                String time = newTaskTime.getText().toString();
                if(finalIsUpdate){
                    db.updateTitle(bundle.getInt("id"), title);
                    db.updateDescription(bundle.getInt("id"), description);
                    db.updateDate(bundle.getInt("id"), date);
                    db.updateTime(bundle.getInt("id"), time);
                }
                else {
                    TaskModel task = new TaskModel();
                    task.setTitle(title);
                    task.setDescription(description);
                    task.setDate(date);
                    task.setTime(time);
                    task.setStatus(0);
                    db.insertTask(task);
                }
                dismiss();
            }
        });
    }
    private Timer mTimer;
    private TimerTask mMyTimerTask;


    private String getCalendarUriBase(MainActivity act) {

        String calendarUriBase = null;
        Uri calendars = Uri.parse("content://calendar/calendars");
        Cursor managedCursor = null;
        try {
            managedCursor = act.managedQuery(calendars, null, null, null, null);
        } catch (Exception e) {
        }
        if (managedCursor != null) {
            calendarUriBase = "content://calendar/";
        } else {
            calendars = Uri.parse("content://com.android.calendar/calendars");
            try {
                managedCursor = act.managedQuery(calendars, null, null, null, null);
            } catch (Exception e) {
            }
            if (managedCursor != null) {
                calendarUriBase = "content://com.android.calendar/";
            }
        }
        return calendarUriBase;
    }

    public void GoogleSearch(String text){
        text = text.replaceAll("\\s+","+");
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com/search?q=" + text + "&btnI"));
        startActivity(browserIntent);
    }

//    public void setTimer(String text) {
//
//
//
//        Timer myTimer = new Timer();
//        myTimer.schedule(new TimerTask() {
//
//            @Override
//            public void run() {
////                Button startButton = findViewById(R.id.startButton);
////                startButton.setEnabled(false);
//
////                Button pauseButton = findViewById(R.id.pauseButton);
////                pauseButton.setEnabled(false);
//
//                //And handle AlertView here
//            }
//        }, 60 * 10 * 1000);
//    }
public void SetAlarm(String text){

    int text2 = Integer.parseInt(text.replaceAll("/D+",""));

    // + (text2 % 100) * 60 * 1000 parse time from string

    int etHour = (text2 / 100) * 3600 * 1000;
    int etMinute = (text2 % 100) * 60 * 1000;
    int minute, hour, day;
    Calendar cal;

    cal = new GregorianCalendar();
    cal.setTimeInMillis(System.currentTimeMillis());
    day = cal.get(Calendar.DAY_OF_WEEK);
    hour = cal.get(Calendar.HOUR_OF_DAY);
    minute = cal.get(Calendar.MINUTE);

    Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
    i.putExtra(AlarmClock.EXTRA_HOUR, hour + etHour);
    i.putExtra(AlarmClock.EXTRA_MINUTES, minute + etMinute);
    i.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
    startActivity(i);
}

//    public void SetReminder(String text){
//        int text2 = Integer.parseInt(text.replaceAll("\\D+",""));
//
//        Calendar cal = Calendar.getInstance();
//        Uri EVENTS_URI = Uri.parse(getCalendarUriBase(this) + "events");
//        ContentResolver cr = getContentResolver();
//
//// event insert
//        ContentValues values = new ContentValues();
//        values.put("calendar_id", 1);
//        values.put("title", "Reminder Title");
//        values.put("allDay", 0);
//        if(text.toLowerCase(Locale.ROOT).lastIndexOf("сек") > -1 && text.toLowerCase(Locale.ROOT).lastIndexOf("мин") > -1 && text.toLowerCase(Locale.ROOT).lastIndexOf("час") > -1 || text.toLowerCase(Locale.ROOT).lastIndexOf("sec") > -1 && text.toLowerCase(Locale.ROOT).lastIndexOf("min") > -1 && text.toLowerCase(Locale.ROOT).lastIndexOf("hour") > -1){
//            values.put("dtstart", cal.getTimeInMillis() +  ( (text2 / 10000) * 3600 + (text2 % 10000)/ 100) * 60 +  (text2 % 100) * 1000);
//        }else if(text.toLowerCase(Locale.ROOT).lastIndexOf("час") > -1 && text.toLowerCase(Locale.ROOT).lastIndexOf("мин") > -1 || text.toLowerCase(Locale.ROOT).lastIndexOf("hour") > -1 && text.toLowerCase(Locale.ROOT).lastIndexOf("min") > -1){
//            values.put("dtstart", cal.getTimeInMillis() + (text2 / 100) * 3600 + (text2 % 100) * 60 * 1000);
//        }else if(text.toLowerCase(Locale.ROOT).lastIndexOf("сек") > -1 && text.toLowerCase(Locale.ROOT).lastIndexOf("мин") > -1 || text.toLowerCase(Locale.ROOT).lastIndexOf("sec") > -1 && text.toLowerCase(Locale.ROOT).lastIndexOf("min") > -1){
//            values.put("dtstart", cal.getTimeInMillis() + (text2 / 100) * 60 + (text2 % 100) * 1000);
//        }else if(text.toLowerCase(Locale.ROOT).lastIndexOf("сек") > -1 || text.toLowerCase(Locale.ROOT).lastIndexOf("sec") > -1){
//            values.put("dtstart", cal.getTimeInMillis() +  text2 * 1000);
//        }else if(text.toLowerCase(Locale.ROOT).lastIndexOf("час") > -1 || text.toLowerCase(Locale.ROOT).lastIndexOf("hour") > -1){
//            values.put("dtstart", cal.getTimeInMillis() + text2 *  60 * 1000);
//        }else if(text.toLowerCase(Locale.ROOT).lastIndexOf("мин") > -1 || text.toLowerCase(Locale.ROOT).lastIndexOf("min") > -1){
//            values.put("dtstart", cal.getTimeInMillis() +  text2 * 1000);
//        }else{
//            //"No time provided"
//        }
//        values.put("dtend", cal.getTimeInMillis()+ 60*60*1000); // ends 60 minutes from now
//        values.put("description", "Reminder description");
//        values.put("visibility", 0);
//        values.put("hasAlarm", 1);
//        Uri event = ((ContentResolver) cr).insert(EVENTS_URI, values);
//
//// reminder insert
//        Uri REMINDERS_URI = Uri.parse(getCalendarUriBase(this) + "reminders");
//        values = new ContentValues();
//        values.put( "event_id", Long.parseLong(event.getLastPathSegment()));
//        values.put( "method", 1 );
//        values.put( "minutes", 10 );
//        cr.insert( REMINDERS_URI, values );
//    }

    public String Search() {
        String text;
        if(newTaskDescription!=null) {
            text = newTaskDescription.getText().toString();
        }
        else{
            text = "";
        }

        if(text.toLowerCase(Locale.ROOT).lastIndexOf("alarm") > -1){
            SetAlarm(text);
        }
//        else if(text.toLowerCase(Locale.ROOT).lastIndexOf("тайм") > -1 || text.toLowerCase(Locale.ROOT).lastIndexOf("time") > -1){
//            setTimer(text);
//        }
//        else if(text.toLowerCase(Locale.ROOT).lastIndexOf("счит") > -1 || text.toLowerCase(Locale.ROOT).lastIndexOf("count") > -1){
//            return "Count";
//        }
//        else if(text.toLowerCase(Locale.ROOT).lastIndexOf("напом") > -1 || text.toLowerCase(Locale.ROOT).lastIndexOf("remind") > -1){
//            SetReminder(text);
//        }
        else if(text.toLowerCase(Locale.ROOT).lastIndexOf("alarm") > -1){
            return "SetAlarm";
        }
        else{
            GoogleSearch(text);
        }
        return text;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog){
        Activity activity = getActivity();
        if(activity instanceof DialogCloseListener)
            ((DialogCloseListener)activity).handleDialogClose(dialog);
    }

}
