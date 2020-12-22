package com.z.fileselectorlib;

import android.app.Activity;
import android.content.Intent;

public class FileSelectorSettings {

    private BasicParams basicParams;

    public FileSelectorSettings() {
        basicParams=BasicParams.getInitInstance();
    }

    public FileSelectorSettings setRootPath(String path){
        basicParams.setRootPath(path);
        return this;
    }
    public static void show(Activity activity){
        Intent intent=new Intent(activity,FileSelectorActivity.class);
        activity.startActivity(intent);
    }
}
