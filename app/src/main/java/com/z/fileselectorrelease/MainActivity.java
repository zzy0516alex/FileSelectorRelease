package com.z.fileselectorrelease;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.z.fileselectorlib.FileSelectorSettings;
import com.z.fileselectorlib.FileSelectorTheme;
import com.z.fileselectorlib.Objects.FileInfo;
import com.z.fileselectorlib.Utils.FileUtil;

import org.w3c.dom.DocumentFragment;

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
                .setFileTypesToSelect(FileInfo.FileType.Folder)
                .setFileListRequestCode(544)
                .setCustomizedIcons(new String[]{".dat"},this,R.mipmap.file_dat)
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
