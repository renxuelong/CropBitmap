package com.renxl.cropimgdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.FileDescriptor;

/**
 * Created by renxl
 * On 2017/7/10 17:53.
 * <p>
 * 压缩图片
 * 可压缩 FileDescriptor、Path 指定的 Bitmap
 * InputStream 需要先转换成 FileDescriptor 之后再进行压缩
 */

public class ImageResizer {

    public static Bitmap resizerFileDescriptor(FileDescriptor fileDescriptor, int width, int height) {

        Log.i("renxl", "ImageResizer.resizerFileDescriptor");
        if (fileDescriptor == null || width <= 0 || height <= 0) return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = calculateInSampleSize(options, width, height);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
    }

    public static Bitmap resizerPath(String path, int width, int height) {
        if (path == null || width <= 0 || height <= 0) return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = calculateInSampleSize(options, width, height);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int width, int height) {
        int inSampleSize = 1;

        int outWidth = options.outWidth;
        int outHeight = options.outHeight;

        if (outWidth > width && outHeight > height) {
            outWidth /= 2;
            outHeight /= 2;

            while (outWidth / inSampleSize > width && outHeight / inSampleSize > height) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

}
