package com.yumi.sample;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.yumi.sample.R;
import com.yumi.sample.adapter.PhotoAdapter;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int SELECT_IMAGE_REQUEST = 101;
    private static final int UPLOAD_IMAGE_MAX_COUNT = 9;
    private static final int UPLOAD_IMAGE_COLUMN_COUNT = 3;

    private PhotoAdapter photoAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCompatButton camera = (AppCompatButton) findViewById(R.id.camera);
        camera.setOnClickListener(this);
        AppCompatButton withoutCamera = (AppCompatButton) findViewById(R.id.without_camera);
        withoutCamera.setOnClickListener(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        photoAdapter = new PhotoAdapter(this, true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(UPLOAD_IMAGE_COLUMN_COUNT, OrientationHelper.VERTICAL));
        recyclerView.setAdapter(photoAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == SELECT_IMAGE_REQUEST) {
            List<String> photos = null;
            if (data != null) {
                photos = data.getStringArrayListExtra(PickActivity.KEY_SELECTED_PHOTOS);
            }
            if (photos == null || photoAdapter == null) {
                return;
            }
            photoAdapter.addPhotoPaths(photos);
            photoAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        photoAdapter.clearAll();
        super.onDestroy();
    }

    private void selectPhotos(boolean hasCamera) {
        if (photoAdapter.getItemCount() == UPLOAD_IMAGE_MAX_COUNT) {
            Toast.makeText(this, String.format(getString(R.string.photo_picker_over_max_count_tips), UPLOAD_IMAGE_MAX_COUNT),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, PickActivity.class);
        intent.putExtra(PickActivity.COLUMN_COUNT, UPLOAD_IMAGE_COLUMN_COUNT);
        intent.putExtra(PickActivity.HAS_CAMERA, hasCamera);
        intent.putExtra(PickActivity.PIC_COUNT, UPLOAD_IMAGE_MAX_COUNT - photoAdapter.getItemCount());
        startActivityForResult(intent, SELECT_IMAGE_REQUEST);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.camera:
                selectPhotos(true);
                break;
            case R.id.without_camera:
                selectPhotos(false);
                break;
            default:
                break;
        }
    }
}
