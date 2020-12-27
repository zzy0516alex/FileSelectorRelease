package com.z.fileselectorlib;

import android.app.Activity;
import android.content.Intent;

import com.z.fileselectorlib.Objects.BasicParams;
import com.z.fileselectorlib.Objects.FileInfo;

public class FileSelectorSettings {

    private BasicParams basicParams;
    public static String FILE_PATH_LIST_REQUEST="file_path_list";
    public static int BACK_WITHOUT_SELECT=0;
    public static int BACK_WITH_SELECTIONS=1;
    public static int REQUEST_CODE=2;

    public FileSelectorSettings() {
        basicParams=BasicParams.getInitInstance();
    }

    public FileSelectorSettings setRootPath(String path){
        basicParams.setRootPath(path);
        return this;
    }

    public FileSelectorSettings setMaxFileSelect(int num){
        basicParams.setMaxSelectNum(num);
        return this;
    }

    public FileSelectorSettings setTitle(String title){
        basicParams.setTips(title);
        return this;
    }

    public FileSelectorSettings setThemeColor(String color){
        basicParams.setColor(color);
        return this;
    }
    public FileSelectorSettings setFileTypesToSelect(FileInfo.FileType ... fileTypes){
        basicParams.setFileTypes(fileTypes);
        return this;
    }

    /**
     *
     * @return /storage/emulated/0
     */
    public static String getSystemRootPath(){
        return BasicParams.BasicPath;
    }

    public void show(Activity activity){
        Intent intent=new Intent(activity,FileSelectorActivity.class);
        activity.startActivityForResult(intent,REQUEST_CODE);
    }
}
