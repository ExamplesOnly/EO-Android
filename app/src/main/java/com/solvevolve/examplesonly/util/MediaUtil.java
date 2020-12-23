package com.solvevolve.examplesonly.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
public class MediaUtil {

    public static File bitmapToFile(Bitmap bitmap, Context context) {
        File thumbFile = new File(context.getCacheDir(), String.valueOf(System.currentTimeMillis()));
        try {
            thumbFile.createNewFile();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(CompressFormat.PNG, 0, bos);
            byte[] bitmapData = bos.toByteArray();

            FileOutputStream fos = new FileOutputStream(thumbFile);
            fos.write(bitmapData);
            fos.flush();
            fos.close();

            return thumbFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isVideoLarger(float height, float width) {
        if (height / width > 1.2) {
            return true;
        }
        return false;
    }
}
