package com.iwiz.logsapp;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import timber.log.Timber;

class AppExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Application application;
    private Thread.UncaughtExceptionHandler systemHandler, crashlyticsHandler;
    private Activity lastStartedActivity = null;
    private int startCount = 0;
    private static final String RESTARTED = "appExceptionHandler_restarted";
    private static final String LAST_EXCEPTION = "appExceptionHandler_lastException";


    AppExceptionHandler(Thread.UncaughtExceptionHandler systemhandler, Thread.UncaughtExceptionHandler crashlyticHandler, Application application){
        this.systemHandler = systemhandler;
        this.crashlyticsHandler = crashlyticHandler;
        this.application = application;
        init();
    }

    private void init(){
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                startCount++;
                lastStartedActivity = activity;
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                startCount--;
                if(startCount<=0){
                    lastStartedActivity= null;
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Timber.e(e);
        Activity activity = this.lastStartedActivity;
        if(activity!=null){
            boolean isRestarted = activity.getIntent().getBooleanExtra("appExceptionHandler_restarted",false);
            Throwable lastException = (Throwable)activity.getIntent().getSerializableExtra("appExceptionHandler_lastException");
            if(isRestarted && this.isSameExceptiom(e,lastException)){
                Timber.d("The system exception handler will handle the caught exception.");
                this.killThisProcess();
                systemHandler.uncaughtException(t,e);
            }else {
                this.killThisProcess();
                crashlyticsHandler.uncaughtException(t,e);
                Intent intent = activity.getIntent();
                intent.putExtra(RESTARTED,true);
                intent.putExtra(LAST_EXCEPTION,e);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(intent);
                activity.finish();
            }
        }else {
            this.killThisProcess();
            crashlyticsHandler.uncaughtException(t,e);
            systemHandler.uncaughtException(t,e);
        }

    }

    private Boolean isSameExceptiom(Throwable originalException, Throwable lastException){
        if(lastException == null){
            return false;
        }
        return originalException.getClass().equals(lastException.getClass()) &&
                originalException.getMessage().equals(lastException.getMessage()) && originalException.getStackTrace() == lastException.getStackTrace() ;
    }

    private void killThisProcess() {
        Process.killProcess(Process.myPid());
        System.exit(10);
    }
}
