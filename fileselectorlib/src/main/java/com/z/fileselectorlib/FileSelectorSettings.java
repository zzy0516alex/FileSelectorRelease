package com.z.fileselectorlib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.z.fileselectorlib.Objects.BasicParams;
import com.z.fileselectorlib.Objects.FileInfo;
import com.z.fileselectorlib.Utils.PermissionUtil;

import java.io.File;
import java.util.Arrays;

public class FileSelectorSettings {

    private BasicParams basicParams;
    public static String FILE_PATH_LIST_REQUEST = "file_path_list";
    public static int BACK_WITHOUT_SELECT = 510;
    public static int BACK_WITH_SELECTIONS = 511;
    public static int FILE_LIST_REQUEST_CODE = 512;

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

    public FileSelectorSettings setTheme(FileSelectorTheme theme){
        basicParams.setTheme(theme);
        return this;
    }

    public FileSelectorSettings setFileTypesToSelect(FileInfo.FileType ... fileTypes){
        if (Arrays.asList(fileTypes).contains(FileInfo.FileType.Parent)){
            throw new IllegalArgumentException("类型不能包含parent");
        }
        else basicParams.setSelectableFileTypes(fileTypes);
        return this;
    }
    public FileSelectorSettings setFileTypesToShow(String ... extensions){
        basicParams.setFileTypeFilter(extensions);//如果extensions 为空,则只显示文件夹
        basicParams.setUseFilter(true);
        return this;
    }
    public FileSelectorSettings setCustomizedIcons(String[] extensions, Bitmap ... icons){
        if (extensions.length!=icons.length){
            throw new IllegalArgumentException("文件扩展名必须与自定义图标一一对应");
        }
        else {
            for (int i = 0; i < extensions.length; i++) {
                basicParams.addCustomIcon(extensions[i],icons[i]);
            }
        }
        return this;
    }

    public FileSelectorSettings setCustomizedIcons(String[] extensions, Context context, int ... icon_ids){
        if (extensions.length!=icon_ids.length){
            throw new IllegalArgumentException("文件扩展名必须与自定义图标一一对应");
        }
        else {
            for (int i = 0; i < extensions.length; i++) {
                Bitmap icon = BitmapFactory.decodeResource(context.getResources(), icon_ids[i]);
                basicParams.addCustomIcon(extensions[i],icon);
            }
        }
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
     * 获取系统根目录
     * @return /storage/emulated/0
     */
    public static String getSystemRootPath(){
        return BasicParams.BasicPath;
    }

    public FileSelectorSettings setFileListRequestCode(int fileListRequestCode) {
        FILE_LIST_REQUEST_CODE = fileListRequestCode;
        return this;
    }

    public void show(Activity activity){
        boolean permissionGranted = PermissionUtil.isStoragePermissionGranted(activity);
        if (!permissionGranted)
            Toast.makeText(activity, "请求文件读写权限", Toast.LENGTH_SHORT).show();
//        File[] test=(new File(basicParams.getRootPath())).listFiles();
//        if (test==null){
//            throw new IllegalArgumentException("初始路径不是一个目录");
//        }
        Intent intent=new Intent(activity,FileSelectorActivity.class);
        activity.startActivityForResult(intent, FILE_LIST_REQUEST_CODE);
    }
}
