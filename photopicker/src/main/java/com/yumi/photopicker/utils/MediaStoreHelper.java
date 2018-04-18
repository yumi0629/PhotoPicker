package com.yumi.photopicker.utils;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;

import com.yumi.photopicker.model.PhotoDir;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static android.provider.MediaStore.MediaColumns.DATA;

/**
 * Created by yumi on 2016/9/14.
 */
public class MediaStoreHelper {
    public static String EXTRA_SHOW_GIF = "extra_show_gif";
    public static final String DIR_ALL = "all";
    public static PhotoDirLoaderCallbacks callbacks;
    private static final int LOADER_ID = 101;

    public static void getPhotoDirs(Activity activity, Bundle args, PhotosResultCallback resultCallback) {
        if (callbacks == null) {
            callbacks = new PhotoDirLoaderCallbacks(activity, resultCallback);
        }
        if (activity.getLoaderManager().getLoader(LOADER_ID) == null) {
            activity.getLoaderManager().initLoader(LOADER_ID, args, new PhotoDirLoaderCallbacks(activity, resultCallback));
        } else {
            activity.getLoaderManager().restartLoader(LOADER_ID, args, new PhotoDirLoaderCallbacks(activity, resultCallback));
        }
    }

    public static void restartLoader(Activity activity, Bundle args, PhotosResultCallback resultCallback) {
        if (callbacks != null) {
            activity.getLoaderManager().restartLoader(LOADER_ID, args, new PhotoDirLoaderCallbacks(activity, resultCallback));
        }
    }

    static class PhotoDirLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {

        private WeakReference<Context> context;
        private PhotosResultCallback resultCallback;

        public PhotoDirLoaderCallbacks(Context context, PhotosResultCallback resultCallback) {
            this.context = new WeakReference<>(context);
            this.resultCallback = resultCallback;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new PhotoDirectoryLoader(context.get(), args.getBoolean(EXTRA_SHOW_GIF, false));
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data == null || data.getCount() == 0) {
                return;
            }
            List<String> dirs = new ArrayList<>();
            dirs.add(DIR_ALL);
            List<PhotoDir> photoDirs = new ArrayList<>();
            photoDirs.add(new PhotoDir(DIR_ALL, new ArrayList<String>()));
            while (data.moveToNext()) {
                String path = data.getString(data.getColumnIndexOrThrow(DATA));
                File file = new File(path);
                if (!file.exists() || file.length() == 0) {
                    continue;
                }
                photoDirs.get(0).photoPaths.add(path);
                String dir = path.substring(0, path.lastIndexOf(File.separator)); // 不包括"/"
                if (dirs.contains(dir)) {  // 已经有了
                    photoDirs.get(dirs.indexOf(dir)).photoPaths.add(path);
                } else {  // 还没有
                    dirs.add(dir);
                    List<String> tempPaths = new ArrayList<>();
                    tempPaths.add(path);
                    photoDirs.add(new PhotoDir(dir, tempPaths));
                }
            }
            if (resultCallback != null) {
                resultCallback.onResultCallback(photoDirs);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }

    public interface PhotosResultCallback {
        void onResultCallback(List<PhotoDir> directories);
    }
}
