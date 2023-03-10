package com.example.clockappliction;

import static android.icu.text.DateTimePatternGenerator.DAY;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class SetTimeActivity extends AppCompatActivity implements View.OnClickListener , TimePickerDialog.OnTimeSetListener {

    private int hour,minute;
    private TextView tv_time;
    private Button btn_send;
    private EditText et_remind;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_picker);
        tv_time = findViewById(R.id.tv_time);
        findViewById(R.id.btn_time).setOnClickListener(this);
        btn_send = (Button) findViewById(R.id.btn_send);
        btn_send.setOnClickListener(this);
        et_remind = (EditText) findViewById(R.id.et_remind);
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.btn_time){
            Calendar calendar = Calendar.getInstance();
            TimePickerDialog dialog = new TimePickerDialog(this,this,
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true);
            dialog.show();
        }
        else if (view.getId()==R.id.btn_send) {
            int hours = getHours();
            int minutes =getMinute();

            Intent intent = new Intent(this, AlarmReceiver.class);
            String mes = et_remind.getText().toString();
            intent.putExtra("mes",mes);
            PendingIntent sender = PendingIntent.getBroadcast(this,0, intent,PendingIntent.FLAG_MUTABLE);

            long firstTime = SystemClock.elapsedRealtime();    // ????????????????????????????????????(??????????????????)
            long systemTime = System.currentTimeMillis();

            android.icu.util.Calendar calendar = android.icu.util.Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
// ?????????????????????????????????????????????8?????????????????????
            calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            calendar.set(android.icu.util.Calendar.MINUTE,getMinute());
            calendar.set(android.icu.util.Calendar.HOUR_OF_DAY,getHours());
            calendar.set(android.icu.util.Calendar.SECOND,0);
            calendar.set(android.icu.util.Calendar.MILLISECOND,0);
// ?????????????????????
            long selectTime = calendar.getTimeInMillis();
// ????????????????????????????????????????????????????????????????????????????????????
            if(systemTime > selectTime) {
                Toast.makeText(this," ", Toast.LENGTH_SHORT).show();//?????????????????????????????????
                calendar.add(android.icu.util.Calendar.DAY_OF_MONTH,0);
                selectTime = calendar.getTimeInMillis();
            }
// ?????????????????????????????????????????????
            long time = selectTime - systemTime;
            firstTime += time;
// ??????????????????
            AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
            manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    firstTime, DAY, sender);
            Log.i(TAG,"time ==== " + time +", selectTime ===== "
                    + selectTime + ", systemTime ==== " + systemTime +", firstTime === " + firstTime);
            Toast.makeText(this,"????????????????????????! ", Toast.LENGTH_LONG).show();

//            Intent intent = new Intent(this,RemindActivity.class);
//
//            intent.putExtra("t_hour",hours);
//            intent.putExtra("t_minute",minutes);

        }
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hours_of_day, int minute) {
        String desc = String.format("?????????????????????%d???%d???",hours_of_day,minute);
        tv_time.setText(desc);
        setHours(hours_of_day);
        setMinute(minute);
    }


    public int getHours(){
        return hour;
    }

    public int getMinute(){
        return minute;
    }
    public void setHours(int hour){
        this.hour=hour;
    }
    public void setMinute(int minute){
        this.minute=minute;
    }
}
