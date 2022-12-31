package com.z.fileselectorlib.Objects;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.z.fileselectorlib.Utils.FileUtil;

import java.util.ArrayList;

public class FileList extends ViewModel {
    private MutableLiveData<ArrayList<FileInfo>> fileList;

    public MutableLiveData<ArrayList<FileInfo>> getFileList() {
        if (fileList==null){
            ArrayList<FileInfo> initList=new ArrayList<>();
            fileList=new MutableLiveData<>();
            fileList.setValue(initList);
        }
        return fileList;
    }

    public void addToResult(FileInfo fileInfo){
        ArrayList<FileInfo> new_list=fileList.getValue();
        if (new_list!=null)new_list.add(fileInfo);
        fileList.setValue(new_list);
    }
    public void addToResult(ArrayList<FileInfo> fileInfo){
        ArrayList<FileInfo> new_list=fileList.getValue();
        if (new_list!=null)new_list.addAll(fileInfo);
        fileList.setValue(new_list);
    }
    public void clear(){
        ArrayList<FileInfo>emptyList=new ArrayList<>();
        fileList.setValue(emptyList);
    }
    public void sortByName(){
        ArrayList<FileInfo> new_list=fileList.getValue();
        if(new_list.get(0).getFileType() == FileInfo.FileType.Parent) {
            FileInfo header = new_list.remove(0);
            FileUtil.SortFilesByName(new_list, true);
            new_list.add(0, header);
        }
        else FileUtil.SortFilesByName(new_list, true);
    }
}
