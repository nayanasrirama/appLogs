package com.iwiz.logsapp;

import android.app.Application;

import timber.log.Timber;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        /*Timber initialization*/
        Timber.plant(new Timber.DebugTree());
        Timber.plant(new FileLoggingTree());
        
        /*set up the exception handler*/
        setUpCrashHandler();

    }

    private void setUpCrashHandler() {
        Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Timber.e(e);
            }
        };
        Thread.UncaughtExceptionHandler fabricExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();


        Thread.setDefaultUncaughtExceptionHandler(new AppExceptionHandler(handler, fabricExceptionHandler, this));
    }

}
