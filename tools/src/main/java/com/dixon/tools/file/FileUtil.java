package com.dixon.tools.file;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FileUtil {

    /**
     * 获取本机所有图片
     * <p>
     * 存在耗时 一加7T手机图片百张耗时40ms左右 暂不子线程处理 反正自用
     *
     * @param context
     * @return
     */
    public static List<ImageItem> getPhotos(Context context) {
        List<ImageItem> resultList = new ArrayList<>();
        Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = context.getContentResolver();
        String[] projection = new String[]{
                MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DISPLAY_NAME,
                MediaStore.Images.ImageColumns.SIZE, MediaStore.Images.ImageColumns.DATE_ADDED
        };
        Cursor cursor = contentResolver.query(imageUri, projection, null, null,
                MediaStore.Images.Media.DATE_ADDED + " desc");

        if (cursor == null) {
            return resultList;
        }
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
                String fileName =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                String size = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
                String date = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));

                ImageItem item = new ImageItem(path, fileName, size, date);
                resultList.add(item);
            }
        }
        cursor.close();
        return resultList;
    }

    public static List<VideoItem> getVideos(Context context) {
        List<VideoItem> resultList = new ArrayList<>();
        Uri videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        ContentResolver cr = context.getContentResolver();
        String[] projection = new String[]{
                MediaStore.Video.VideoColumns.DATA, MediaStore.Video.VideoColumns.DURATION,
                MediaStore.Video.VideoColumns.DISPLAY_NAME, MediaStore.Video.VideoColumns.DATE_ADDED
        };

        Cursor cursor = cr.query(videoUri, projection, null, null, null);
        if (cursor == null) {
            return resultList;
        }
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                String name = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                String date = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED));
                long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
//                String size = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));

                VideoItem item = new VideoItem(path, name, String.valueOf(new File(path).length()), date, duration);
                resultList.add(item);
            }
        }
        cursor.close();
        return resultList;
    }

    public static void openImage(Context context, String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        File file = new File(path);     // path 是外部存储中的一个图片的路径
        Uri uri;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            uri = Uri.fromFile(file);
        } else {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            uri = FileProvider.getUriForFile(context, context.getPackageName() + ".file_provider", file);
        }
        intent.setDataAndType(uri, "image/*");
        context.startActivity(Intent.createChooser(intent, null));
    }

    public static void openVideo(Context context, String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        File file = new File(path);     // path 是外部存储中的一个图片的路径
        Uri uri;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            uri = Uri.fromFile(file);
        } else {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            uri = FileProvider.getUriForFile(context, context.getPackageName() + ".file_provider", file);
        }
        intent.setDataAndType(uri, "video/*");
        context.startActivity(Intent.createChooser(intent, null));
    }

    public static List<File> getFileList(String path) {
        ArrayList<File> arrayList = new ArrayList<>();
        File file = new File(path);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                Collections.addAll(arrayList, files);
            }
        }
        Collections.sort(arrayList, new FileNameComparator());
        return arrayList;
    }

    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        if (sdDir != null) {
            return sdDir.toString();
        }
        return "";
    }

    public static String getSuffix(String fileName) {
        if (fileName.contains(".")) {
            String[] split = fileName.split("\\.");
            String suffix = split[split.length - 1];
            return suffix;
        }
        return "";
    }

    private static final class FileNameComparator implements Comparator<File> {

        @Override
        public int compare(File o1, File o2) {
            return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
        }
    }
}
