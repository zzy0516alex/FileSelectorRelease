package com.z.fileselectorlib.bean;

public class FileInfo {
    public enum FileType{Folder,Video,Audio,Image,Unknown}
    private String FileName;
    private int SubFileNum;
    private String FileLastUpdateTime;
    private String FilePath;
    private FileType fileType;

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public int getSubFileNum() {
        if (fileType== FileType.Folder)
            return SubFileNum;
        else return 0;
    }

    public void setSubFileNum(int subFileNum) {
        SubFileNum = subFileNum;
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

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }
}
