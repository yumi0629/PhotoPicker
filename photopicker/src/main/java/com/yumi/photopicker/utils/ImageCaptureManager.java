package com.yumi.photopicker.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yumi on 2016/9/14.
 */
public class ImageCaptureManager {
    private final static String CAPTURED_PHOTO_PATH_KEY = "mCurrentPhotoPath";
    public static final int REQUEST_TAKE_PHOTO = 1;

    private String mCurrentPhotoPath;
    private Context mContext;

    public ImageCaptureManager(Context mContext) {
        this.mContext = mContext;
    }

    private String createImagePath() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "PNG" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                throw new IOException();
            }
        }
        return storageDir.getAbsolutePath() + File.separator + imageFileName + ".png";
    }


    public Intent dispatchTakePictureIntent() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(mContext.getPackageManager()) != null) {
            mCurrentPhotoPath = createImagePath();
            ContentValues contentValues = new ContentValues(2);
            //如果想拍完存在系统相机的默认目录,改为
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, mCurrentPhotoPath);
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            Uri out = mContext.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, out);
        }
        return takePictureIntent;
    }


    public void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.parse(mCurrentPhotoPath);
        mediaScanIntent.setData(contentUri);
        mContext.sendBroadcast(mediaScanIntent);
    }


    public String getCurrentPhotoPath() {
        return mCurrentPhotoPath;
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null && mCurrentPhotoPath != null) {
            savedInstanceState.putString(CAPTURED_PHOTO_PATH_KEY, mCurrentPhotoPath);
        }
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(CAPTURED_PHOTO_PATH_KEY)) {
            mCurrentPhotoPath = savedInstanceState.getString(CAPTURED_PHOTO_PATH_KEY);
        }
    }
}
