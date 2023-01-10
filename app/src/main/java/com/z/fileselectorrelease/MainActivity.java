package com.z.fileselectorrelease;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.z.fileselectorlib.FileSelectorSettings;
import com.z.fileselectorlib.FileSelectorTheme;
import com.z.fileselectorlib.Objects.FileInfo;
import com.z.fileselectorlib.Utils.FileUtil;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FileSelectorTheme theme = new FileSelectorTheme();
        theme.setThemeColor(getColor(R.color.colorPrimary));

        FileSelectorSettings settings=new FileSelectorSettings();
        settings.setRootPath(FileSelectorSettings.getSystemRootPath() + "/Android/data")
                .setTheme(theme)
                .setMaxFileSelect(2)
                .setTitle("请选择文件夹")
                .setFileTypesToSelect(FileInfo.FileType.File, FileInfo.FileType.Text)
                .setFileListRequestCode(544)
                .setCustomizedIcons(new String[]{".apk"},this,R.mipmap.file_custom_apk)
                .setMoreOptions(new String[]{"新建文件夹", "删除文件"},
                        (activity, position, currentPath, FilePathSelected) -> {
                            File Folder =new File(currentPath,"新文件夹");
                            if(!Folder.exists()){
                                Folder.mkdir();
                            }
                        }, (activity, position, currentPath, FilePathSelected) -> {
                            if (FilePathSelected!=null){
                                for (String path :
                                        FilePathSelected) {
                                    File delFile=new File(path);
                                    delFile.delete();
                                }
                            }
                        })
                .show(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==FileSelectorSettings.FILE_LIST_REQUEST_CODE && resultCode==FileSelectorSettings.BACK_WITH_SELECTIONS){
            assert data != null;
            Bundle bundle=data.getExtras();
            assert bundle != null;
            ArrayList<String> FilePathSelected
                    =bundle.getStringArrayList(FileSelectorSettings.FILE_PATH_LIST_REQUEST);
            for (String file_path :
                    FilePathSelected) {
                FileInfo.AccessType accessType = FileInfo.judgeAccess(file_path);
                switch(accessType){
                    case Open:
                        File file = new File(file_path);
                        break;
                    case Protected:
                        DocumentFile documentFile = FileUtil.getDocumentFilePath(this,file_path);
                        break;
                    default:
                }
                Log.v("file_sel", file_path);
            }
        }
    }
}
