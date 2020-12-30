# 一款具有多种功能的文件选择器
# 介绍
GitHub地址：[Fileselector
](https://github.com/zzy0516alex/FileSelectorRelease)

 1. 这是一个文件选择器
 2. 可以区分多种文件类型
 3. 支持多选和单选
 4. 可以选择指定类型文件
 5. 提供更多功能拓展
 
 ### 实例展示
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201230203801806.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NDM3MDUwNg==,size_16,color_FFFFFF,t_70#pic_center)

顶部导航栏可点击

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201230204002283.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NDM3MDUwNg==,size_16,color_FFFFFF,t_70#pic_center)

长按条目可选择

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201230204039400.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NDM3MDUwNg==,size_16,color_FFFFFF,t_70#pic_center)

可自定义拓展功能

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201230204109978.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NDM3MDUwNg==,size_16,color_FFFFFF,t_70#pic_center)



 
 # 使用方法
 
gradle:project 中
```java
allprojects {
    repositories {
        google()
        maven { url 'https://www.jitpack.io' }
        jcenter()
    }
}
```
gradle:app 中

```java
implementation 'com.github.zzy0516alex:FileSelectorRelease:v3.0'
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
FileSelectorSettings settings=new FileSelectorSettings();
                settings.setRootPath(FileSelectorSettings.getSystemRootPath()+"/Android")//起始路径
                        .setMaxFileSelect(2)//最大文件选择数
                        .setTitle("请选择文件夹")//标题
                        .setThemeColor("#3700B3")//主题颜色
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

# 类与方法
FileSelectorSettings
| 方法 | 注释 | 错误 |
|--|--|--|
| FileSelectorSettings setRootPath(String path) | 设置起始目录 |"初始路径不是一个目录或无权限"|
| FileSelectorSettings setMaxFileSelect(int num) | 设置最大文件选择数 |无|
| FileSelectorSettings setTitle(String title) | 设置标题 |无|
| FileSelectorSettings setThemeColor(String color) | 设置主题颜色(包含标题栏和状态栏) |无|
| FileSelectorSettings setFileTypesToSelect(FileInfo.FileType ... fileTypes)| 设置可选择的文件类型|"文件类型不能包含parent"|
| FileSelectorSettings setMoreOPtions(String[] optionsName, BasicParams.OnOptionClick...onOptionClicks) | 设置更多选项，第一个参数为选项名，第二个参数为选项点击事件 |“选项名和点击响应数量必须对应"|
| static String getSystemRootPath() | 获取系统外部存储根目录：/storage/emulated/0 |无|

FileInfo.FileType
|变量| 解释 |
|--|--|
| Folder | 文件夹 |
| Video | 视频文件 |
| Image| 图片文件 |
| Audio | 音频文件 |
| Unknown | 除上述类型以外的其他文件类型 |
| Parent | 不可用的变量 |

BasicParams.OnOptionClick
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
