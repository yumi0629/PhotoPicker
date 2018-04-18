package com.yumi.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yumi.photopicker.adapter.PhotoSelectAdapter;
import com.yumi.photopicker.listener.OnItemSelectedListener;
import com.yumi.photopicker.model.PhotoDir;
import com.yumi.photopicker.utils.ImageCaptureManager;
import com.yumi.photopicker.utils.MediaStoreHelper;
import com.yumi.photopicker.widget.DirTitlePopupWin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Select photos activity.
 * Created by yumi on 2016/9/29.
 */
public class PickActivity extends AppCompatActivity implements View.OnClickListener {
    public final static String COLUMN_COUNT = "column_count";
    public final static String PIC_COUNT = "pic_count";
    public final static String HAS_CAMERA = "has_camera";
    public final static String KEY_SELECTED_PHOTOS = "key_selected_photos";
    private final static int DEFAULT_COLUMN_COUNT = 3;
    private final static int DEFAULT_MAX_PIC_COUNT = 9;

    private TextView tvDone;
    private TextView tvDirTitle;
    private RelativeLayout rlBottom;
    private RecyclerView rvPhoto;

    private DirTitlePopupWin popupWin;

    private ImageCaptureManager captureManager;
    private PhotoSelectAdapter selectAdapter;
    private List<String> paths = new ArrayList<>();
    private List<PhotoDir> photoDirs;

    private Bundle mediaStoreArgs;

    private int columnCount = DEFAULT_COLUMN_COUNT;
    private int picCount = DEFAULT_MAX_PIC_COUNT;

    private boolean hasCamera = true;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_pick_view);

        tvDone = (TextView) findViewById(R.id.btn_actionbar_save);
        rlBottom = (RelativeLayout) findViewById(R.id.rl_photo_pick_bottom);
        rvPhoto = (RecyclerView) findViewById(R.id.rv_photo_pick);
        tvDirTitle = (TextView) findViewById(R.id.tv_photo_pick_dir_title);
        tvDirTitle.setOnClickListener(this);
        tvDone.setOnClickListener(this);

        Intent intent = getIntent();
        if (intent != null) {
            columnCount = intent.getIntExtra(COLUMN_COUNT, DEFAULT_COLUMN_COUNT);
            picCount = intent.getIntExtra(PIC_COUNT, DEFAULT_MAX_PIC_COUNT);
            hasCamera = intent.getBooleanExtra(HAS_CAMERA, true);
        }
        tvDone.setText(String.format(getString(R.string.photo_picker_done), 0, picCount));
        initRvPhoto();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImageCaptureManager.REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            tvDirTitle.setText(getString(R.string.photo_picker_dir_all));
            MediaStoreHelper.restartLoader(this, mediaStoreArgs, new MediaStoreHelper.PhotosResultCallback() {
                @Override
                public void onResultCallback(List<PhotoDir> directories) {
                    if (tvDirTitle.getText().equals(getString(R.string.photo_picker_dir_all))) {
                        selectAdapter.setPhotoPaths(directories.get(0).photoPaths);
                        selectAdapter.notifyDataSetChanged();
                        photoDirs = directories;
                        if (popupWin != null) {
                            popupWin.setPhotoDirs(photoDirs);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        selectAdapter.clearAll();
        super.onDestroy();
    }

    private void initRvPhoto() {
        captureManager = new ImageCaptureManager(this);
        selectAdapter = new PhotoSelectAdapter(this, hasCamera, picCount);
        rvPhoto.setLayoutManager(new StaggeredGridLayoutManager(columnCount, OrientationHelper.VERTICAL));
        rvPhoto.setAdapter(selectAdapter);
        // Read image resources from system photo gallery;
        mediaStoreArgs = new Bundle();
        // If you want to show gif photos, you can set the falg true.
        mediaStoreArgs.putBoolean(MediaStoreHelper.EXTRA_SHOW_GIF, false);
        MediaStoreHelper.getPhotoDirs(this, mediaStoreArgs, new MediaStoreHelper.PhotosResultCallback() {
            @Override
            public void onResultCallback(List<PhotoDir> directories) {
                if (tvDirTitle.getText().equals(PickActivity.this.getString(R.string.photo_picker_dir_all))) {
                    selectAdapter.setPhotoPaths(directories.get(0).photoPaths);
                    selectAdapter.notifyDataSetChanged();
                    photoDirs = directories;
                }
            }
        });

        selectAdapter.setOnCameraClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = captureManager.dispatchTakePictureIntent();
                    PickActivity.this.startActivityForResult(intent, ImageCaptureManager.REQUEST_TAKE_PHOTO);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        selectAdapter.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int seleted, int total) {
                tvDone.setText(String.format(PickActivity.this.getString(R.string.photo_picker_done), seleted, total));
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_actionbar_save:
                Intent intent = new Intent();
                intent.putStringArrayListExtra(KEY_SELECTED_PHOTOS, selectAdapter.getSelectedPhotos());
                setResult(RESULT_OK, intent);
                onBackPressed();
                break;
            case R.id.tv_photo_pick_dir_title:
                if (popupWin == null) {
                    popupWin = new DirTitlePopupWin(this, photoDirs, rlBottom);
                    popupWin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            tvDirTitle.setText(photoDirs.get(position).getDirName());
                            paths = photoDirs.get(position).photoPaths;
                            selectAdapter.setPhotoPaths(paths);
                            selectAdapter.notifyDataSetChanged();
                            popupWin.dismiss();
                        }
                    });
                }
                if (popupWin.isShowing()) {
                    popupWin.dismiss();
                } else {
                    popupWin.show();
                }
                break;
            default:
                break;
        }
    }
}
