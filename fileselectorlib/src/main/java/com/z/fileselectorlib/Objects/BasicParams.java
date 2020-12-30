package com.z.fileselectorlib.Objects;

import android.app.Activity;
import android.os.Environment;

import java.util.ArrayList;

import static com.z.fileselectorlib.Objects.FileInfo.FileType.Audio;
import static com.z.fileselectorlib.Objects.FileInfo.FileType.Folder;
import static com.z.fileselectorlib.Objects.FileInfo.FileType.Image;
import static com.z.fileselectorlib.Objects.FileInfo.FileType.Unknown;
import static com.z.fileselectorlib.Objects.FileInfo.FileType.Video;

public class BasicParams {
    public static final String BasicPath=Environment.getExternalStorageDirectory().getAbsolutePath();
    private String RootPath;
    private int MaxSelectNum;
    private String tips;
    private String color;
    private FileInfo.FileType[] fileTypes;
    private boolean needMoreOptions;
    private String[] OptionsName;
    private OnOptionClick[] onOptionClicks;


    public String getRootPath() {
        return RootPath;
    }

    public void setRootPath(String rootPath) {
        RootPath = rootPath;
    }

    public int getMaxSelectNum() {
        return MaxSelectNum;
    }

    public void setMaxSelectNum(int maxSelectNum) {
        MaxSelectNum = maxSelectNum;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setFileTypes(FileInfo.FileType... fileTypes) {
        this.fileTypes = fileTypes;
    }

    public FileInfo.FileType[] getFileTypes() {
        return fileTypes;
    }

    public boolean isNeedMoreOptions() {
        return needMoreOptions;
    }

    public void setNeedMoreOptions(boolean needMoreOptions) {
        this.needMoreOptions = needMoreOptions;
    }

    public String[] getOptionsName() {
        return OptionsName;
    }

    public void setOptionsName(String[] optionsName) {
        OptionsName = optionsName;
    }

    public OnOptionClick[] getOnOptionClicks() {
        return onOptionClicks;
    }

    public void setOnOptionClicks(OnOptionClick[] onOptionClicks) {
        this.onOptionClicks = onOptionClicks;
    }

    public static BasicParams getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public static BasicParams getInitInstance() {
        BasicParams params= InstanceHolder.INSTANCE;
        params.setRootPath(BasicPath);
        params.setMaxSelectNum(1);
        params.setTips("请选择文件");
        params.setColor("#1E90FF");
        params.setFileTypes(Folder,Video,Audio,Image,Unknown);
        params.setNeedMoreOptions(false);
        return params;
    }

    private static final class InstanceHolder {
        private static final BasicParams INSTANCE = new BasicParams();
    }

    public interface OnOptionClick{
        void onclick(Activity activity, int position, String currentPath, ArrayList<String> FilePathSelected);
    }
}
