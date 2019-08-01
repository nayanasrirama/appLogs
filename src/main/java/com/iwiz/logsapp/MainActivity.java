package com.iwiz.logsapp;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Timber.i("MainActivity : oncreate()");

        toDeleteOldLogs();
    }

    private void toDeleteOldLogs() {
        File root = new File(Environment.getExternalStorageDirectory(), "/Logs/appLogs");
        File[] files = root.listFiles();
        try {
            for (File file : files) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                long modifiedTime;
                modifiedTime = simpleDateFormat.parse(file.getName().substring(0, 10)).getTime();
                long currentTime = Calendar.getInstance().getTimeInMillis();
                long SECOND_MILLIS = 1000;
                long MINUTE_MILLIS = 60 * SECOND_MILLIS;
                long HOUR_MILLIS = 60 * MINUTE_MILLIS;
                long DAY_MILLIS = 24 * HOUR_MILLIS;

                long diff = currentTime - modifiedTime;
                long diffDays = diff / DAY_MILLIS;

                if (Math.abs(diffDays) > 1) {
                    if (file.exists()) {
                        file.delete();
                    }
                }
            }
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

    }
}
