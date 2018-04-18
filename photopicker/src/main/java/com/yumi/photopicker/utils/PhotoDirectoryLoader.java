package com.yumi.photopicker.utils;

import android.content.Context;
import android.content.CursorLoader;
import android.net.Uri;
import android.provider.MediaStore;

import static android.provider.MediaStore.MediaColumns.MIME_TYPE;

/**
 * Created by yumi on 2016/9/14.
 */
public class PhotoDirectoryLoader extends CursorLoader {
    final String[] IMAGE_PROJECTION = {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED
    };

    public PhotoDirectoryLoader(Context context, boolean showGif) {
        super(context);

        setProjection(IMAGE_PROJECTION);
        setUri(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        setSortOrder(MediaStore.Images.Media.DATE_ADDED + " DESC");

        setSelection(
                MIME_TYPE + "=? or " + MIME_TYPE + "=? " + (showGif ? ("or " + MIME_TYPE + "=?") : ""));
        String[] selectionArgs;
        if (showGif) {
            selectionArgs = new String[] { "image/jpeg", "image/png", "image/gif" };
        } else {
            selectionArgs = new String[] { "image/jpeg", "image/png" };
        }
        setSelectionArgs(selectionArgs);
    }

    private PhotoDirectoryLoader(Context context, Uri uri, String[] projection, String selection,
                                 String[] selectionArgs, String sortOrder) {
        super(context, uri, projection, selection, selectionArgs, sortOrder);
    }
}
