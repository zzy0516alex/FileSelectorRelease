package com.z.fileselectorlib;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.ColorUtils;

import com.z.fileselectorlib.Objects.BasicParams;
import com.z.fileselectorlib.Objects.FileInfo;
import com.z.fileselectorlib.Utils.FileUtil;
import com.z.fileselectorlib.Utils.PermissionUtil;
import com.z.fileselectorlib.Utils.StatusBarUtil;
import com.z.fileselectorlib.Utils.TimeUtil;
import com.z.fileselectorlib.adapter.FileListAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class FileSelectorActivity extends AppCompatActivity {

    private ArrayList<FileInfo> currentFileList = new ArrayList<>();
    private ArrayList<String> FileSelected = new ArrayList<>();
    private ListView lvFileList;
    private TextView tvTips;
    private TextView tv_select_num;
    private LinearLayout llTopView;
    private ImageView imBack;
    private LinearLayout llBottomView;
    private Button select_confirm;
    private Button select_cancel;
    private Window window;
    private AdapterView.OnItemClickListener onItemClickListener;
    private AdapterView.OnItemLongClickListener itemLongClickListener;
    private FileListAdapter adapter;
    private Activity activity;
    private boolean onSelect=false;
    private int SelectNum=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_selector);

        //init params
        tv_select_num =findViewById(R.id.select_num);
        tvTips =findViewById(R.id.tips);
        lvFileList=findViewById(R.id.FileList);
        imBack=findViewById(R.id.back);
        llTopView=findViewById(R.id.top_view);
        llBottomView=findViewById(R.id.bottom_view);
        select_confirm=findViewById(R.id.select_confirm);
        select_cancel=findViewById(R.id.select_cancel);
        activity=this;
        setOnItemClick();
        setOnItemLongClick();

        //init views
        initSelectNum();
        initTips();
        initBackBtn();
        initTopView();
        initBottomView();


        if (PermissionUtil.isStoragePermissionGranted(this)) {
            //get init files
            String initPath= BasicParams.getInstance().getRootPath();
            getFileList(initPath);
            adapter=new FileListAdapter(currentFileList,this);
            lvFileList.setAdapter(adapter);
            lvFileList.setOnItemClickListener(onItemClickListener);
            lvFileList.setOnItemLongClickListener(itemLongClickListener);

        }

    }

    private void initBottomView() {
        BottomViewShow(View.INVISIBLE,0);
        select_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                Bundle bundle_back=new Bundle();
                bundle_back.putStringArrayList(FileSelectorSettings.FILE_PATH_LIST_REQUEST,FileSelected);
                intent.putExtras(bundle_back);
                activity.setResult(FileSelectorSettings.BACK_WITH_SELECTIONS,intent);
                activity.finish();
            }
        });
        select_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.clearSelections();
                adapter.setSelect(false);
                BottomViewShow(View.INVISIBLE,0);
                onSelect=false;
                SelectNum=0;
                FileSelected.clear();
                changeSelectNum(0);
                tv_select_num.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void setOnItemLongClick() {
        itemLongClickListener=new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (!onSelect && currentFileList.get(position).getFileType() != FileInfo.FileType.Parent) {
                    tv_select_num.setVisibility(View.VISIBLE);
                    BottomViewShow(View.VISIBLE, 140);
                    adapter.setSelect(true);
                    onSelect=true;
                }
                return true;
            }
        };
    }

    public void BottomViewShow(int visible, int i) {
        llBottomView.setVisibility(visible);
        ViewGroup.LayoutParams params = llBottomView.getLayoutParams();
        params.height = i;
        llBottomView.setLayoutParams(params);
    }

    private void initBackBtn() {
        imBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.setResult(FileSelectorSettings.BACK_WITHOUT_SELECT);
                activity.finish();
            }
        });
    }

    private void initTopView() {
        int theme_color= Color.parseColor(BasicParams.getInstance().getColor());
        llTopView.setBackgroundColor(theme_color);
        window= this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //显示状态栏
        window.setStatusBarColor(theme_color);
        if (ColorUtils.calculateLuminance(theme_color)>0.5) {
            StatusBarUtil.setStatusBarDarkTheme(this, true);
            tvTips.setTextColor(getColor(R.color.text_color_dark));
        }
        else {
            StatusBarUtil.setStatusBarDarkTheme(this, false);
            tvTips.setTextColor(getColor(R.color.text_color_light));
        }
    }

    private void initTips() {
        tvTips.setText(BasicParams.getInstance().getTips());
    }

    private void initSelectNum() {
        changeSelectNum(0);
        tv_select_num.setVisibility(View.INVISIBLE);
    }

    private void changeSelectNum(int num) {
        String selectNum=getString(R.string.selectNum);
        selectNum=String.format(selectNum,num, BasicParams.getInstance().getMaxSelectNum());
        tv_select_num.setText(selectNum);
    }

    private void setOnItemClick() {
        onItemClickListener=new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FileInfo file_select=currentFileList.get(position);
                if (onSelect && file_select.getFileType() != FileInfo.FileType.Parent){
                    FileListAdapter.ViewHolder viewHolder = (FileListAdapter.ViewHolder) view.getTag();
                    if (file_select.FileFilter(BasicParams.getInstance().getFileTypes())) {
                        if (SelectNum < BasicParams.getInstance().getMaxSelectNum() || viewHolder.ckSelector.isChecked()) {
                            viewHolder.ckSelector.toggle();
                            if (file_select.getFileType() != FileInfo.FileType.Parent) {
                                adapter.ModifyFileSelected(position, viewHolder.ckSelector.isChecked());
                                if (viewHolder.ckSelector.isChecked()) {
                                    FileSelected.add(file_select.getFilePath());
                                } else {
                                    FileSelected.remove(file_select.getFilePath());
                                }
                                SelectNum = FileSelected.size();
                                changeSelectNum(SelectNum);
                            }
                        }
                    }
                }else {
                    if (file_select.getFileType() == FileInfo.FileType.Folder ||
                            file_select.getFileType() == FileInfo.FileType.Parent) {
                        getFileList(file_select.getFilePath());
                        adapter.updateFileList(currentFileList);
                        if (onSelect){
                            adapter.clearSelections();
                        }
                    }
                }
            }
        };
    }


    private void getFileList(String Path) {
        currentFileList.clear();
        File initFile = new File(Path);
        if (!Path.equals(BasicParams.BasicPath)){
            FileInfo fileInfo=new FileInfo();
            fileInfo.setFileName("返回上一级");
            fileInfo.setFileLastUpdateTime("");
            fileInfo.setFileType(FileInfo.FileType.Parent);
            fileInfo.setFilePath(initFile.getParent());
            fileInfo.setFileCount(-1);
            currentFileList.add(fileInfo);
        }
        File[] files = initFile.listFiles();
        assert files != null;
        List<File> file_list= Arrays.asList(files);
        FileUtil.SortFilesByName(file_list);
        for (File f : file_list) {
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
                    Log.d("myfile", fileInfo.getFileName() + ":" + fileInfo.getFileCount());
                    currentFileList.add(fileInfo);
                } else {
                    if (FileUtil.isAudioFileType(f.getPath())) {
                        FileInfo fileInfo = new FileInfo();
                        fileInfo.setFileName(f.getName());
                        fileInfo.setFileLastUpdateTime(TimeUtil.getDateInString(new Date(f.lastModified())));
                        fileInfo.setFilePath(f.getPath());
                        fileInfo.setFileType(FileInfo.FileType.Audio);
                        fileInfo.setFileCount(f.length());
                        Log.d("myfile", fileInfo.getFileName() + ":" + fileInfo.getFileCount());
                        currentFileList.add(fileInfo);
                    }
                    else if (FileUtil.isImageFileType(f.getPath())){
                        FileInfo fileInfo = new FileInfo();
                        fileInfo.setFileName(f.getName());
                        fileInfo.setFileLastUpdateTime(TimeUtil.getDateInString(new Date(f.lastModified())));
                        fileInfo.setFilePath(f.getPath());
                        fileInfo.setFileType(FileInfo.FileType.Image);
                        fileInfo.setFileCount(f.length());
                        Log.d("myfile", fileInfo.getFileName() + ":" + fileInfo.getFileCount());
                        currentFileList.add(fileInfo);
                    }
                    else if (FileUtil.isVideoFileType(f.getPath())){
                        FileInfo fileInfo = new FileInfo();
                        fileInfo.setFileName(f.getName());
                        fileInfo.setFileLastUpdateTime(TimeUtil.getDateInString(new Date(f.lastModified())));
                        fileInfo.setFilePath(f.getPath());
                        fileInfo.setFileType(FileInfo.FileType.Video);
                        fileInfo.setFileCount(f.length());
                        Log.d("myfile", fileInfo.getFileName() + ":" + fileInfo.getFileCount());
                        currentFileList.add(fileInfo);
                    }
                    else {
                        FileInfo fileInfo = new FileInfo();
                        fileInfo.setFileName(f.getName());
                        fileInfo.setFileLastUpdateTime(TimeUtil.getDateInString(new Date(f.lastModified())));
                        fileInfo.setFilePath(f.getPath());
                        fileInfo.setFileType(FileInfo.FileType.Unknown);
                        fileInfo.setFileCount(f.length());
                        Log.d("myfile", fileInfo.getFileName() + ":" + fileInfo.getFileCount());
                        currentFileList.add(fileInfo);
                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.v("permission","onRequestPermissionsResult requestCode ： " + requestCode
                + " Permission: " + permissions[0] + " was " + grantResults[0]
                + " Permission: " + permissions[1] + " was " + grantResults[1]
        );
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            //resume tasks needing this permission
            Log.i("permission", "granted");
            //TODO: 再次加载文件目录
            getFileList(BasicParams.getInstance().getRootPath());
        }
    }

}
