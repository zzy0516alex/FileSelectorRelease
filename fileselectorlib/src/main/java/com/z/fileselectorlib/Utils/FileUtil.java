package com.z.fileselectorlib.Utils;

import android.content.Context;
import android.content.UriPermission;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.documentfile.provider.DocumentFile;

import com.z.fileselectorlib.Objects.BasicParams;
import com.z.fileselectorlib.Objects.FileInfo;

import java.io.File;
import java.text.Collator;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class FileUtil {
    public static String sFileExtensions;

    // Audio
    public static final int FILE_TYPE_MP3     = 1;
    public static final int FILE_TYPE_M4A     = 2;
    public static final int FILE_TYPE_WAV     = 3;
    public static final int FILE_TYPE_AMR     = 4;
    public static final int FILE_TYPE_AWB     = 5;
    public static final int FILE_TYPE_WMA     = 6;
    public static final int FILE_TYPE_OGG     = 7;
    private static final int FIRST_AUDIO_FILE_TYPE = FILE_TYPE_MP3;
    private static final int LAST_AUDIO_FILE_TYPE = FILE_TYPE_OGG;

    // MIDI
    public static final int FILE_TYPE_MID     = 11;
    public static final int FILE_TYPE_SMF     = 12;
    public static final int FILE_TYPE_IMY     = 13;
    private static final int FIRST_MIDI_FILE_TYPE = FILE_TYPE_MID;
    private static final int LAST_MIDI_FILE_TYPE = FILE_TYPE_IMY;

    // Video
    public static final int FILE_TYPE_MP4     = 21;
    public static final int FILE_TYPE_M4V     = 22;
    public static final int FILE_TYPE_3GPP    = 23;
    public static final int FILE_TYPE_3GPP2   = 24;
    public static final int FILE_TYPE_WMV     = 25;
    private static final int FIRST_VIDEO_FILE_TYPE = FILE_TYPE_MP4;
    private static final int LAST_VIDEO_FILE_TYPE = FILE_TYPE_WMV;

    // Image
    public static final int FILE_TYPE_JPEG    = 31;
    public static final int FILE_TYPE_GIF     = 32;
    public static final int FILE_TYPE_PNG     = 33;
    public static final int FILE_TYPE_BMP     = 34;
    public static final int FILE_TYPE_WBMP    = 35;
    private static final int FIRST_IMAGE_FILE_TYPE = FILE_TYPE_JPEG;
    private static final int LAST_IMAGE_FILE_TYPE = FILE_TYPE_WBMP;

    // Playlist
    public static final int FILE_TYPE_M3U     = 41;
    public static final int FILE_TYPE_PLS     = 42;
    public static final int FILE_TYPE_WPL     = 43;
    private static final int FIRST_PLAYLIST_FILE_TYPE = FILE_TYPE_M3U;
    private static final int LAST_PLAYLIST_FILE_TYPE = FILE_TYPE_WPL;

    //TEXT
    public static final int FILE_TYPE_TXT     = 51;
    public static final int FILE_TYPE_DOC     = 52;
    public static final int FILE_TYPE_RTF     = 53;
    public static final int FILE_TYPE_LOG     = 54;
    public static final int FILE_TYPE_CONF    = 55;
    public static final int FILE_TYPE_SH      = 56;
    public static final int FILE_TYPE_XML     = 57;
    private static final int FIRST_TEXT_FILE_TYPE = FILE_TYPE_TXT;
    private static final int LAST_TEXT_FILE_TYPE = FILE_TYPE_XML;

    //静态内部类
    static class MediaFileType {

        int fileType;
        String mimeType;

        MediaFileType(int fileType, String mimeType) {
            this.fileType = fileType;
            this.mimeType = mimeType;
        }
    }

    private static HashMap<String, MediaFileType> sFileTypeMap
            = new HashMap<>();
    private static HashMap<String, Integer> sMimeTypeMap
            = new HashMap<>();
    static void addFileType(String extension, int fileType, String mimeType) {
        sFileTypeMap.put(extension, new MediaFileType(fileType, mimeType));
        sMimeTypeMap.put(mimeType, fileType);
    }
    static {
        addFileType("MP3", FILE_TYPE_MP3, "audio/mpeg");
        addFileType("M4A", FILE_TYPE_M4A, "audio/mp4");
        addFileType("WAV", FILE_TYPE_WAV, "audio/x-wav");
        addFileType("AMR", FILE_TYPE_AMR, "audio/amr");
        addFileType("AWB", FILE_TYPE_AWB, "audio/amr-wb");
        addFileType("WMA", FILE_TYPE_WMA, "audio/x-ms-wma");
        addFileType("OGG", FILE_TYPE_OGG, "application/ogg");

        addFileType("MID", FILE_TYPE_MID, "audio/midi");
        addFileType("XMF", FILE_TYPE_MID, "audio/midi");
        addFileType("RTTTL", FILE_TYPE_MID, "audio/midi");
        addFileType("SMF", FILE_TYPE_SMF, "audio/sp-midi");
        addFileType("IMY", FILE_TYPE_IMY, "audio/imelody");

        addFileType("MP4", FILE_TYPE_MP4, "video/mp4");
        addFileType("M4V", FILE_TYPE_M4V, "video/mp4");
        addFileType("3GP", FILE_TYPE_3GPP, "video/3gpp");
        addFileType("3GPP", FILE_TYPE_3GPP, "video/3gpp");
        addFileType("3G2", FILE_TYPE_3GPP2, "video/3gpp2");
        addFileType("3GPP2", FILE_TYPE_3GPP2, "video/3gpp2");
        addFileType("WMV", FILE_TYPE_WMV, "video/x-ms-wmv");

        addFileType("JPG", FILE_TYPE_JPEG, "image/jpeg");
        addFileType("JPEG", FILE_TYPE_JPEG, "image/jpeg");
        addFileType("GIF", FILE_TYPE_GIF, "image/gif");
        addFileType("PNG", FILE_TYPE_PNG, "image/png");
        addFileType("BMP", FILE_TYPE_BMP, "image/x-ms-bmp");
        addFileType("WBMP", FILE_TYPE_WBMP, "image/vnd.wap.wbmp");

        addFileType("M3U", FILE_TYPE_M3U, "audio/x-mpegurl");
        addFileType("PLS", FILE_TYPE_PLS, "audio/x-scpls");
        addFileType("WPL", FILE_TYPE_WPL, "application/vnd.ms-wpl");

        addFileType("TXT", FILE_TYPE_TXT , "text/plain");
        addFileType("DOC", FILE_TYPE_DOC , "application/msword");
        addFileType("RTF", FILE_TYPE_RTF , "application/rtf");
        addFileType("LOG", FILE_TYPE_LOG , "text/plain");
        addFileType("CONF", FILE_TYPE_CONF, "text/plain");
        addFileType("SH", FILE_TYPE_SH  , "text/plain");
        addFileType("XML", FILE_TYPE_XML , "text/plain");

        // compute file extensions list for native Media Scanner
        StringBuilder builder = new StringBuilder();

        for (String s : sFileTypeMap.keySet()) {
            if (builder.length() > 0) {
                builder.append(',');
            }
            builder.append(s);
        }
        sFileExtensions = builder.toString();
    }

    public static final String UNKNOWN_STRING = "<unknown>";

    public static boolean isAudioFileType(int fileType) {
        return ((fileType >= FIRST_AUDIO_FILE_TYPE &&
                fileType <= LAST_AUDIO_FILE_TYPE) ||
                (fileType >= FIRST_MIDI_FILE_TYPE &&
                        fileType <= LAST_MIDI_FILE_TYPE));
    }

    public static boolean isVideoFileType(int fileType) {
        return (fileType >= FIRST_VIDEO_FILE_TYPE &&
                fileType <= LAST_VIDEO_FILE_TYPE);
    }

    public static boolean isImageFileType(int fileType) {
        return (fileType >= FIRST_IMAGE_FILE_TYPE &&
                fileType <= LAST_IMAGE_FILE_TYPE);
    }

    public static boolean isPlayListFileType(int fileType) {
        return (fileType >= FIRST_PLAYLIST_FILE_TYPE &&
                fileType <= LAST_PLAYLIST_FILE_TYPE);
    }

    public static boolean isTextFileType(int fileType){
        return (fileType >= FIRST_TEXT_FILE_TYPE &&
                fileType <= LAST_TEXT_FILE_TYPE);
    }

    public static MediaFileType getFileType(String path) {
        int lastDot = path.lastIndexOf(".");
        if (lastDot < 0)
            return null;
        return sFileTypeMap.get(path.substring(lastDot + 1).toUpperCase());
    }

    //根据视频文件路径判断文件类型
    public static boolean isVideoFileType(String path) {
        MediaFileType type = getFileType(path);
        if(null != type) {
            return isVideoFileType(type.fileType);
        }
        return false;
    }

    //根据音频文件路径判断文件类型
    public static boolean isAudioFileType(String path) {
        MediaFileType type = getFileType(path);
        if(null != type) {
            return isAudioFileType(type.fileType);
        }
        return false;
    }

    //根据图片文件路径判断文件类型
    public static boolean isImageFileType(String path) {
        MediaFileType type = getFileType(path);
        if(null != type) {
            return isImageFileType(type.fileType);
        }
        return false;
    }

    //根据文本文件路径判断文件类型
    public static boolean isTextFileType(String path) {
        MediaFileType type = getFileType(path);
        if(null != type) {
            return isTextFileType(type.fileType);
        }
        return false;
    }

    //根据mime类型查看文件类型
    public static int getFileTypeForMimeType(String mimeType) {
        Integer value = sMimeTypeMap.get(mimeType);
        return (value == null ? 0 : value);
    }

    public static int getSubfolderNum(String path) {
        int i=0;
        File[] files = new File(path).listFiles();
        if (files == null)return -1;
        for (File f : files) {
            if (f.getName().indexOf(".") != 0){
                i++;
            }
        }
        return i;
    }

    public static int getSubFolderNum(DocumentFile documentFile){
        int i = 0;
        DocumentFile[] documentFiles = documentFile.listFiles();
        for (DocumentFile file : documentFiles) {
            if (file.getName()==null)continue;
            if (file.getName().indexOf(".") != 0) {
                i++;
            }
        }
        return i;
    }

    public static String getFileSize(long size) {
        StringBuffer bytes = new StringBuffer();
        DecimalFormat format = new DecimalFormat("###.0");
        if (size >= 1024 * 1024 * 1024) {
            double i = (size / (1024.0 * 1024.0 * 1024.0));
            bytes.append(format.format(i)).append("GB");
        }
        else if (size >= 1024 * 1024) {
            double i = (size / (1024.0 * 1024.0));
            bytes.append(format.format(i)).append("MB");
        }
        else if (size >= 1024) {
            double i = (size / (1024.0));
            bytes.append(format.format(i)).append("KB");
        }
        else {
            if (size <= 0) {
                bytes.append("0B");
            }
            else {
                bytes.append((int) size).append("B");
            }
        }
        return bytes.toString();

    }

    public static void SortFilesByName(List<File> fileList) {
        Collections.sort(fileList, (o1, o2) -> {
            if (o1.isDirectory() && o2.isFile())
                return -1;
            if (o1.isFile() && o2.isDirectory())
                return 1;
            return Collator.getInstance(java.util.Locale.CHINA).compare(o1.getName(), o2.getName());
        });
    }

    public static void SortFilesByName(List<FileInfo> fileList, boolean ignore) {
        Collections.sort(fileList, (o1, o2) -> {
            if (o1.isDirectory() && (!o2.isDirectory()))
                return -1;
            if ((!o1.isDirectory()) && o2.isDirectory())
                return 1;
            return Collator.getInstance(java.util.Locale.CHINA).compare(o1.getFileName(), o2.getFileName());
        });
    }

    public static ArrayList<String> getRelativePaths(String path){
        ArrayList<String>paths=new ArrayList<>();
        if (path.contains(BasicParams.BasicPath)){
            int startIndex=path.indexOf(BasicParams.BasicPath)+BasicParams.BasicPath.length();
            String rawPath=path.substring(startIndex);
            String[]p=rawPath.split("/");
            for (String l :
                    p) {
                if (!l.equals(""))paths.add(l);
            }
        }
        return paths;
    }

    public static boolean fileFilter(String path,String...extensions){
        path = path.toUpperCase();
        for (String extension : extensions) {
            String ext = extension.toUpperCase();
            if (path.endsWith(ext))return true;
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String mergeAbsolutePath(ArrayList<String>paths){
        return BasicParams.BasicPath+File.separator+String.join("/",paths);
    }

    //判断是否已经获取了受保护的文件夹的访问权限
    public static boolean isGrant(Context context, String path) {
        String uri = changeToUriNormal(path);
        for (UriPermission persistedUriPermission : context.getContentResolver().getPersistedUriPermissions()) {
            if (persistedUriPermission.isReadPermission() && persistedUriPermission.getUri().toString().equals(uri)) {
                return true;
            }
        }
        return false;
    }

    //将path转换成可解析的uri文本
    public static String changeToUriAndroidOrigin(String path) {
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        String path2 = path.replace("/storage/emulated/0/", "").replace("/", "%2F");
        return "content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3A" + path2;
    }

    public static String changeToUriNormal(String path) {
        path = path.replace("/storage/emulated/0/", "");
        path = Uri.encode(path).replace("/", "%2F");
        return ("content://com.android.externalstorage.documents/tree/primary%3A" + path);
    }

    public static String changeToPath(String uri){
        String path = uri;
        if (uri.contains("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata"))
            path = uri.replace("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3A", "").replace("%2F", "/");
        else if (uri.contains("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fobb"))
            path = uri.replace("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fobb/document/primary%3A", "").replace("%2F", "/");
        path = Uri.decode(path);
        return "/storage/emulated/0/"+path;
    }

    public static DocumentFile getDocumentFilePath(Context context, String path) {
        String path_pattern = "/storage/emulated/0";
        String rootUri = "content://com.android.externalstorage.documents/tree/primary%3A";
        if (path.contains("/Android/data")) {
            path_pattern = "/storage/emulated/0/Android/data";
            rootUri = "content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata";
        }
        if (path.contains("/Android/obb")) {
            path_pattern = "/storage/emulated/0/Android/obb";
            rootUri = "content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fobb";
        }
        //String treeUri = "content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3AAndroid%2Fdata";
        DocumentFile document = DocumentFile.fromTreeUri(context, Uri.parse(rootUri));
        path = path.replace(path_pattern, "");
        String[] parts = path.split("/");
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].equals(""))continue;
            String encoded_path = Uri.decode(parts[i]);
            if (document==null)break;
            document = document.findFile(encoded_path);
        }
        return document;
    }
}
