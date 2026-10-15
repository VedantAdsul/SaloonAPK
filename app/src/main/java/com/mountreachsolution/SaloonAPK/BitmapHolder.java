package com.mountreachsolution.SaloonAPK;

import android.graphics.Bitmap;

public class BitmapHolder {

    private static Bitmap bitmap;

    public static void setBitmap(Bitmap bmp) {
        bitmap = bmp;
    }

    public static Bitmap getBitmap() {
        Bitmap bmp = bitmap;
        bitmap = null;
        return bmp;
    }
}