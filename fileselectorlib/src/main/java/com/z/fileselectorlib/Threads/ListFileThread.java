package com.z.fileselectorlib.Threads;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.documentfile.provider.DocumentFile;

import com.z.fileselectorlib.Objects.BasicParams;
import com.z.fileselectorlib.Objects.FileInfo;
import com.z.fileselectorlib.Utils.FileUtil;
import com.z.fileselectorlib.Utils.TimeUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ListFileThread extends Thread {
    public static final int PROCESS_DONE = 1001;
    private Handler handler;
    private Message message;
    private CountDownLatch countDownLatch;
    private ArrayList<FileInfo> fileInfoList;
    private File[] fileList;
    private DocumentFile[] protectedFileList;
    private FileInfo.AccessType accessType;

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    public void callback(int event, Object object){
        if (handler!=null)message = handler.obtainMessage();
        if (message!=null){
            message.what = event;
            if (object!=null)message.obj = object;
            handler.sendMessage(message);
        }
    }

    public ListFileThread(File[] fileList) {
        this.fileList = fileList;
        this.accessType = FileInfo.AccessType.Open;
    }

    public ListFileThread(DocumentFile[] protectedFileList) {
        this.protectedFileList = protectedFileList;
        this.accessType = FileInfo.AccessType.Protected;
    }

    private void getOpenFileList(){
        fileInfoList = new ArrayList<>();
        for (File f : fileList) {
            if (f.getName().indexOf(".") != 0) {
                //隐藏文件不显示
                if (f.isDirectory()) {
                    //文件夹
                    FileInfo fileInfo = new FileInfo();
                    fileInfo.setFileName(f.getName());
                    fileInfo.setFileLastUpdateTime(TimeUtil.getDateInString(new Date(f.lastModified())));
                    fileInfo.setFileType(FileInfo.FileType.Folder);
                    fileInfo.setFilePath(f.getPath());
                    fileInfo.setFileCount(FileUtil.getSubfolderNum(f.getPath()));
                    fileInfo.setAccessType(FileInfo.judgeAccess(f.getPath()));
                    fileInfoList.add(fileInfo);
                } else {
                    if (BasicParams.getInstance().isUseFilter()) {
                        if (!FileUtil.fileFilter(f.getPath(),BasicParams.getInstance().getFileTypeFilter()))
                            continue;
                    }
                    FileInfo fileInfo = new FileInfo();
                    if (FileUtil.isAudioFileType(f.getPath())) {
                        fileInfo.setFileType(FileInfo.FileType.Audio);
                    }
                    else if (FileUtil.isImageFileType(f.getPath())){
                        fileInfo.setFileType(FileInfo.FileType.Image);
                    }
                    else if (FileUtil.isVideoFileType(f.getPath())){
                        fileInfo.setFileType(FileInfo.FileType.Video);
                    }
                    else if (FileUtil.isTextFileType(f.getPath())){
                        fileInfo.setFileType(FileInfo.FileType.Text);
                    }
                    else {
                        fileInfo.setFileType(FileInfo.FileType.Unknown);
                    }
                    fileInfo.setFileName(f.getName());
                    fileInfo.setFileLastUpdateTime(TimeUtil.getDateInString(new Date(f.lastModified())));
                    fileInfo.setFilePath(f.getPath());
                    fileInfo.setFileCount(f.length());
                    fileInfo.setAccessType(FileInfo.judgeAccess(f.getPath()));
                    fileInfoList.add(fileInfo);
                }
            }
        }
    }

    private void getProtectedFileList(){
        fileInfoList = new ArrayList<>();
        for (DocumentFile file : protectedFileList) {
            if (file.getName() == null)continue;
            if (file.getName().indexOf(".") == 0)continue;
            Log.d("Android保护文件", file.getName());
            if (file.isDirectory()) {
                FileInfo fileInfo = new FileInfo();
                fileInfo.setFileName(file.getName());
                fileInfo.setFileLastUpdateTime(TimeUtil.getDateInString(new Date(file.lastModified())));
                fileInfo.setFileType(FileInfo.FileType.Folder);
                fileInfo.setAccessType(FileInfo.AccessType.Protected);
                String path = FileUtil.changeToPath(file.getUri().toString());
                fileInfo.setFilePath(path);
                fileInfo.setFileCount(FileUtil.getSubFolderNum(file));
                fileInfoList.add(fileInfo);
            }
            else {
                String path = FileUtil.changeToPath(file.getUri().toString());
                if (BasicParams.getInstance().isUseFilter()) {
                    if (!FileUtil.fileFilter(path,BasicParams.getInstance().getFileTypeFilter()))
                        continue;
                }
                FileInfo fileInfo = new FileInfo();
                if (FileUtil.isAudioFileType(path)) {
                    fileInfo.setFileType(FileInfo.FileType.Audio);
                }
                else if (FileUtil.isImageFileType(path)){
                    fileInfo.setFileType(FileInfo.FileType.Image);
                }
                else if (FileUtil.isVideoFileType(path)){
                    fileInfo.setFileType(FileInfo.FileType.Video);
                }
                else if (FileUtil.isTextFileType(path)){
                    fileInfo.setFileType(FileInfo.FileType.Text);
                }
                else {
                    fileInfo.setFileType(FileInfo.FileType.Unknown);
                }
                fileInfo.setFileName(file.getName());
                fileInfo.setFileLastUpdateTime(TimeUtil.getDateInString(new Date(file.lastModified())));
                fileInfo.setFilePath(path);
                fileInfo.setFileCount(file.length());
                fileInfo.setAccessType(FileInfo.AccessType.Protected);
                fileInfoList.add(fileInfo);

            }
        }
    }

    @Override
    public void run() {
        super.run();
        switch(accessType){
            case Open:
                getOpenFileList();
                break;
            case Protected:
                getProtectedFileList();
                break;
            default:
        }
        callback(PROCESS_DONE,fileInfoList);
        if (countDownLatch!=null)countDownLatch.countDown();
    }

    public static class FileListHandler extends Handler{
        private FileListListener listener;

        public FileListHandler(FileListListener listener) {
            this.listener = listener;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what==PROCESS_DONE)
                listener.onFileListGenerated((ArrayList<FileInfo>) msg.obj);
        }
    }

    public interface FileListListener{
        void onFileListGenerated(ArrayList<FileInfo> fileInfoList);
    }
}
