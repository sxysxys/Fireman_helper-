package com.personal.framework.utils;//


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtil {

    private static String TAG = "ImageUtil";

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888 : Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static String saveBitmapToFile(Bitmap bitmap) {
        File file = new File(FileUtils.getSDPath(), "画视界");
        if (!file.exists()) {
            file.mkdir();
        }
        File bitmapFile = new File(file.getAbsolutePath(), System.currentTimeMillis() + ".png");
        boolean saveResult = false;
        FileOutputStream fos = null;
        String filePath = "";
        try {
            fos = new FileOutputStream(bitmapFile);
            saveResult = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            if (saveResult) {
                filePath = bitmapFile.getAbsolutePath();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                    fos = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (bitmap != null){
                bitmap.recycle();
            }
            return filePath;
        }
    }

}
