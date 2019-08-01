package com.iwiz.logsapp;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import timber.log.Timber;

public class FileLoggingTree extends Timber.DebugTree {

    @Override
    protected void log(int priority, String tag, @NotNull String message, Throwable t) {
        try {
            String path = "Log";
            String fileNameTimeStamp = new SimpleDateFormat("dd-MM-yyyy",
                    Locale.getDefault()).format(new Date());
            String logTimeStamp = new SimpleDateFormat("E MMM dd yyyy 'at' hh:mm:ss:SSS aaa",
                    Locale.getDefault()).format(new Date());
            String fileName = fileNameTimeStamp + ".html";

            // Create file
            File file  = generateFile(path, fileName);

            // If file created or exists save logs
            if (file != null ) {
                FileWriter writer = new FileWriter(file, true);

                if (null!=t){
                    writer.append("<p style=\"background:lightgray;\"><strong "
                            + "style=\"background:lightblue;\">&nbsp&nbsp")
                            .append(logTimeStamp)
                            .append(" :&nbsp&nbsp</strong><strong>&nbsp&nbsp")
                            .append(tag)
                            .append("</strong> - ")
                            .append(message)
                            .append("</p>")
                            .append(t.toString())
                    ;
                }
                writer.append("<p style=\"background:lightgray;\"><strong "
                        + "style=\"background:lightblue;\">&nbsp&nbsp")
                        .append(logTimeStamp)
                        .append(" :&nbsp&nbsp</strong><strong>&nbsp&nbsp")
                        .append(tag)
                        .append("</strong> - ")
                        .append(message)
                        .append("</p>")
                ;
                writer.flush();
                writer.close();
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    protected String createStackElementTag(@NotNull StackTraceElement element) {
        // Add log statements line number to the log
        return super.createStackElementTag(element) + " - " + element.getLineNumber();
    }

    /*  Helper method to create file*/
    @Nullable
    private static File generateFile(@NonNull String path, @NonNull String fileName) {
        File file = null;
        if (isExternalStorageAvailable()) {
            File root =  new File(Environment.getExternalStorageDirectory() ,"/Logs/appLogs");


            boolean dirExists = true;

            if (!root.exists()) {
                dirExists = root.mkdirs();

            }

            if (dirExists) {
                file = new File(root, fileName);
            }
        }

        return file;

    }

    /* Helper method to determine if external storage is available*/
    private static boolean isExternalStorageAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());

    }
}
