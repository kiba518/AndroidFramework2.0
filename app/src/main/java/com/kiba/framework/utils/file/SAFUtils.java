package com.kiba.framework.utils.file;

import android.content.ContentValues;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;
import androidx.annotation.StringDef;

import com.kiba.framework.utils.LogUtils;
import com.kiba.framework.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class SAFUtils {

    /**
     * 只读模式
     */
    public static final String MODE_READ_ONLY = "r";

    /**
     * 只写模式
     */
    public static final String MODE_WRITE_ONLY = "w";

    /**
     * 读写模式
     */
    public static final String MODE_READ_WRITE = "rw";

    /**
     * 文件读写模式
     */
    @StringDef({MODE_READ_ONLY, MODE_WRITE_ONLY, MODE_READ_WRITE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface FileMode {
    }

    private SAFUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 当前应用是否是以兼容模式运行;
     *
     * @return true: 是，false: 不是
     */
    public static boolean isExternalStorageLegacy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return Environment.isExternalStorageLegacy();
        }
        return false;
    }

    /**
     * 是否是分区存储模式：在公共目录下file的api无效了
     *
     * @return 是否是分区存储模式
     */
    public static boolean isScopedStorageMode() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Environment.isExternalStorageLegacy();
    }

    //================从uri资源符中获取输入/输出流====================//

    /**
     * 从uri资源符中获取输入流
     *
     * @param uri 文本资源符
     * @return InputStream
     */
    public static InputStream openInputStream(Uri uri) {
        try {
            return openInputStreamWithException(uri);
        } catch (FileNotFoundException e) {
            LogUtils.e(e);
        }
        return null;
    }

    /**
     * 从uri资源符中获取输入流
     *
     * @param uri 文本资源符
     * @return InputStream
     */
    public static InputStream openInputStreamWithException(Uri uri) throws FileNotFoundException {
        return Utils.getContentResolver().openInputStream(uri);
    }

    /**
     * 从uri资源符中获取输出流
     *
     * @param uri 文本资源符
     * @return OutputStream
     */
    public static OutputStream openOutputStream(Uri uri) {
        try {
            return openOutputStreamWithException(uri);
        } catch (FileNotFoundException e) {
            LogUtils.e(e);
        }
        return null;
    }

    /**
     * 从uri资源符中获取输出流
     *
     * @param uri 文本资源符
     * @return OutputStream
     */
    public static OutputStream openOutputStreamWithException(Uri uri) throws FileNotFoundException {
        return Utils.getContentResolver().openOutputStream(uri);
    }

    //================从uri资源符中读取文件描述====================//

    /**
     * 从uri资源符中读取文件描述
     * <p>
     * 可接受的uri类型:
     * 1.content
     * 2.file
     *
     * @param uri 文本资源符
     * @return ParcelFileDescriptor
     */
    public static ParcelFileDescriptor openFileDescriptor(Uri uri) {
        return openFileDescriptor(uri, MODE_READ_ONLY);
    }

    /**
     * 从uri资源符中读取文件描述
     * <p>
     * 可接受的uri类型:
     * 1.content
     * 2.file
     *
     * @param uri 文本资源符
     * @return ParcelFileDescriptor
     */
    public static ParcelFileDescriptor openFileDescriptorWithException(Uri uri) throws FileNotFoundException {
        return openFileDescriptorWithException(uri, MODE_READ_ONLY);
    }

    /**
     * 从uri资源符中读取文件描述
     * <p>
     * 可接受的uri类型:
     * 1.content
     * 2.file
     *
     * @param uri  文本资源符
     * @param mode 文件读写模式
     * @return ParcelFileDescriptor
     */
    public static ParcelFileDescriptor openFileDescriptor(Uri uri, @FileMode String mode) {
        try {
            return openFileDescriptorWithException(uri, mode);
        } catch (FileNotFoundException e) {
            LogUtils.e(e);
        }
        return null;
    }

    /**
     * 从uri资源符中读取文件描述
     * <p>
     * 可接受的uri类型:
     * 1.content
     * 2.file
     *
     * @param uri  文本资源符
     * @param mode 文件读写模式
     * @return ParcelFileDescriptor
     */
    public static ParcelFileDescriptor openFileDescriptorWithException(Uri uri, @FileMode String mode) throws FileNotFoundException {
        return Utils.getContentResolver().openFileDescriptor(uri, mode);
    }

    /**
     * 从uri资源符中读取文件描述
     * <p>
     * 可接受的uri类型更广:
     * 1.content
     * 2.android.resource
     * 3.file
     *
     * @param uri 文本资源符
     * @return AssetFileDescriptor
     */
    public static AssetFileDescriptor openAssetFileDescriptor(Uri uri) {
        return openAssetFileDescriptor(uri, MODE_READ_ONLY);
    }

    /**
     * 从uri资源符中读取文件描述
     * <p>
     * 可接受的uri类型更广:
     * 1.content
     * 2.android.resource
     * 3.file
     *
     * @param uri 文本资源符
     * @return AssetFileDescriptor
     */
    public static AssetFileDescriptor openAssetFileDescriptorWithException(Uri uri) throws FileNotFoundException {
        return openAssetFileDescriptorWithException(uri, MODE_READ_ONLY);
    }


    /**
     * 从uri资源符中读取文件描述
     * <p>
     * 可接受的uri类型更广:
     * 1.content
     * 2.android.resource
     * 3.file
     *
     * @param uri  文本资源符
     * @param mode 文件读写模式
     * @return AssetFileDescriptor
     */
    public static AssetFileDescriptor openAssetFileDescriptor(Uri uri, @FileMode String mode) {
        try {
            return openAssetFileDescriptorWithException(uri, mode);
        } catch (FileNotFoundException e) {
            LogUtils.e(e);
        }
        return null;
    }

    /**
     * 从uri资源符中读取文件描述
     * <p>
     * 可接受的uri类型更广:
     * 1.content
     * 2.android.resource
     * 3.file
     *
     * @param uri  文本资源符
     * @param mode 文件读写模式
     * @return AssetFileDescriptor
     */
    public static AssetFileDescriptor openAssetFileDescriptorWithException(Uri uri, @FileMode String mode) throws FileNotFoundException {
        return Utils.getContentResolver().openAssetFileDescriptor(uri, mode);
    }

    //============往下载目录中写文件===============//

    /**
     * 写文件到外部公共下载目录
     *
     * @param dirPath  外部公共下载目录中的相对目录，例如传入目录是：test/，对应的写入位置就是：/storage/emulated/0/Download/test/
     * @param fileName 文件名
     * @param mimeType 文件类型
     * @return {@code true}: 写入成功<br>{@code false}: 写入失败
     */
    public static boolean writeFileToPublicDownloads(String dirPath, String fileName, String mimeType, InputStream inputStream) {
        if (isScopedStorageMode()) {
            Uri insertUri = getFileDownloadUri(dirPath, fileName, mimeType);
            if (insertUri != null) {
                return FileIOUtils.writeFileFromIS(inputStream, SAFUtils.openOutputStream(insertUri));
            }
            return false;
        } else {
            return FileIOUtils.writeFileFromIS(FileUtils.getFilePath(PathUtils.getExtDownloadsPath() + File.separator + dirPath, fileName), inputStream);
        }
    }

    /**
     * 获取文件保存到外部公共下载目录的uri
     *
     * @param dirPath  外部公共下载目录中的相对目录，例如传入目录是：test/，对应的写入位置就是：/storage/emulated/0/Download/test/
     * @param fileName 文件名
     * @return uri
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private static Uri getFileDownloadUri(String dirPath, String fileName, String mimeType) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
        values.put(MediaStore.Downloads.MIME_TYPE, mimeType);
        values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + File.separator + dirPath);
        return Utils.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
    }

}
