# 介绍

 1. 这是一个文件选择器库
 2. 可以区分多种文件类型
 3. 支持多选和单选
 4. 可以选择指定类型文件
 
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
implementation 'com.github.zzy0516alex:FileSelectorRelease:v2.0'
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
  		<activity android:name="com.z.fileselectorlib.FileSelectorActivity"/>      
<application> 
```
依赖与权限设置完成后
在你的Activity中

```java
FileSelectorSettings selectorSettings=new FileSelectorSettings();
                selectorSettings
                	.setTitle("请选择文件夹")//标题
                        .setMaxFileSelect(3)//最大文件选择数
                        .setRootPath(FileSelectorSettings.getSystemRootPath()+"/download")//起始路径
                        .setFileTypesToSelect(FileInfo.FileType.Folder)//可选择文件类型
                        .show(activity);//显示文件选择器
```
