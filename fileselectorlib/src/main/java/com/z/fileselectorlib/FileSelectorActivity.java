package com.z.fileselectorlib;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.documentfile.provider.DocumentFile;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.z.fileselectorlib.Objects.BasicParams;
import com.z.fileselectorlib.Objects.FileInfo;
import com.z.fileselectorlib.Objects.FileList;
import com.z.fileselectorlib.Threads.ListFileThread;
import com.z.fileselectorlib.Utils.FileUtil;
import com.z.fileselectorlib.Utils.PermissionUtil;
import com.z.fileselectorlib.Utils.StatusBarUtil;
import com.z.fileselectorlib.Utils.TimeUtil;
import com.z.fileselectorlib.adapter.FileListAdapter;
import com.z.fileselectorlib.adapter.NavigationAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class FileSelectorActivity extends AppCompatActivity {
    private static final int REQUEST_FOR_DATA_PATH = 20;
    private enum Orientation{Forward, Backward, Init, Skip};

    private FileList fileList;
    private ArrayList<FileInfo> currentFileList = new ArrayList<>();
    private ArrayList<String> FileSelected = new ArrayList<>();
    private ArrayList<String> RelativePaths = new ArrayList<>();
    private String currentPath;
    private ListView lvFileList;
    private TextView tvTips;
    private TextView tv_select_num;
    private RelativeLayout llTopView;
    private ImageView imBack;
    private LinearLayout llBottomView;
    private Button select_confirm;
    private Button select_cancel;
    private RecyclerView navigation_view;
    private LinearLayout llRoot;
    private NavigationAdapter navigationAdapter;
    private ImageView imMore;
    private PopupWindow moreOptions;
    private SwipeRefreshLayout refreshLayout;
    private Window window;
    private AdapterView.OnItemClickListener onItemClickListener;
    private AdapterView.OnItemLongClickListener itemLongClickListener;
    private FileListAdapter fileListAdapter;
    private Activity activity;
    private Context context;
    private SharedPreferences sharedPreferences;
    private ListFileThread.FileListHandler handler;
    private boolean init = true;//初次加载文件目录
    private boolean onSelect = false;
    private int SelectNum=0;
    private int parent_list_pos = 0;//父级文件列表的顶部元素索引
    private Orientation orientation = Orientation.Init;

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
        navigation_view=findViewById(R.id.navigation_view);
        llRoot=findViewById(R.id.root);
        imMore=findViewById(R.id.more);
        activity=this;
        context = this;
        sharedPreferences = getSharedPreferences("FSConf",MODE_PRIVATE);
        setOnItemClick();
        setOnItemLongClick();

        //init views
        initSelectNum();
        initTips();
        initBackBtn();
        initTopView();
        initBottomView();
        initRootButton();
        initMoreOptionsView();

        FileSelectorTheme theme = BasicParams.getInstance().getTheme();
        int theme_color= theme.getThemeColor();
        refreshLayout = findViewById(R.id.fs_refresh);
        refreshLayout.setColorSchemeColors(theme_color);
        refreshLayout.setOnRefreshListener(() -> {
            refreshFileList(currentPath, Orientation.Init);
        });

        fileList = new ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(FileList.class);
        fileList.getFileList().observe(this, fileInfo -> {
            currentFileList = fileInfo;
            if (init){
                setFileList();
                init = false;
            }
            fileListAdapter.updateFileList(currentFileList);
            if (onSelect){
                fileListAdapter.clearSelections();
            }
        });

        handler = new ListFileThread.FileListHandler(fileInfoList -> {
            if (fileInfoList.size()==0)return;
            fileList.addToResult(fileInfoList);
            fileList.sortByName();
        });


        if (PermissionUtil.isStoragePermissionGranted(this)) {
            //get init files
            initFileList();
        }

    }

    @Override
    public void onBackPressed() {
        if (onSelect){
            quitSelectMod();
        }
        else if (!currentPath.equals(BasicParams.BasicPath)){
            refreshFileList(new File(currentPath).getParent(), Orientation.Backward);
        }
        else super.onBackPressed();
    }

    private void initMoreOptionsView() {
        if (BasicParams.getInstance().isNeedMoreOptions())imMore.setVisibility(View.VISIBLE);
        else imMore.setVisibility(View.INVISIBLE);
        imMore.setOnClickListener(v -> {
            //加载布局
            LinearLayout layout = (LinearLayout) LayoutInflater.from(activity).inflate(R.layout.popup_more_options, null);
            layout.measure(0,0);
            //找到布局的控件
            ListView listView = layout.findViewById(R.id.options);
            //设置适配器
            listView.setAdapter(new ArrayAdapter<>(activity, R.layout.option_list_item, R.id.option_text, BasicParams.getInstance().getOptionsName()));
            // 实例化popupWindow
            moreOptions = new PopupWindow(layout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            //控制键盘是否可以获得焦点
            moreOptions.setFocusable(true);
            //设置popupWindow弹出窗体的背景
            moreOptions.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.popwindow_white));
            BackGroundAlpha(0.6f);
            moreOptions.showAsDropDown(imMore,-layout.getMeasuredWidth() + imMore.getWidth(),-imMore.getHeight()+30);
            moreOptions.setOnDismissListener(() -> BackGroundAlpha(1.0f));
            listView.setOnItemClickListener((parent, view, position, id) -> {
                moreOptions.dismiss();
                BasicParams.getInstance().getOnOptionClicks()[position].onclick(activity,position,currentPath,FileSelected);
                //刷新列表
                refreshFileList(currentPath, Orientation.Init);
            });
        });
    }

    public void BackGroundAlpha(float f) {
        WindowManager.LayoutParams layoutParams = activity.getWindow().getAttributes();
        layoutParams.alpha = f;
        activity.getWindow().setAttributes(layoutParams);
    }

    private void initRootButton() {
        FileSelectorTheme theme = BasicParams.getInstance().getTheme();
        int naviBarTextColor = theme.getNaviBarTextColor();
        int naviBarTextSize = theme.getNaviBarTextSize();
        int naviBarArrowIcon = theme.getNaviBarArrowIcon();
        TextView tv_root_name = findViewById(R.id.root_name);
        ImageView im_path_segment = findViewById(R.id.path_segment);
        tv_root_name.setTextColor(naviBarTextColor);
        tv_root_name.setTextSize(naviBarTextSize);
        im_path_segment.setImageResource(naviBarArrowIcon);

        llRoot.setOnClickListener(v -> {
            String rootPath= BasicParams.BasicPath;
            refreshFileList(rootPath, Orientation.Skip);
        });
    }

    private void setNavigationBar(String initPath) {
        RelativePaths= FileUtil.getRelativePaths(initPath);
        navigation_view.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        navigationAdapter=new NavigationAdapter(this,RelativePaths);
        navigationAdapter.setRecycleItemClickListener((view, position) -> {
            List<String>sublist=RelativePaths.subList(0,position+1);
            RelativePaths= new ArrayList<>(sublist);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                refreshFileList(FileUtil.mergeAbsolutePath(RelativePaths), Orientation.Skip);
            }
        });
        navigation_view.setAdapter(navigationAdapter);
    }

    private void setFileList() {
        fileListAdapter =new FileListAdapter(currentFileList,this);
        lvFileList.setAdapter(fileListAdapter);
        lvFileList.setOnItemClickListener(onItemClickListener);
        lvFileList.setOnItemLongClickListener(itemLongClickListener);
    }

    private void initBottomView() {
        BottomViewShow(View.INVISIBLE,0);
        select_confirm.setOnClickListener(v -> {
            Intent intent=new Intent();
            Bundle bundle_back=new Bundle();
            bundle_back.putStringArrayList(FileSelectorSettings.FILE_PATH_LIST_REQUEST,FileSelected);
            intent.putExtras(bundle_back);
            activity.setResult(FileSelectorSettings.BACK_WITH_SELECTIONS,intent);
            activity.finish();
        });
        select_cancel.setOnClickListener(v -> quitSelectMod());
    }

    private void quitSelectMod() {
        fileListAdapter.clearSelections();
        fileListAdapter.setSelect(false);
        BottomViewShow(View.INVISIBLE,0);
        onSelect=false;
        SelectNum=0;
        FileSelected.clear();
        changeSelectNum(0);
        tv_select_num.setVisibility(View.INVISIBLE);
    }

    private void setOnItemLongClick() {
        itemLongClickListener= (parent, view, position, id) -> {
            if (!onSelect && currentFileList.get(position).getFileType() != FileInfo.FileType.Parent) {
                tv_select_num.setVisibility(View.VISIBLE);
                BottomViewShow(View.VISIBLE, 140);
                fileListAdapter.setSelect(true);
                onSelect=true;
            }
            return true;
        };
    }

    public void BottomViewShow(int visible, int i) {
        llBottomView.setVisibility(visible);
        ViewGroup.LayoutParams params = llBottomView.getLayoutParams();
        params.height = i;
        llBottomView.setLayoutParams(params);
    }

    private void initBackBtn() {
        imBack.setOnClickListener(v -> {
            activity.setResult(FileSelectorSettings.BACK_WITHOUT_SELECT);
            activity.finish();
        });
    }

    private void initTopView() {
        //theme
        FileSelectorTheme theme = BasicParams.getInstance().getTheme();
        int theme_color= theme.getThemeColor();
        int back_icon_id= theme.getTopToolBarBackIcon();
        int title_color = theme.getTopToolBarTitleColor();
        int title_size = theme.getTopToolBarTitleSize();
        int menu_icon_id = theme.getTopToolBarMenuIcon();

        llTopView.setBackgroundColor(theme_color);
        ImageView back_arrow = findViewById(R.id.back);
        back_arrow.setImageResource(back_icon_id);
        tvTips.setTextColor(title_color);
        tvTips.setTextSize(title_size);
        tv_select_num.setTextColor(title_color);
        tv_select_num.setTextSize(title_size);
        imMore.setImageResource(menu_icon_id);

        window= this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //显示状态栏
        window.setStatusBarColor(theme_color);
        if (ColorUtils.calculateLuminance(theme_color)>0.5) {
            StatusBarUtil.setStatusBarDarkTheme(this, true);
        }
        else {
            StatusBarUtil.setStatusBarDarkTheme(this, false);
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
        onItemClickListener= (parent, view, position, id) -> {
            FileInfo file_select=currentFileList.get(position);
            if (onSelect && file_select.getFileType() != FileInfo.FileType.Parent){
                FileListAdapter.ViewHolder viewHolder = (FileListAdapter.ViewHolder) view.getTag();
                if (file_select.FileFilter(BasicParams.getInstance().getSelectableFileTypes())) {
                    if (SelectNum < BasicParams.getInstance().getMaxSelectNum() || viewHolder.ckSelector.isChecked()) {
                        viewHolder.ckSelector.toggle();
                        if (file_select.getFileType() != FileInfo.FileType.Parent) {
                            fileListAdapter.ModifyFileSelected(position, viewHolder.ckSelector.isChecked());
                            if (viewHolder.ckSelector.isChecked()) {
                                FileSelected.add(file_select.getFilePath());
                            } else {
                                FileSelected.remove(file_select.getFilePath());
                            }
                            SelectNum = FileSelected.size();
                            changeSelectNum(SelectNum);
                        }
                    }
                }else Toast.makeText(activity, "该文件类型不可选", Toast.LENGTH_SHORT).show();
            }else {
                if (file_select.getFileType() == FileInfo.FileType.Folder ) {
                    parent_list_pos = lvFileList.getFirstVisiblePosition();
                    refreshFileList(file_select, Orientation.Forward);
                }
                else if (file_select.getFileType() == FileInfo.FileType.Parent){
                    refreshFileList(file_select, Orientation.Backward);
                }
            }
        };
    }

    private void refreshFileList(FileInfo parent, Orientation orientation) {
        if (!PermissionUtil.isStoragePermissionGranted(this))return;
        if (!refreshLayout.isRefreshing())refreshLayout.setRefreshing(true);
        this.orientation = orientation;
        String path = parent.getFilePath();
        currentPath= path;
        startListFileThread(parent);
        RelativePaths=FileUtil.getRelativePaths(path);
        navigationAdapter.UpdatePathList(RelativePaths);
        navigation_view.scrollToPosition(navigationAdapter.getItemCount()-1);
        if (onSelect){
            fileListAdapter.clearSelections();
        }
    }

    private void refreshFileList(String parent_path, Orientation orientation) {
        if (!PermissionUtil.isStoragePermissionGranted(this))return;
        if (!refreshLayout.isRefreshing())refreshLayout.setRefreshing(true);
        this.orientation = orientation;
        currentPath= parent_path;
        startListFileThread(parent_path);
        RelativePaths=FileUtil.getRelativePaths(parent_path);
        navigationAdapter.UpdatePathList(RelativePaths);
        navigation_view.scrollToPosition(navigationAdapter.getItemCount()-1);
        if (onSelect){
            fileListAdapter.clearSelections();
        }
    }

    private void startListFileThread(String initPath){
        File file = new File(initPath);
        FileInfo initFile = new FileInfo();
        initFile.setFileName(file.getName());
        initFile.setAccessType(FileInfo.judgeAccess(initPath));
        initFile.setFileType(file.isDirectory()? FileInfo.FileType.Folder: FileInfo.FileType.Unknown);
        initFile.setFilePath(initPath);
        startListFileThread(initFile);
    }

    private void startListFileThread(FileInfo parent){
        fileList.clear();
        File initFile = new File(parent.getFilePath());
        if (!parent.getFilePath().equals(BasicParams.BasicPath)){
            FileInfo fileInfo=new FileInfo();
            fileInfo.setFileName("返回上一级");
            fileInfo.setFileLastUpdateTime("");
            fileInfo.setFileType(FileInfo.FileType.Parent);
            fileInfo.setFilePath(initFile.getParent());
            fileInfo.setFileCount(-1);
            fileInfo.setAccessType(FileInfo.judgeAccess(fileInfo.getFilePath()));
            fileList.addToResult(fileInfo);
        }
        if (parent.getAccessType()== FileInfo.AccessType.Open) {
            File[] files = initFile.listFiles();
            if (files == null) {
                if (parent.getFilePath().contains("Android/data")) {
                    Log.d("list file", "enter Android/data");
                    handleProtectedFiles(parent);
                }
                else if (parent.getFilePath().contains("Android/obb")){
                    handleProtectedFiles(parent);
                }
                else Log.d("list file", "can not list file");
                return;
            }
            handleNormalFiles(files);
        }
        else {
            handleProtectedFiles(parent);
        }
    }

    private void handleNormalFiles(File[] files) {
        int parts = files.length/20;
        CountDownLatch countDownLatch = new CountDownLatch(parts+1);
        int index = 0;
        for (int i = 0; i < parts+1; i++) {
            int size = 20;
            if (i==parts)size = files.length % 20;
            if (size==0)continue;
            File[] sub_files = new File[size];
            for (int j = 0; j < size; j++) {
                sub_files[j] = files[index];
                index++;
            }
            ListFileThread thread = new ListFileThread(sub_files);
            thread.setHandler(handler);
            thread.setCountDownLatch(countDownLatch);
            thread.start();
        }
        new Thread(()->{
            try {
                countDownLatch.await();
                runOnUiThread(()->{
                    if (orientation == Orientation.Backward)
                        lvFileList.post(()-> lvFileList.setSelection(parent_list_pos));
                    if (refreshLayout.isRefreshing())
                        refreshLayout.setRefreshing(false);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void handleProtectedFiles(FileInfo parent) {
        String grant_key = "";
        if (parent.getFilePath().contains("Android/data"))grant_key = "data_path_granted";
        if (parent.getFilePath().contains("Android/obb"))grant_key = "obb_path_granted";
        boolean data_all_granted = sharedPreferences.getBoolean(grant_key, false);
        boolean granted = FileUtil.isGrant(context, parent.getFilePath());
        if (!granted && !data_all_granted) grantPermissionForProtectedFile(parent.getFilePath());
        else{
            DocumentFile documentFile = FileUtil.getDocumentFilePath(context, parent.getFilePath());
            if (documentFile == null) {
                Toast.makeText(context, "该路径无法识别", Toast.LENGTH_SHORT).show();
                if (refreshLayout.isRefreshing())
                    refreshLayout.setRefreshing(false);
                return;
            }
            if (documentFile.isDirectory()) {
                DocumentFile[] documentFiles = documentFile.listFiles();
                int parts = documentFiles.length/20;
                CountDownLatch countDownLatch = new CountDownLatch(parts+1);
                int index = 0;boolean isEmpty = false;
                for (int i = 0; i < parts+1; i++) {
                    int size = 20;
                    if (i==parts)size = documentFiles.length % 20;
                    if (size==0){
                        isEmpty = true;break;
                    }
                    DocumentFile[] sub_files = new DocumentFile[size];
                    for (int j = 0; j < size; j++) {
                        sub_files[j] = documentFiles[index];
                        index++;
                    }
                    ListFileThread thread = new ListFileThread(sub_files);
                    thread.setHandler(handler);
                    thread.setCountDownLatch(countDownLatch);
                    thread.start();
                }
                if (isEmpty){
                    if (refreshLayout.isRefreshing())
                        refreshLayout.setRefreshing(false);
                    return;
                }
                new Thread(()->{
                    try {
                        countDownLatch.await();
                        runOnUiThread(()->{
                            if (orientation == Orientation.Backward)
                                lvFileList.post(()-> lvFileList.setSelection(parent_list_pos));
                            if (refreshLayout.isRefreshing())
                                refreshLayout.setRefreshing(false);
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        }
    }

    private void grantPermissionForProtectedFile(String path) {
        String uri = FileUtil.changeToUriAndroidOrigin(path);//调用方法，把path转换成可解析的uri文本
        Uri parse = Uri.parse(uri);
        Intent intent = new Intent("android.intent.action.OPEN_DOCUMENT_TREE");
        intent.addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                        | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, parse);
        }
        startActivityForResult(intent, REQUEST_FOR_DATA_PATH);//开始授权
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
            initFileList();
        }
        else Toast.makeText(context, "未获得文件读写权限", Toast.LENGTH_SHORT).show();
    }

    private void initFileList() {
        if (!refreshLayout.isRefreshing())
            refreshLayout.setRefreshing(true);
        String initPath = BasicParams.getInstance().getRootPath();
        File[] test = (new File(initPath)).listFiles();
        DocumentFile documentFile = FileUtil.getDocumentFilePath(context, initPath);
        if (test == null && documentFile == null){
            initPath = BasicParams.BasicPath;
        }
        startListFileThread(initPath);
        currentPath = initPath;
        setNavigationBar(initPath);
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (refreshLayout.isRefreshing())
            refreshLayout.setRefreshing(false);
        Uri uri;
        if (data == null & requestCode == REQUEST_FOR_DATA_PATH) {
            Log.d("file permission","request permission failed");
            return;
        }
        if (requestCode == REQUEST_FOR_DATA_PATH && (uri = data.getData()) != null) {
            getContentResolver().takePersistableUriPermission(uri, data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION));//保存目录的访问权限
            String grant_key = "";
            if (currentPath.contains("Android/data"))grant_key = "data_path_granted";
            if (currentPath.contains("Android/obb"))grant_key = "obb_path_granted";
            sharedPreferences.edit().putBoolean(grant_key,true).apply();
            refreshFileList(currentPath,Orientation.Forward);
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
