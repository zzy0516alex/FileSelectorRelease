package com.z.fileselectorlib;

import android.graphics.Color;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.core.graphics.ColorUtils;

public class FileSelectorTheme {
    //Global
    private int themeColor;//主题颜色,包含标题栏和状态栏
    //Top ToolBar
    private int topToolBarBackIcon;//顶部返回按钮 (Resource ID)
    private int topToolBarTitleColor;//顶部标题及提示文本颜色 (Color-int)
    private int topToolBarTitleSize;//顶部标题及提示文本大小 (sp)
    private int topToolBarMenuIcon;//顶部菜单按钮 (Resource ID)
    //Navigation Bar
    private int naviBarTextColor;//导航栏字体颜色 (Color-int)
    private int naviBarTextSize;//导航栏文本大小 (sp)
    private int naviBarArrowIcon;//导航栏分隔箭头 (Resource ID)
    //File List
    private int fileNameColor;//文件(夹)名称字体颜色 (Color-int)
    private int fileNameSize;//文件(夹)名称字体大小 (sp)
    private int fileInfoColor;//文件信息提示字体颜色 (Color-int)
    private int fileInfoSize;//文件信息提示字体大小 (sp)
    private int checkboxDrawable;//文件选择框的样式 (Drawable Resource ID)
    private boolean replaceCheckboxBackground;

    public FileSelectorTheme() {
        this.topToolBarBackIcon = R.mipmap.fs_back_arrow;
        this.themeColor = Color.parseColor("#1E90FF");
        this.topToolBarTitleColor = Color.parseColor("#FFFFFF");
        this.topToolBarTitleSize = 22;
        this.topToolBarMenuIcon = R.mipmap.fs_menu;

        this.naviBarTextColor = Color.parseColor("#000000");
        this.naviBarTextSize = 16;
        this.naviBarArrowIcon = R.mipmap.fs_next;

        this.fileNameColor = Color.parseColor("#000000");
        this.fileNameSize = 18;
        this.fileInfoColor = Color.parseColor("#A9A9A9");
        this.fileInfoSize = 15;
        this.replaceCheckboxBackground = false;
    }

    public int getThemeColor() {
        return themeColor;
    }

    public FileSelectorTheme setThemeColor(int themeColor) {
        this.themeColor = themeColor;
        int default_color = Color.parseColor("#FFFFFF");
        if(this.topToolBarTitleColor == default_color){
            if (ColorUtils.calculateLuminance(themeColor)>0.5) {
                this.setTopToolBarTitleColor("#000000");
            }
            else {
                this.setTopToolBarTitleColor("#FFFFFF");
            }
        }
        return this;
    }

    public FileSelectorTheme setThemeColor(String themeColorString) {
        int theme_color = Color.parseColor(themeColorString);
        setThemeColor(theme_color);
        return this;
    }

    public int getTopToolBarBackIcon() {
        return topToolBarBackIcon;
    }

    public FileSelectorTheme setTopToolBarBackIcon(@DrawableRes int topToolBarBackIcon) {
        this.topToolBarBackIcon = topToolBarBackIcon;
        return this;
    }

    public int getTopToolBarTitleColor() {
        return topToolBarTitleColor;
    }

    public FileSelectorTheme setTopToolBarTitleColor(int topToolBarTitleColor) {
        this.topToolBarTitleColor = topToolBarTitleColor;
        return this;
    }

    public FileSelectorTheme setTopToolBarTitleColor(String titleColorString) {
        this.topToolBarTitleColor = Color.parseColor(titleColorString);
        return this;
    }

    public int getTopToolBarTitleSize() {
        return topToolBarTitleSize;
    }

    public FileSelectorTheme setTopToolBarTitleSize(int topToolBarTitleSize) {
        this.topToolBarTitleSize = topToolBarTitleSize;
        return this;
    }

    public int getTopToolBarMenuIcon() {
        return topToolBarMenuIcon;
    }

    public FileSelectorTheme setTopToolBarMenuIcon(@DrawableRes int topToolBarMenuIcon) {
        this.topToolBarMenuIcon = topToolBarMenuIcon;
        return this;
    }

    public int getNaviBarTextColor() {
        return naviBarTextColor;
    }

    public FileSelectorTheme setNaviBarTextColor(int naviBarTextColor) {
        this.naviBarTextColor = naviBarTextColor;
        return this;
    }

    public FileSelectorTheme setNaviBarTextColor(String textColorString) {
        this.naviBarTextColor = Color.parseColor(textColorString);
        return this;
    }

    public int getNaviBarTextSize() {
        return naviBarTextSize;
    }

    public FileSelectorTheme setNaviBarTextSize(int naviBarTextSize) {
        this.naviBarTextSize = naviBarTextSize;
        return this;
    }

    public int getNaviBarArrowIcon() {
        return naviBarArrowIcon;
    }

    public FileSelectorTheme setNaviBarArrowIcon(@DrawableRes int naviBarArrowIcon) {
        this.naviBarArrowIcon = naviBarArrowIcon;
        return this;
    }

    public int getFileNameColor() {
        return fileNameColor;
    }

    public FileSelectorTheme setFileNameColor(int fileNameColor) {
        this.fileNameColor = fileNameColor;
        return this;
    }

    public FileSelectorTheme setFileNameColor(String fileNameColorString) {
        this.fileNameColor = Color.parseColor(fileNameColorString);
        return this;
    }

    public int getFileNameSize() {
        return fileNameSize;
    }

    public FileSelectorTheme setFileNameSize(int fileNameSize) {
        this.fileNameSize = fileNameSize;
        return this;
    }

    public int getFileInfoColor() {
        return fileInfoColor;
    }

    public FileSelectorTheme setFileInfoColor(int fileInfoColor) {
        this.fileInfoColor = fileInfoColor;
        return this;
    }

    public FileSelectorTheme setFileInfoColor(String fileInfoColorString) {
        this.fileInfoColor = Color.parseColor(fileInfoColorString);
        return this;
    }

    public int getFileInfoSize() {
        return fileInfoSize;
    }

    public FileSelectorTheme setFileInfoSize(int fileInfoSize) {
        this.fileInfoSize = fileInfoSize;
        return this;
    }

    public int getCheckboxDrawable() {
        return checkboxDrawable;
    }

    public FileSelectorTheme setCheckboxDrawable(@DrawableRes int checkboxDrawable) {
        this.checkboxDrawable = checkboxDrawable;
        this.replaceCheckboxBackground = true;
        return this;
    }

    public boolean isReplaceCheckboxBackground() {
        return replaceCheckboxBackground;
    }
}
