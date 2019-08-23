package com.bikshanov.fingerpaint;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Environment;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static Point getSize(Context context) {
        Point size = new Point();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        display.getSize(size);

        return size;
    }

    public static boolean savePicture(View view, Context context) {

        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/FingerPaint";
        File dir = new File(filePath);


        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()).concat(".jpg");
        File file = new File(dir, fileName);

        FileOutputStream fout;

        view.setDrawingCacheEnabled(true);

        try {
            fout = new FileOutputStream(file);
            view.getDrawingCache().compress(Bitmap.CompressFormat.JPEG, 85, fout);
            fout.flush();
            fout.close();
            view.setDrawingCacheEnabled(false);

            Uri contentUri = Uri.fromFile(file);

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, contentUri);

            context.sendBroadcast(mediaScanIntent);

            return true;
        }

        catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }
}