package com.z.fileselectorlib;

import android.app.Activity;
import android.content.Intent;

import com.z.fileselectorlib.Objects.BasicParams;
import com.z.fileselectorlib.Objects.FileInfo;

import java.io.File;
import java.util.Arrays;

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
        File[] test=(new File(path)).listFiles();
        if (test==null){
            throw new IllegalArgumentException("初始路径不是一个目录或无权限");
        }
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
        if (Arrays.asList(fileTypes).contains(FileInfo.FileType.Parent)){
            throw new IllegalArgumentException("类型不能包含parent");
        }
        else basicParams.setFileTypes(fileTypes);
        return this;
    }

    public FileSelectorSettings setMoreOPtions(String[] optionsName, BasicParams.OnOptionClick...onOptionClicks){
        if (optionsName.length!=onOptionClicks.length){
            throw new IllegalArgumentException("选项名和点击响应必须一一对应");
        }
        else {
            basicParams.setNeedMoreOptions(true);
            basicParams.setOptionsName(optionsName);
            basicParams.setOnOptionClicks(onOptionClicks);
        }
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
