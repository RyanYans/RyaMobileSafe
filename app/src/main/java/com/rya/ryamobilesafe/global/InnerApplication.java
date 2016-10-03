package com.rya.ryamobilesafe.global;

import android.app.Application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */

public class InnerApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {

                try {
                    ex.printStackTrace();

                    String path = getExternalFilesDir(null).getPath() + File.separator + "error";
                    File dir = new File(path);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    File file = new File(dir, "innerError.logx");
                    PrintWriter printWriter = new PrintWriter(file);
                    ex.printStackTrace(printWriter);

                    printWriter.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                System.exit(0);

            }
        });

    }
}
