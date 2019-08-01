package com.iwiz.logsapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

import timber.log.Timber;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class SplashScreen extends AppCompatActivity {
    
    ArrayList<String> permissions = new ArrayList<>();
    int REQUEST_ID_MULTIPLE_PERMISSIONS = 2;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);  // Need full screen without title bars for splash screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
        setContentView(R.layout.activity_splash_screen);
        checkaAllPermissions();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean checkaAllPermissions() {
        int storagepermissionwrite = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int storagepermissionread = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);

        if(storagepermissionwrite != PackageManager.PERMISSION_GRANTED){
            permissions.add(WRITE_EXTERNAL_STORAGE);
        }
        if(storagepermissionread != PackageManager.PERMISSION_GRANTED){
            permissions.add(READ_EXTERNAL_STORAGE);
        }

        if(!permissions.isEmpty()){
            ActivityCompat.requestPermissions(this, new String[]
                    {
                            READ_EXTERNAL_STORAGE,
                            WRITE_EXTERNAL_STORAGE
                    },1);
            return false;
        }else {
            goToMainActivity();
        }
        return true;
    }

    public void goToMainActivity(){
        Thread t = new Thread(){
            public void run(){
                try {
                    Timber.i("Moving to Main screen from splash screen .");
                    Thread.sleep(5000);  // change the time according to your needs(its in milliseconds)
                    Intent i = new Intent(getApplicationContext(),MainActivity.class);     // change the activity you want to load
                    startActivity(i);
                    finish();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0) {

                boolean phonePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (phonePermission) {
                    goToMainActivity();
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();

                }
            }
        }
    }

}
