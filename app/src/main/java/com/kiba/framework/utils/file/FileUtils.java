package com.kiba.framework.utils.file;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.kiba.framework.MyApplication;
import com.kiba.framework.utils.LogUtils;
import com.kiba.framework.utils.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {


    public static Context getContext() {
        return MyApplication.getContext();
    }

    public static File saveToFile(Bitmap bitmap, String outPath, String fileName) throws IOException {
        // create a directory ArcGIS to save the file
        File root;
        File file = null;
        try {
            File fileDir = new File(outPath);
            boolean isDirectoryCreated = fileDir.exists();
            if (!isDirectoryCreated) {
                isDirectoryCreated = fileDir.mkdirs();
            }
            if (isDirectoryCreated) {
                file = new File(fileDir, fileName);
                // write the bitmap to PNG file
                FileOutputStream fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                // close the stream
                fos.flush();
                fos.close();
            }
        } catch (IOException e) {

        }
        return file;
    }

    /**
     * 从sd卡获取图片文件列表===符合到场类型
     *
     * @return
     */
    public static List<File> getImageFilesFromSD(String YWH, String DCLX, String appFileName ) {
        List<File> picFiles = new ArrayList<File>();
        // 得到该路径文件夹下所有的文件
        String root = Environment.getExternalStorageDirectory().getPath();
        File fileAll = new File(root, String.format("%s/%s文件夹", appFileName, YWH));

        File[] files = fileAll.listFiles();
        //将地图图片截图放到集合中最后一个
        File mapFile = null;
        // 将所有的文件存入ArrayList中,并过滤所有图片格式的文件
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.getName().contains(DCLX) && checkIsImageFile(file.getPath())) {
                if (file.getName().contains("MAP")) {
                    mapFile = file;
                    continue;
                }
                picFiles.add(file);
            }
        }
        if (mapFile != null) {
            picFiles.add(mapFile);
        }
        // 返回得到的图片文件列表
        return picFiles;
    }


    /**
     * 从sd卡删除指定路径文件
     *
     * @return
     */
    public static boolean deleteFileFromSD(String fileName,String CurrentAppUserFileDir) {
        String filePath = CurrentAppUserFileDir + String.format("/%s", fileName);
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 检查扩展名，得到图片格式的文件
     *
     * @param fName 文件名
     * @return
     */
    @SuppressLint("DefaultLocale")
    static boolean checkIsImageFile(String fName) {
        boolean isImageFile = false;
        // 获取扩展名
        String FileEnd = fName.substring(fName.lastIndexOf(".") + 1,
                fName.length()).toLowerCase();
        if (FileEnd.equals("jpg") || FileEnd.equals("png") || FileEnd.equals("gif")
                || FileEnd.equals("jpeg") || FileEnd.equals("bmp")) {
            isImageFile = true;
        } else {
            isImageFile = false;
        }
        return isImageFile;
    }

    private static final String LINE_SEP = System.getProperty("line.separator");

    //================文件路径获取===================//

    /**
     * SD卡是否存在
     *
     * @return
     */
    public static boolean isSDCardExist() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable();
    }

    /**
     * 获取磁盘的缓存目录
     *
     * @return SD卡不存在: /data/data/com.xxx.xxx/cache;<br>
     * 存在: /storage/emulated/0/Android/data/com.xxx.xxx/cache;
     */
    public static String getDiskCacheDir() {
        return isSDCardExist() && Utils.getContext().getExternalCacheDir() != null ? Utils.getContext().getExternalCacheDir().getPath() : Utils.getContext().getCacheDir().getPath();
    }

    /**
     * 获取磁盘的自定义缓存目录
     *
     * @return SD卡不存在: /data/data/com.xxx.xxx/cache/fileDir;<br>
     * 存在: /storage/emulated/0/Android/data/com.xxx.xxx/cache/fileDir;
     */
    public static String getDiskCacheDir(String fileDir) {
        return getDiskCacheDir() + File.separator + fileDir;
    }

    /**
     * 获取磁盘的文件目录
     *
     * @return SD卡不存在: /data/data/com.xxx.xxx/files;<br>
     * 存在: /storage/emulated/0/Android/data/com.xxx.xxx/files;
     */
    public static String getDiskFilesDir() {
        File file = Utils.getContext().getExternalFilesDir(null);
        return isSDCardExist() && file != null ? file.getPath() : Utils.getContext().getFilesDir().getPath();
    }

    /**
     * 获取磁盘的自定义文件目录
     *
     * @return SD卡不存在: /data/data/com.xxx.xxx/files/fileDir;<br>
     * 存在: /storage/emulated/0/Android/data/com.xxx.xxx/files/fileDir;
     */
    public static String getDiskFilesDir(String fileDir) {
        return getDiskFilesDir() + File.separator + fileDir;
    }

    /**
     * 获取磁盘目录
     *
     * @return SD卡不存在: /data/data/com.xxx.xxx/fileDir;<br>
     * 存在: /storage/emulated/0/fileDir; 或者 /storage/sdcard/fileDir
     */
    public static String getDiskDir(String fileDir) {
        if (isSDCardExist()) {
            return Environment.getExternalStorageDirectory() + File.separator + fileDir;
        } else {
            return Utils.getContext().getDir(fileDir, Context.MODE_PRIVATE).getPath();
        }
    }

    /**
     * 获取磁盘目录
     *
     * @return SD卡不存在: /data/data/com.xxx.xxx/com.xxx.xxx;<br>
     * 存在: /storage/emulated/0/com.xxx.xxx; 或者 /storage/sdcard/com.xxx.xxx
     */
    public static String getDiskDir() {
        return getDiskDir(Utils.getContext().getPackageName());
    }

    //=================判断文件是否存在========================//

    /**
     * 获取文件的路径
     *
     * @param dirPath  目录
     * @param fileName 文件名
     * @return 拼接的文件的路径
     */
    public static String getFilePath(String dirPath, String fileName) {
        return getDirPath(dirPath) + fileName;
    }

    /**
     * 获取文件目录的路径，自动补齐"/"
     *
     * @param dirPath 目录路径
     * @return 自动补齐"/"的目录路径
     */
    public static String getDirPath(String dirPath) {
        if (isSpace(dirPath)) {
            return "";
        }

        if (!dirPath.trim().endsWith(File.separator)) {
            dirPath = dirPath.trim() + File.separator;
        }
        return dirPath;
    }

    /**
     * 根据文件路径获取文件
     *
     * @param filePath 文件路径
     * @return 文件
     */
    @Nullable
    public static File getFileByPath(final String filePath) {
        return isSpace(filePath) ? null : new File(filePath);
    }

    /**
     * 判断文件是否存在
     *
     * @param file 文件
     * @return {@code true}: 存在<br>{@code false}: 不存在
     */
    public static boolean isFileExists(final File file) {
        if (file == null) {
            return false;
        }
        if (file.exists()) {
            return true;
        }
        return isFileExists(file.getAbsolutePath());
    }

    /**
     * 判断文件是否存在
     *
     * @param filePath 文件路径
     * @return {@code true}: 存在<br>{@code false}: 不存在
     */
    public static boolean isFileExists(final String filePath) {
        File file = getFileByPath(filePath);
        if (file == null) {
            return false;
        }
        if (file.exists()) {
            return true;
        }
        return isFileExistsApi29(filePath);
    }

    /**
     * Android 10判断文件是否存在的方法
     *
     * @param filePath 文件路径
     * @return {@code true}: 存在<br>{@code false}: 不存在
     */
    private static boolean isFileExistsApi29(String filePath) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            AssetFileDescriptor afd = null;
            try {
                Uri uri = Uri.parse(filePath);
                afd = SAFUtils.openAssetFileDescriptorWithException(uri);
                if (afd == null) {
                    return false;
                } else {
                    closeIOQuietly(afd);
                }
            } catch (FileNotFoundException e) {
                return false;
            } finally {
                closeIOQuietly(afd);
            }
            return true;
        }
        return false;
    }

    /**
     * 判断文件目录是否存在
     *
     * @param dirPath 文件目录路径
     * @return
     */
    public static boolean isFolderExist(final String dirPath) {
        return isFolderExist(getFileByPath(dirPath));
    }

    /**
     * 判断文件目录是否存在
     *
     * @param dir
     * @return
     */
    public static boolean isFolderExist(final File dir) {
        return dir != null && dir.exists() && dir.isDirectory();
    }

    //======================文件判断=======================//

    /**
     * 判断是否是目录
     *
     * @param dirPath 目录路径
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isDir(final String dirPath) {
        return isDir(getFileByPath(dirPath));
    }

    /**
     * 判断是否是目录
     *
     * @param file 文件
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isDir(final File file) {
        return file != null && file.exists() && file.isDirectory();
    }

    /**
     * 判断是否是文件
     *
     * @param filePath 文件路径
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isFile(final String filePath) {
        return isFile(getFileByPath(filePath));
    }

    /**
     * 判断是否是文件
     *
     * @param file 文件
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isFile(final File file) {
        return file != null && file.exists() && file.isFile();
    }

    //=======================重命名文件=======================//

    /**
     * 重命名文件
     *
     * @param filePath 文件路径
     * @param newName  新名称
     * @return {@code true}: 重命名成功<br>{@code false}: 重命名失败
     */
    public static boolean rename(final String filePath, final String newName) {
        return rename(getFileByPath(filePath), newName);
    }

    /**
     * 重命名文件
     *
     * @param file    文件
     * @param newName 新名称
     * @return {@code true}: 重命名成功<br>{@code false}: 重命名失败
     */
    public static boolean rename(final File file, final String newName) {
        // 文件为空返回 false
        if (file == null) {
            return false;
        }
        // 文件不存在返回 false
        if (!file.exists()) {
            return false;
        }
        // 新的文件名为空返回 false
        if (isSpace(newName)) {
            return false;
        }
        // 如果文件名没有改变返回 true
        if (newName.equals(file.getName())) {
            return true;
        }
        File newFile = new File(file.getParent() + File.separator + newName);
        // 如果重命名的文件已存在返回 false
        return !newFile.exists()
                && file.renameTo(newFile);
    }

    //=======================创建文件=======================//

    /**
     * 文件不存在就创建，存在则返回文件，不存在则创建并返回文件
     *
     * @param filePath
     * @return 文件
     */
    public static File isFileNotExistCreate(final String filePath) {
        File file = getFileByPath(filePath);
        if (file == null) {
            return null;
        }
        // 如果存在，是文件则返回
        if (file.exists()) {
            return file;
        }
        //目录没有创建成功，直接返回null
        if (!createOrExistsDir(file.getParentFile())) {
            return null;
        }
        try {
            file.createNewFile();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 判断目录是否存在，不存在则判断是否创建成功
     *
     * @param dirPath 目录路径
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsDir(final String dirPath) {
        return createOrExistsDir(getFileByPath(dirPath));
    }

    /**
     * 判断目录是否存在，不存在则判断是否创建成功
     *
     * @param file 文件
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsDir(final File file) {
        // 如果存在，是目录则返回 true，是文件则返回 false，不存在则返回是否创建成功
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    /**
     * 判断文件是否存在，不存在则判断是否创建成功
     *
     * @param filePath 文件路径
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsFile(final String filePath) {
        return createOrExistsFile(getFileByPath(filePath));
    }

    /**
     * 判断文件是否存在，不存在则判断是否创建成功
     *
     * @param file 文件
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsFile(final File file) {
        if (file == null) {
            return false;
        }
        // 如果存在，是文件则返回 true，是目录则返回 false
        if (file.exists()) {
            return file.isFile();
        }
        if (!createOrExistsDir(file.getParentFile())) {
            return false;
        }
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断文件是否存在，存在则在创建之前删除
     *
     * @param filePath 文件路径
     * @return {@code true}: 创建成功<br>{@code false}: 创建失败
     */
    public static boolean createFileByDeleteOldFile(final String filePath) {
        return createFileByDeleteOldFile(getFileByPath(filePath));
    }

    /**
     * 判断文件是否存在，存在则在创建之前删除
     *
     * @param file 文件
     * @return {@code true}: 创建成功<br>{@code false}: 创建失败
     */
    public static boolean createFileByDeleteOldFile(final File file) {
        if (file == null) {
            return false;
        }
        // 文件存在并且删除失败返回 false
        if (file.exists() && !file.delete()) {
            return false;
        }
        // 创建目录失败返回 false
        if (!createOrExistsDir(file.getParentFile())) {
            return false;
        }
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    //=======================文件复制或移动=======================//

    /**
     * 复制或移动目录
     *
     * @param srcDirPath  源目录路径
     * @param destDirPath 目标目录路径
     * @param listener    是否覆盖监听器
     * @param isMove      是否移动
     * @return {@code true}: 复制或移动成功<br>{@code false}: 复制或移动失败
     */
    private static boolean copyOrMoveDir(final String srcDirPath,
                                         final String destDirPath,
                                         final OnReplaceListener listener,
                                         final boolean isMove) {
        return copyOrMoveDir(getFileByPath(srcDirPath),
                getFileByPath(destDirPath),
                listener,
                isMove
        );
    }

    /**
     * 复制或移动目录
     *
     * @param srcDir   源目录
     * @param destDir  目标目录
     * @param listener 是否覆盖监听器
     * @param isMove   是否移动
     * @return {@code true}: 复制或移动成功<br>{@code false}: 复制或移动失败
     */
    private static boolean copyOrMoveDir(final File srcDir,
                                         final File destDir,
                                         final OnReplaceListener listener,
                                         final boolean isMove) {
        if (srcDir == null || destDir == null) {
            return false;
        }
        // 如果目标目录在源目录中则返回 false，看不懂的话好好想想递归怎么结束
        // srcPath : F:\\MyGithub\\AndroidUtilCode\\utilcode\\src\\test\\res
        // destPath: F:\\MyGithub\\AndroidUtilCode\\utilcode\\src\\test\\res1
        // 为防止以上这种情况出现出现误判，须分别在后面加个路径分隔符
        String srcPath = srcDir.getPath() + File.separator;
        String destPath = destDir.getPath() + File.separator;
        if (destPath.contains(srcPath)) {
            return false;
        }
        // 源文件不存在或者不是目录则返回 false
        if (!srcDir.exists() || !srcDir.isDirectory()) {
            return false;
        }
        if (destDir.exists()) {
            if (listener == null || listener.onReplace()) {// 需要覆盖则删除旧目录
                if (!deleteAllInDir(destDir)) {// 删除文件失败的话返回 false
                    return false;
                }
            } else {// 不需要覆盖直接返回即可 true
                return true;
            }
        }
        // 目标目录不存在返回 false
        if (!createOrExistsDir(destDir)) {
            return false;
        }
        File[] files = srcDir.listFiles();
        for (File file : files) {
            File oneDestFile = new File(destPath + file.getName());
            if (file.isFile()) {
                // 如果操作失败返回 false
                if (!copyOrMoveFile(file, oneDestFile, listener, isMove)) {
                    return false;
                }
            } else if (file.isDirectory()) {
                // 如果操作失败返回 false
                if (!copyOrMoveDir(file, oneDestFile, listener, isMove)) {
                    return false;
                }
            }
        }
        return !isMove || deleteDir(srcDir);
    }

    /**
     * 复制或移动文件
     *
     * @param srcFilePath  源文件路径
     * @param destFilePath 目标文件路径
     * @param listener     是否覆盖监听器
     * @param isMove       是否移动
     * @return {@code true}: 复制或移动成功<br>{@code false}: 复制或移动失败
     */
    private static boolean copyOrMoveFile(final String srcFilePath,
                                          final String destFilePath,
                                          final OnReplaceListener listener,
                                          final boolean isMove) {
        return copyOrMoveFile(getFileByPath(srcFilePath),
                getFileByPath(destFilePath),
                listener,
                isMove
        );
    }

    /**
     * 复制或移动文件
     *
     * @param srcFile  源文件
     * @param destFile 目标文件
     * @param listener 是否覆盖监听器
     * @param isMove   是否移动
     * @return {@code true}: 复制或移动成功<br>{@code false}: 复制或移动失败
     */
    private static boolean copyOrMoveFile(final File srcFile,
                                          final File destFile,
                                          final OnReplaceListener listener,
                                          final boolean isMove) {
        if (srcFile == null || destFile == null) {
            return false;
        }
        // 如果源文件和目标文件相同则返回 false
        if (srcFile.equals(destFile)) {
            return false;
        }
        // 源文件不存在或者不是文件则返回 false
        if (!srcFile.exists() || !srcFile.isFile()) {
            return false;
        }
        if (destFile.exists()) {// 目标文件存在
            if (listener == null || listener.onReplace()) {// 需要覆盖则删除旧文件
                if (!destFile.delete()) {// 删除文件失败的话返回 false
                    return false;
                }
            } else {// 不需要覆盖直接返回即可 true
                return true;
            }
        }
        // 目标目录不存在返回 false
        if (!createOrExistsDir(destFile.getParentFile())) {
            return false;
        }
        try {
            return writeFileFromIS(destFile, new FileInputStream(srcFile), false)
                    && !(isMove && !deleteFile(srcFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * 将输入流写入文件
     *
     * @param file   文件
     * @param is     输入流
     * @param append 是否追加在文件末
     * @return {@code true}: 写入成功<br>{@code false}: 写入失败
     */
    public static boolean writeFileFromIS(final File file,
                                          final InputStream is,
                                          final boolean append) {
        int sBufferSize = 8192;
        if (!createOrExistsFile(file) || is == null) {
            return false;
        }
        OutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file, append));
            byte[] data = new byte[sBufferSize];
            int len;
            while ((len = is.read(data, 0, sBufferSize)) != -1) {
                os.write(data, 0, len);
            }
            os.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeIO(is, os);
        }
    }
    //=======================文件复制=======================//

    /**
     * 复制目录
     *
     * @param srcDirPath  源目录路径
     * @param destDirPath 目标目录路径
     * @param listener    是否覆盖监听器
     * @return {@code true}: 复制成功<br>{@code false}: 复制失败
     */
    public static boolean copyDir(final String srcDirPath,
                                  final String destDirPath,
                                  final OnReplaceListener listener) {
        return copyDir(getFileByPath(srcDirPath), getFileByPath(destDirPath), listener);
    }

    /**
     * 复制目录
     *
     * @param srcDir   源目录
     * @param destDir  目标目录
     * @param listener 是否覆盖监听器
     * @return {@code true}: 复制成功<br>{@code false}: 复制失败
     */
    public static boolean copyDir(final File srcDir,
                                  final File destDir,
                                  final OnReplaceListener listener) {
        return copyOrMoveDir(srcDir, destDir, listener, false);
    }

    /**
     * 复制文件
     *
     * @param srcFilePath  源文件路径
     * @param destFilePath 目标文件路径
     * @param listener     是否覆盖监听器
     * @return {@code true}: 复制成功<br>{@code false}: 复制失败
     */
    public static boolean copyFile(final String srcFilePath,
                                   final String destFilePath,
                                   final OnReplaceListener listener) {
        return copyFile(getFileByPath(srcFilePath), getFileByPath(destFilePath), listener);
    }

    /**
     * 复制文件
     *
     * @param srcFile  源文件
     * @param destFile 目标文件
     * @param listener 是否覆盖监听器
     * @return {@code true}: 复制成功<br>{@code false}: 复制失败
     */
    public static boolean copyFile(final File srcFile,
                                   final File destFile,
                                   final OnReplaceListener listener) {
        return copyOrMoveFile(srcFile, destFile, listener, false);
    }

    //=======================文件移动=======================//

    /**
     * 移动目录
     *
     * @param srcDirPath  源目录路径
     * @param destDirPath 目标目录路径
     * @param listener    是否覆盖监听器
     * @return {@code true}: 移动成功<br>{@code false}: 移动失败
     */
    public static boolean moveDir(final String srcDirPath,
                                  final String destDirPath,
                                  final OnReplaceListener listener) {
        return moveDir(getFileByPath(srcDirPath), getFileByPath(destDirPath), listener);
    }

    /**
     * 移动目录
     *
     * @param srcDir   源目录
     * @param destDir  目标目录
     * @param listener 是否覆盖监听器
     * @return {@code true}: 移动成功<br>{@code false}: 移动失败
     */
    public static boolean moveDir(final File srcDir,
                                  final File destDir,
                                  final OnReplaceListener listener) {
        return copyOrMoveDir(srcDir, destDir, listener, true);
    }

    /**
     * 移动文件
     *
     * @param srcFilePath  源文件路径
     * @param destFilePath 目标文件路径
     * @param listener     是否覆盖监听器
     * @return {@code true}: 移动成功<br>{@code false}: 移动失败
     */
    public static boolean moveFile(final String srcFilePath,
                                   final String destFilePath,
                                   final OnReplaceListener listener) {
        return moveFile(getFileByPath(srcFilePath), getFileByPath(destFilePath), listener);
    }

    /**
     * 移动文件
     *
     * @param srcFile  源文件
     * @param destFile 目标文件
     * @param listener 是否覆盖监听器
     * @return {@code true}: 移动成功<br>{@code false}: 移动失败
     */
    public static boolean moveFile(final File srcFile,
                                   final File destFile,
                                   final OnReplaceListener listener) {
        return copyOrMoveFile(srcFile, destFile, listener, true);
    }

    //=======================文件删除=======================//

    /**
     * 删除文件或目录
     *
     * @param filePath 文件的路径
     * @return {@code true}: 删除成功<br>{@code false}: 删除失败
     */
    public static boolean delete(final String filePath) {
        return delete(getFileByPath(filePath));
    }

    /**
     * 删除文件或目录
     *
     * @param file 文件
     * @return {@code true}: 删除成功<br>{@code false}: 删除失败
     */
    public static boolean delete(final File file) {
        if (file == null) {
            return false;
        }
        if (file.isDirectory()) {
            return deleteDir(file);
        }
        return deleteFile(file);
    }

    /**
     * 删除目录
     *
     * @param dirPath 目录路径
     * @return {@code true}: 删除成功<br>{@code false}: 删除失败
     */
    public static boolean deleteDir(final String dirPath) {
        return deleteDir(getFileByPath(dirPath));
    }

    /**
     * 删除目录
     *
     * @param dir 目录
     * @return {@code true}: 删除成功<br>{@code false}: 删除失败
     */
    public static boolean deleteDir(final File dir) {
        if (dir == null) {
            return false;
        }
        // 目录不存在返回 true
        if (!dir.exists()) {
            return true;
        }
        // 不是目录返回 false
        if (!dir.isDirectory()) {
            return false;
        }
        // 现在文件存在且是文件夹
        File[] files = dir.listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {
                if (file.isFile()) {
                    if (!file.delete()) {
                        return false;
                    }
                } else if (file.isDirectory()) {
                    if (!deleteDir(file)) {
                        return false;
                    }
                }
            }
        }
        return dir.delete();
    }

    /**
     * 删除文件
     *
     * @param srcFilePath 文件路径
     * @return {@code true}: 删除成功<br>{@code false}: 删除失败
     */
    public static boolean deleteFile(final String srcFilePath) {
        return deleteFile(getFileByPath(srcFilePath));
    }

    /**
     * 删除文件
     *
     * @param file 文件
     * @return {@code true}: 删除成功<br>{@code false}: 删除失败
     */
    public static boolean deleteFile(final File file) {
        return file != null && (!file.exists() || file.isFile() && file.delete());
    }

    /**
     * 安全删除文件.
     *
     * @param file 需要删除的文件
     * @return 是否删除成功
     */
    public static boolean deleteFileSafely(File file) {
        if (file != null && file.exists()) {
            String tmpPath = file.getParent() + File.separator + System.currentTimeMillis();
            File tmp = new File(tmpPath);
            file.renameTo(tmp);
            return tmp.delete();
        }
        return false;
    }

    /**
     * 删除目录下所有东西
     *
     * @param dirPath 目录路径
     * @return {@code true}: 删除成功<br>{@code false}: 删除失败
     */
    public static boolean deleteAllInDir(final String dirPath) {
        return deleteAllInDir(getFileByPath(dirPath));
    }

    /**
     * 删除目录下所有东西
     *
     * @param dir 目录
     * @return {@code true}: 删除成功<br>{@code false}: 删除失败
     */
    public static boolean deleteAllInDir(final File dir) {
        return deleteFilesInDirWithFilter(dir, new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return true;
            }
        });
    }

    /**
     * 删除目录下所有文件
     *
     * @param dirPath 目录路径
     * @return {@code true}: 删除成功<br>{@code false}: 删除失败
     */
    public static boolean deleteFilesInDir(final String dirPath) {
        return deleteFilesInDir(getFileByPath(dirPath));
    }

    /**
     * 删除目录下所有文件
     *
     * @param dir 目录
     * @return {@code true}: 删除成功<br>{@code false}: 删除失败
     */
    public static boolean deleteFilesInDir(final File dir) {
        return deleteFilesInDirWithFilter(dir, new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile();
            }
        });
    }

    /**
     * 删除目录下所有过滤的文件【目录不删除】
     *
     * @param dirPath 目录路径
     * @param filter  过滤器
     * @return {@code true}: 删除成功<br>{@code false}: 删除失败
     */
    public static boolean deleteFilesInDirWithFilter(final String dirPath,
                                                     final FileFilter filter) {
        return deleteFilesInDirWithFilter(getFileByPath(dirPath), filter);
    }

    /**
     * 删除目录下所有过滤的文件【目录不删除】
     *
     * @param dir    目录
     * @param filter 过滤器
     * @return {@code true}: 删除成功<br>{@code false}: 删除失败
     */
    public static boolean deleteFilesInDirWithFilter(final File dir, final FileFilter filter) {
        if (dir == null) {
            return false;
        }
        // 目录不存在返回 true
        if (!dir.exists()) {
            return true;
        }
        // 不是目录返回 false
        if (!dir.isDirectory()) {
            return false;
        }
        // 现在文件存在且是文件夹
        File[] files = dir.listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {
                if (filter.accept(file)) {
                    if (file.isFile()) {
                        if (!file.delete()) {
                            return false;
                        }
                    } else if (file.isDirectory()) {
                        if (!deleteDir(file)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    //=======================文件遍历、过滤、获取=======================//

    /**
     * 获取目录下所有文件
     * <p>不递归进子目录</p>
     *
     * @param dirPath 目录路径
     * @return 文件链表
     */
    public static List<File> listFilesInDir(final String dirPath) {
        return listFilesInDir(dirPath, false);
    }

    /**
     * 获取目录下所有文件
     * <p>不递归进子目录</p>
     *
     * @param dir 目录
     * @return 文件链表
     */
    public static List<File> listFilesInDir(final File dir) {
        return listFilesInDir(dir, false);
    }

    /**
     * 获取目录下所有文件
     *
     * @param dirPath     目录路径
     * @param isRecursive 是否递归进子目录
     * @return 文件链表
     */
    public static List<File> listFilesInDir(final String dirPath, final boolean isRecursive) {
        return listFilesInDir(getFileByPath(dirPath), isRecursive);
    }

    /**
     * 获取目录下所有文件
     *
     * @param dir         目录
     * @param isRecursive 是否递归进子目录
     * @return 文件链表
     */
    public static List<File> listFilesInDir(final File dir, final boolean isRecursive) {
        return listFilesInDirWithFilter(dir, new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return true;
            }
        }, isRecursive);
    }

    /**
     * 获取目录下所有过滤的文件
     * <p>不递归进子目录</p>
     *
     * @param dirPath 目录路径
     * @param filter  过滤器
     * @return 文件链表
     */
    public static List<File> listFilesInDirWithFilter(final String dirPath,
                                                      final FileFilter filter) {
        return listFilesInDirWithFilter(getFileByPath(dirPath), filter, false);
    }

    /**
     * 获取目录下所有过滤的文件
     * <p>不递归进子目录</p>
     *
     * @param dir    目录
     * @param filter 过滤器
     * @return 文件链表
     */
    public static List<File> listFilesInDirWithFilter(final File dir,
                                                      final FileFilter filter) {
        return listFilesInDirWithFilter(dir, filter, false);
    }

    /**
     * 获取目录下所有过滤的文件
     *
     * @param dirPath     目录路径
     * @param filter      过滤器
     * @param isRecursive 是否递归进子目录
     * @return 文件链表
     */
    public static List<File> listFilesInDirWithFilter(final String dirPath,
                                                      final FileFilter filter,
                                                      final boolean isRecursive) {
        return listFilesInDirWithFilter(getFileByPath(dirPath), filter, isRecursive);
    }

    /**
     * 获取目录下所有过滤的文件
     *
     * @param dir         目录
     * @param filter      过滤器
     * @param isRecursive 是否递归进子目录
     * @return 文件链表
     */
    public static List<File> listFilesInDirWithFilter(final File dir,
                                                      final FileFilter filter,
                                                      final boolean isRecursive) {
        if (!isDir(dir)) {
            return null;
        }
        List<File> list = new ArrayList<>();
        File[] files = dir.listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {
                if (filter.accept(file)) {
                    list.add(file);
                }
                if (isRecursive && file.isDirectory()) {
                    //noinspection ConstantConditions
                    list.addAll(listFilesInDirWithFilter(file, filter, true));
                }
            }
        }
        return list;
    }

    //=======================文件信息（修改时间、大小、编码格式等）=======================//

    /**
     * 获取文件最后修改的毫秒时间戳
     *
     * @param filePath 文件路径
     * @return 文件最后修改的毫秒时间戳
     */

    public static long getFileLastModified(final String filePath) {
        return getFileLastModified(getFileByPath(filePath));
    }

    /**
     * 获取文件最后修改的毫秒时间戳
     *
     * @param file 文件
     * @return 文件最后修改的毫秒时间戳
     */
    public static long getFileLastModified(final File file) {
        if (file == null) {
            return -1;
        }
        return file.lastModified();
    }

    /**
     * 简单获取文件编码格式
     *
     * @param filePath 文件路径
     * @return 文件编码
     */
    public static String getFileCharsetSimple(final String filePath) {
        return getFileCharsetSimple(getFileByPath(filePath));
    }

    /**
     * 简单获取文件编码格式
     *
     * @param file 文件
     * @return 文件编码
     */
    public static String getFileCharsetSimple(final File file) {
        int p = 0;
        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(file));
            p = (is.read() << 8) + is.read();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeIO(is);
        }
        switch (p) {
            case 0xefbb:
                return "UTF-8";
            case 0xfffe:
                return "Unicode";
            case 0xfeff:
                return "UTF-16BE";
            default:
                return "GBK";
        }
    }

    /**
     * 获取文件行数
     *
     * @param filePath 文件路径
     * @return 文件行数
     */
    public static int getFileLines(final String filePath) {
        return getFileLines(getFileByPath(filePath));
    }

    /**
     * 获取文件行数
     * <p>比 readLine 要快很多</p>
     *
     * @param file 文件
     * @return 文件行数
     */
    public static int getFileLines(final File file) {
        int count = 1;
        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(file));
            byte[] buffer = new byte[1024];
            int readChars;
            if (LINE_SEP.endsWith("\n")) {
                while ((readChars = is.read(buffer, 0, 1024)) != -1) {
                    for (int i = 0; i < readChars; ++i) {
                        if (buffer[i] == '\n') {
                            ++count;
                        }
                    }
                }
            } else {
                while ((readChars = is.read(buffer, 0, 1024)) != -1) {
                    for (int i = 0; i < readChars; ++i) {
                        if (buffer[i] == '\r') {
                            ++count;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeIO(is);
        }
        return count;
    }

    /**
     * 获取目录大小
     *
     * @param dirPath 目录路径
     * @return 文件大小
     */
    public static String getDirSize(final String dirPath) {
        return getDirSize(getFileByPath(dirPath));
    }

    /**
     * 获取目录大小
     *
     * @param dir 目录
     * @return 文件大小
     */
    public static String getDirSize(final File dir) {
        long len = getDirLength(dir);
        return len == -1 ? "" : byte2FitMemorySize(len);
    }

    /**
     * 获取文件大小
     *
     * @param filePath 文件路径
     * @return 文件大小
     */
    public static String getFileSize(final String filePath) {
        long len = getFileLength(filePath);
        return len == -1 ? "" : byte2FitMemorySize(len);
    }

    /**
     * 获取文件大小
     *
     * @param file 文件
     * @return 文件大小
     */
    public static String getFileSize(final File file) {
        long len = getFileLength(file);
        return len == -1 ? "" : byte2FitMemorySize(len);
    }

    /**
     * 获取目录长度
     *
     * @param dirPath 目录路径
     * @return 目录长度
     */
    public static long getDirLength(final String dirPath) {
        return getDirLength(getFileByPath(dirPath));
    }

    /**
     * 获取目录长度
     *
     * @param dir 目录
     * @return 目录长度
     */
    public static long getDirLength(final File dir) {
        if (!isDir(dir)) {
            return -1;
        }
        long len = 0;
        File[] files = dir.listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {
                if (file.isDirectory()) {
                    len += getDirLength(file);
                } else {
                    len += file.length();
                }
            }
        }
        return len;
    }

    /**
     * 获取文件长度
     *
     * @param filePath 文件路径
     * @return 文件长度
     */
    public static long getFileLength(final String filePath) {
        boolean isURL = filePath.matches("[a-zA-z]+://[^\\s]*");
        if (isURL) {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(filePath).openConnection();
                conn.setRequestProperty("Accept-Encoding", "identity");
                conn.connect();
                if (conn.getResponseCode() == 200) {
                    return conn.getContentLength();
                }
                return -1;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return getFileLength(getFileByPath(filePath));
    }

    /**
     * 获取文件长度
     *
     * @param file 文件
     * @return 文件长度
     */
    public static long getFileLength(final File file) {
        if (!isFile(file)) {
            return -1;
        }
        return file.length();
    }






    /**
     * 获取全路径中的最长目录
     *
     * @param file 文件
     * @return filePath 最长目录
     */
    public static String getDirName(final File file) {
        if (file == null) {
            return null;
        }
        return getDirName(file.getPath());
    }

    /**
     * 获取全路径中的最长目录
     *
     * @param filePath 文件路径
     * @return filePath 最长目录
     */
    public static String getDirName(final String filePath) {
        if (isSpace(filePath)) {
            return filePath;
        }
        int lastSep = filePath.lastIndexOf(File.separator);
        return lastSep == -1 ? "" : filePath.substring(0, lastSep + 1);
    }

    /**
     * 获取全路径中的文件名
     *
     * @param file 文件
     * @return 文件名
     */
    public static String getFileName(final File file) {
        if (file == null) {
            return null;
        }
        return getFileName(file.getPath());
    }

    /**
     * 获取全路径中的文件名
     *
     * @param filePath 文件路径
     * @return 文件名
     */
    public static String getFileName(final String filePath) {
        if (isSpace(filePath)) {
            return filePath;
        }
        int lastSep = filePath.lastIndexOf(File.separator);
        return lastSep == -1 ? filePath : filePath.substring(lastSep + 1);
    }

    /**
     * <p>获取全路径中的不带拓展名的文件名(不带路径)</p>
     * <p>例如:aa/bb/cc.png  --> cc </p>
     *
     * @param file 文件
     * @return 不带拓展名的文件名
     */
    public static String getFileNameNoExtension(final File file) {
        if (file == null) {
            return null;
        }
        return getFileNameNoExtension(file.getPath());
    }

    /**
     * <p>获取全路径中的不带拓展名的文件名(不带路径)</p>
     * <p>例如:aa/bb/cc.png  --> cc </p>
     *
     * @param filePath 文件路径
     * @return 不带拓展名的文件名
     */
    public static String getFileNameNoExtension(final String filePath) {
        if (isSpace(filePath)) {
            return filePath;
        }
        int lastPoi = filePath.lastIndexOf('.');
        int lastSep = filePath.lastIndexOf(File.separator);
        if (lastSep == -1) {
            return (lastPoi == -1 ? filePath : filePath.substring(0, lastPoi));
        }
        if (lastPoi == -1 || lastSep > lastPoi) {
            return filePath.substring(lastSep + 1);
        }
        return filePath.substring(lastSep + 1, lastPoi);
    }

    /**
     * 获取全路径中的不带拓展名的文件名(带路径）
     * <p>
     * <p>例如:aa/bb/cc.png  --> aa/bb/cc </p>
     *
     * @param filePath 文件路径
     * @return 不带拓展名的文件名(带路径 ）
     */
    public static String getFileNameNoExtensionWithPath(final String filePath) {
        if (isSpace(filePath)) {
            return filePath;
        }
        int lastPoi = filePath.lastIndexOf('.');
        if (lastPoi == -1) {
            return filePath;
        }
        return filePath.substring(0, lastPoi);
    }

    /**
     * 改变文件的拓展名
     * <p>
     * <p>例如:aa/bb/cc.png  --> aa/bb/cc.xxx </p>
     *
     * @param filePath      文件路径
     * @param extensionName 拓展名 [.xxx]
     * @return 改变拓展名的文件路径
     */
    public static String changeFileExtension(final String filePath, final String extensionName) {
        if (isSpace(filePath)) {
            return filePath;
        }
        int lastPoi = filePath.lastIndexOf('.');
        int lastSep = filePath.lastIndexOf(File.separator);
        if (lastPoi == -1 || lastSep >= lastPoi) {
            return "";
        }
        return filePath.substring(0, lastPoi) + extensionName;
    }

    /**
     * 获取全路径中的文件拓展名
     *
     * @param file 文件
     * @return 文件拓展名
     */
    public static String getFileExtension(final File file) {
        if (file == null) {
            return null;
        }
        return getFileExtension(file.getPath());
    }

    /**
     * 获取全路径中的文件拓展名
     *
     * @param filePath 文件路径
     * @return 文件拓展名
     */
    public static String getFileExtension(final String filePath) {
        if (isSpace(filePath)) {
            return filePath;
        }
        int lastPoi = filePath.lastIndexOf('.');
        int lastSep = filePath.lastIndexOf(File.separator);
        if (lastPoi == -1 || lastSep >= lastPoi) {
            return "";
        }
        return filePath.substring(lastPoi + 1);
    }

    ///////////////////////////////////////////////////////////////////////////
    // copy from ConvertUtils
    ///////////////////////////////////////////////////////////////////////////

    private static final char[] hexDigits =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * byteArr 转 hexString
     * <p>例如：</p>
     * bytes2HexString(new byte[] { 0, (byte) 0xa8 }) returns 00A8
     *
     * @param bytes 字节数组
     * @return 16 进制大写字符串
     */
    private static String bytes2HexString(final byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        int len = bytes.length;
        if (len <= 0) {
            return null;
        }
        char[] ret = new char[len << 1];
        for (int i = 0, j = 0; i < len; i++) {
            ret[j++] = hexDigits[bytes[i] >>> 4 & 0x0f];
            ret[j++] = hexDigits[bytes[i] & 0x0f];
        }
        return new String(ret);
    }

    /**
     * 字节数转合适内存大小
     * <p>保留 3 位小数</p>
     *
     * @param byteNum 字节数
     * @return 合适内存大小
     */
    @SuppressLint("DefaultLocale")
    public static String byte2FitMemorySize(final long byteNum) {
        if (byteNum < 0) {
            return "shouldn't be less than zero!";
        } else if (byteNum < 1024) {
            return String.format("%.3fB", (double) byteNum);
        } else if (byteNum < 1048576) {
            return String.format("%.3fKB", (double) byteNum / 1024);
        } else if (byteNum < 1073741824) {
            return String.format("%.3fMB", (double) byteNum / 1048576);
        } else {
            return String.format("%.3fGB", (double) byteNum / 1073741824);
        }
    }

    /**
     * 字节数转合适内存大小
     * <p>保留 N 位小数</p>
     *
     * @param byteNum 字节数
     * @param length  小数位数
     * @return
     */
    @SuppressLint("DefaultLocale")
    public static String byte2FitMemorySize(final long byteNum, int length) {
        if (byteNum < 0) {
            return "shouldn't be less than zero!";
        } else if (byteNum < 1024) {
            return String.format("%." + length + "fB", (double) byteNum);
        } else if (byteNum < 1048576) {
            return String.format("%." + length + "fKB", (double) byteNum / 1024);
        } else if (byteNum < 1073741824) {
            return String.format("%." + length + "fMB", (double) byteNum / 1048576);
        } else {
            return String.format("%." + length + "fGB", (double) byteNum / 1073741824);
        }
    }

    /**
     * 通知系统去扫描文件
     *
     * @param filePath The path of file.
     */
    public static void notifySystemToScan(final String filePath) {
        notifySystemToScan(getFileByPath(filePath));
    }

    /**
     * 通知系统去扫描文件.
     *
     * @param file The file.
     */
    public static void notifySystemToScan(final File file) {
        if (file == null || !file.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = PathUtils.getUriForFile(file);
        intent.setData(uri);
        Utils.getContext().sendBroadcast(intent);
    }

    /**
     * 获取手机文件大小的总和
     *
     * @param anyPathInFs 任意文件路径.
     * @return 手机文件大小的总和
     */
    public static long getFsTotalSize(String anyPathInFs) {
        if (TextUtils.isEmpty(anyPathInFs)) {
            return 0;
        }
        StatFs statFs = new StatFs(anyPathInFs);
        long blockSize;
        long totalSize;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = statFs.getBlockSizeLong();
            totalSize = statFs.getBlockCountLong();
        } else {
            blockSize = statFs.getBlockSize();
            totalSize = statFs.getBlockCount();
        }
        return blockSize * totalSize;
    }

    /**
     * 获取手机文件可用大小
     *
     * @param anyPathInFs 任意文件路径
     * @return 手机文件可用大小
     */
    public static long getFsAvailableSize(final String anyPathInFs) {
        if (TextUtils.isEmpty(anyPathInFs)) {
            return 0;
        }
        StatFs statFs = new StatFs(anyPathInFs);
        long blockSize;
        long availableSize;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = statFs.getBlockSizeLong();
            availableSize = statFs.getAvailableBlocksLong();
        } else {
            blockSize = statFs.getBlockSize();
            availableSize = statFs.getAvailableBlocks();
        }
        return blockSize * availableSize;
    }

    private static boolean isSpace(final String s) {
        if (s == null) {
            return true;
        }
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public interface OnReplaceListener {
        boolean onReplace();
    }


    /**
     * 读取assert下的txt文件
     *
     * @param fileName     文件名
     * @param encodingCode 字符编码 "utf-8"
     * @return 文本内容
     */
    public static String readStringFromAssert(String fileName, String encodingCode) {
        InputStream inputStream = null;
        try {
            inputStream =  getContext().getResources().getAssets().open(fileName);
            if (inputStream != null) {
                byte[] buffer = new byte[inputStream.available()];
                inputStream.read(buffer);
                return new String(buffer, encodingCode);
            }
        } catch (Exception e) {
            LogUtils.e(e);
        } finally {
            closeIO(inputStream);
        }
        return "";
    }
    /**
     * 关闭 IO
     *
     * @param closeables closeables
     */
    public static void closeIO(final Closeable... closeables) {
        if (closeables == null) {
            return;
        }
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 安静关闭 IO
     *
     * @param closeables closeables
     */
    public static void closeIOQuietly(final Closeable... closeables) {
        if (closeables == null) {
            return;
        }
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException ignored) {
                }
            }
        }
    }
}
