# 一款具有多种功能的文件选择器，支持访问Android/data(obb)等系统文件夹
# 介绍
GitHub地址：[Fileselector
](https://github.com/zzy0516alex/FileSelectorRelease)

 1. 这是一个文件选择器
 2. 可以区分多种文件类型，并支持自定义它们的图标
 3. 支持多选和单选
 4. 可以对选择或显示的文件类型进行指定
 5. 提供功能拓展菜单进行自定义
 6. 支持访问/选择Android系统文件(夹)
 7. 支持文件列表下拉刷新
 8. 支持非英文路径显示和访问
 9. 采用多线程加载，打开文件列表更迅捷
 
 ### 实例展示
![在这里插入图片描述](https://img-blog.csdnimg.cn/3f805fa469f648f9b4fd299333806c12.gif#pic_center)

更多预览图请访问[我的博客](https://blog.csdn.net/weixin_44370506/article/details/111828374)

 
 # 使用方法
 ## 基础使用
gradle:project 中
```java
allprojects {
    repositories {
        ...
        maven { url 'https://www.jitpack.io' }
        ...
    }
}
```
gradle:app 中

```java
implementation 'com.github.zzy0516alex:FileSelectorRelease:v6.0'
```
Manifest中

```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

```xml
<application
        ...
        android:requestLegacyExternalStorage ="true">    
<application> 
```
依赖与权限设置完成后
在你的Activity中

```java
FileSelectorSettings settings = new FileSelectorSettings();
                settings.setRootPath(FileSelectorSettings.getSystemRootPath()+"/Android")//起始路径
                        .setMaxFileSelect(2)//最大文件选择数
                        .setTitle("请选择文件夹")//标题
                        .setFileTypesToSelect(FileInfo.FileType.Folder)//可选择文件类型
                        .setMoreOPtions(new String[]{"新建文件夹", "删除文件"},
                                new BasicParams.OnOptionClick() {
                                    @Override
                                    public void onclick(Activity activity, int position, String currentPath, ArrayList<String> FilePathSelected) {
                                        File Folder =new File(currentPath,"新文件夹");
                                        if(!Folder.exists()){
                                            Folder.mkdir();
                                        }
                                    }
                                }, new BasicParams.OnOptionClick() {
                                    @Override
                                    public void onclick(Activity activity, int position, String currentPath, ArrayList<String> FilePathSelected) {
                                        if (FilePathSelected!=null){
                                            for (String path :
                                                    FilePathSelected) {
                                                File delFile=new File(path);
                                                delFile.delete();
                                            }
                                        }
                                    }
                                })//更多功能拓展
                        .show(MainActivity.this);//显示
```

获取返回的数据
```java
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FileSelectorSettings.FILE_LIST_REQUEST_CODE && resultCode == FileSelectorSettings.BACK_WITH_SELECTIONS){
            assert data != null;
            Bundle bundle=data.getExtras();
            assert bundle != null;
            ArrayList<String> FilePathSelected
                    =bundle.getStringArrayList(FileSelectorSettings.FILE_PATH_LIST_REQUEST);
            for (String file_path :
                    FilePathSelected) {
                Log.v("file_sel", file_path);
            }
        }
    }
```

## 自定义文件显示和图标
```java
FileSelectorSettings settings = new FileSelectorSettings();
            settings.setRootPath(FileSelectorSettings.getSystemRootPath())
                    .setMaxFileSelect(2)
                    .setTitle("请选择文件夹")
                    .setFileTypesToSelect(FileInfo.FileType.Unknown)//选择自定义后缀的文件此处参数需为Unknown
                    .setFileTypesToShow(".cer")//自定义可见的文件后缀
                    .setCustomizedIcons(new String[]{".cer"},context,R.mipmap.file_cert)//自定义文件图标
                    .show(MainActivity.this);
```

## 自定义图标及字体
```java
FileSelectorTheme theme = new FileSelectorTheme();
        theme.setTopToolBarTitleColor("#03DAC5")
                .setThemeColor("#FFFFFF")
                .setTopToolBarTitleSize(28)
                .setTopToolBarBackIcon(R.mipmap.back_black)
                .setTopToolBarMenuIcon(R.mipmap.options)
                .setNaviBarArrowIcon(R.mipmap.segment)
                .setNaviBarTextSize(20)
                .setFileInfoColor(getColor(R.color.black))
                .setFileNameSize(25);
FileSelectorSettings settings = new FileSelectorSettings();
            settings.setTheme(theme)
                    .show(MainActivity.this);
```

## 更改默认的文件图标
本组件提供了一组默认的文件图标，如下：
| 图标资源名称 | 对应文件类型 |
|:--:|:--:|
| file_folder.png | 文件夹 |
| file_audio.png | 音频文件 |
| file_image.png | 图片文件 |
| file_text.png | 文本文件 |
| file_video.png | 视频文件 |
| file_unknown.png | 其他文件 |

只需要在 **res > mipmap** 文件夹中添加同名的替换文件即可更改新图标。

## 访问data或obb文件夹
启动文件选择器
```java
FileSelectorSettings settings=new FileSelectorSettings();
settings.setRootPath(FileSelectorSettings.getSystemRootPath() + "/Android/data")
        .setTitle("请选择文件夹")
        .setFileTypesToSelect(FileInfo.FileType.Folder)
        .show(this);
```
在onActivityResult中

```java
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
```
注意通过FileInfo.judgeAccess方法区分是否是受保护的系统文件夹，如果是，则应该通过**DocumentFile**的方式来访问。

# 类与方法
## FileSelectorTheme

下面图片中给出了UI的属性作用位置的序号：

![UI属性编号](https://img-blog.csdnimg.cn/941f10aeb70f43bd93e226511a275a59.png)

| 属性 | 入参类型 | 注释 | 对应UI位置 |
|--|--|--|:--:|
| themeColor | Color-int/Color-String | 主题色，顶栏及状态栏 |1|
| topToolBarBackIcon | Resource ID | 顶部返回按钮 |2|
| topToolBarTitleColor | Color-int/Color-String | 顶部标题及提示文本颜色 |3|
| topToolBarTitleSize | sp | 顶部标题及提示文本大小 |3|
| topToolBarMenuIcon | Resource ID | 顶部菜单按钮 |4|
| naviBarTextColor | Color-int/Color-String | 导航栏字体颜色 |5|
| naviBarTextSize | sp | 导航栏文本大小 |5|
| naviBarArrowIcon | Resource ID | 导航栏分隔箭头 |6|
| fileNameColor | Color-int/Color-String | 文件(夹)名称字体颜色 |7|
| fileNameSize | sp | 文件(夹)名称字体大小 |7|
| fileInfoColor | Color-int/Color-String | 文件信息提示字体颜色 |8|
| fileInfoSize | sp | 文件信息提示字体大小 |8|

表中每个属性均有set方法，并有默认的属性，故该设置不是必须的设置。

## FileSelectorSettings
| 方法 | 作用 | 注释 |
|--|--|--|
| FileSelectorSettings setRootPath(String path) | 设置起始目录 |无|
| FileSelectorSettings setFileListRequestCode(int requestCode)| 设置Activity请求码 |默认为512|
| FileSelectorSettings setMaxFileSelect(int num) | 设置最大文件选择数 |无|
| FileSelectorSettings setTitle(String title) | 设置标题 |无|
| FileSelectorSettings setTheme(FileSelectorTheme theme) | 设置界面主题(大部分图标及字体) |无|
| FileSelectorSettings setFileTypesToSelect(FileInfo.FileType ... fileTypes)| 设置可选择的文件类型|文件类型不能包含parent|
| FileSelectorSettings setFileTypesToShow(String ... extensions)| 设置可见的文件类型|后缀示例".txt",不填写则全部显示|
| FileSelectorSettings setCustomizedIcons(String[] extensions, Context context, int ... icon_ids)| 设置自定义文件图标|后缀名和图标资源id应一一对应|
| FileSelectorSettings setMoreOPtions(String[] optionsName, BasicParams.OnOptionClick...onOptionClicks) | 设置更多选项，第一个参数为选项名，第二个参数为选项点击事件 |选项名和点击响应数量必须对应|
| static String getSystemRootPath() | 获取系统外部存储根目录：/storage/emulated/0 |无|

## FileInfo.FileType
|变量| 解释 |
|--|--|
| Folder | 文件夹 |
| Video | 视频文件 |
| Image| 图片文件 |
| Audio | 音频文件 |
| Text | 文本文件 |
| Unknown | 除上述类型以外的其他文件类型 |
| Parent | 不可用的变量 |

## FileInfo.AccessType
|变量| 解释 |
|--|--|
| Open | 普通文件(夹) |
| Protected | Android系统保护的文件(夹)，必须通过SAF框架访问 |

## BasicParams.OnOptionClick
接口抽象方法：onclick

```java
void onclick(Activity activity,int position,String currentPath,ArrayList<String> FilePathSelected);
```
|参数| 解释 |
|--|--|
| activity | 文件选择器的activity |
| position | 当前点击的位置 |
| currentPath | 当前所在的目录 |
| FilePathSelected | 当前用户选择的文件列表 |
