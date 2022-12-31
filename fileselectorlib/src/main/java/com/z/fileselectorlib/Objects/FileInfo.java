package com.z.fileselectorlib.Objects;

import com.z.fileselectorlib.Utils.FileUtil;

public class FileInfo {
    public enum AccessType{Open,Protected}
    public enum FileType{Folder,Video,Audio,Image,Text,Unknown,Parent}
    private String FileName;
    private long FileCount;//如果是文件夹则表示子目录项数,如果不是文件夹则表示文件大小，-1不显示
    private String FileLastUpdateTime;
    private String FilePath;
    private FileType fileType;
    private AccessType accessType;

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getFileCount() {
        if (fileType== FileType.Parent)
            return "";
        else if (FileCount == -1 && fileType== FileType.Folder)
            return "受保护的文件夹";
        else if (fileType== FileType.Folder)
            return "共"+ FileCount +"项";
        else {
            return FileUtil.getFileSize(FileCount);
        }
    }

    public void setFileCount(long fileCount) {
        FileCount = fileCount;
    }

    public String getFileLastUpdateTime() {
        return FileLastUpdateTime;
    }

    public void setFileLastUpdateTime(String fileLastUpdateTime) {
        FileLastUpdateTime = fileLastUpdateTime;
    }

    public String getFilePath() {
        return FilePath;
    }

    public void setFilePath(String filePath) {
        FilePath = filePath;
    }

    public FileType getFileType() {
        return fileType;
    }

    public boolean isDirectory(){
        return fileType == FileType.Folder;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public static AccessType judgeAccess(String path){
        boolean isProtectedDir = false;
        if (path.contains("Android/data"))isProtectedDir = true;
        if (path.contains("Android/obb"))isProtectedDir = true;
        return isProtectedDir?AccessType.Protected:AccessType.Open;
    }

    public AccessType getAccessType() {
        if (accessType==null){
            accessType = judgeAccess(FilePath);
        }
        return accessType;
    }

    public void setAccessType(AccessType accessType) {
        this.accessType = accessType;
    }

    public boolean FileFilter(FileType[] types){
        for (FileType type:types) {
            if (this.fileType==type)return true;
        }
        return false;
    }
}
