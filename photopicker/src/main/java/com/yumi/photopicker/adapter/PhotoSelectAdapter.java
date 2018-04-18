package com.yumi.photopicker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.yumi.photopicker.R;
import com.yumi.photopicker.listener.OnItemClickListener;
import com.yumi.photopicker.listener.OnItemSelectedListener;
import com.yumi.photopicker.widget.PhotoDetailPopupWin;

import java.util.ArrayList;
import java.util.List;

/**
 * 图片RecyclerView的Adapter
 * Created by yumi on 2016/9/12.
 */
public class PhotoSelectAdapter extends RecyclerView.Adapter<PhotoSelectAdapter.PhotoViewHolder> {
    public final static int ITEM_TYPE_CAMERA = 0;
    public final static int ITEM_TYPE_PHOTO = 1;
    public final static int DEFAULT_MAX_SELECTED_COUNT = 9;

    private Context context;
    private LayoutInflater inflater;

    private List<String> photoPaths = new ArrayList<>();
    private ArrayList<String> selectedPhotos = new ArrayList<>();
    private int maxSelectableCount = DEFAULT_MAX_SELECTED_COUNT;
    private boolean showCamera;

    private PhotoDetailPopupWin popupWindow;

    private RequestManager glide;
    private OnItemClickListener listener = null;
    private View.OnClickListener onCameraClickListener = null;
    private OnItemSelectedListener selectedListener = null;

    public PhotoSelectAdapter(Context context, boolean showCamera) {
        init(context, showCamera);
    }

    public PhotoSelectAdapter(Context context, boolean showCamera, int maxSelectableCount) {
        init(context, showCamera);
        this.maxSelectableCount = maxSelectableCount;
    }

    public PhotoSelectAdapter(Context context, boolean showCamera, int maxSelectableCount, OnItemClickListener listener) {
        init(context, showCamera);
        this.maxSelectableCount = maxSelectableCount;
        this.listener = listener;
    }

    private void init(Context context, boolean showCamera) {
        this.context = context;
        glide = Glide.with(context);
        this.showCamera = showCamera;
        inflater = LayoutInflater.from(context);
    }

    public void addPhotoPaths(List<String> photoPaths) {
        this.photoPaths.addAll(photoPaths);
    }

    public void setPhotoPaths(List<String> photoPaths) {
        this.photoPaths = photoPaths;
        if (popupWindow != null) {
            popupWindow.notifyData(photoPaths);
        }
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.photo_pick_select_item, parent, false);
        PhotoViewHolder holder = new PhotoViewHolder(itemView);
        if (viewType == ITEM_TYPE_CAMERA) {
            holder.ivSelected.setVisibility(View.GONE);
            holder.ivPhoto.setScaleType(ImageView.ScaleType.CENTER);
            holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onCameraClickListener != null) {
                        onCameraClickListener.onClick(view);
                    }
                }
            });
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(final PhotoViewHolder holder, final int position) {
        if (getItemViewType(position) == ITEM_TYPE_PHOTO) {
            int p = position;
            if (showCamera) {
                p = position - 1;
            }
            final int truePosition = p;
            final String photo = photoPaths.get(truePosition);
            glide.load(photo)
                    .centerCrop()
                    .thumbnail(0.1f)
                    .error(R.mipmap.photo_picker_ic_broken_image_black_48dp)
                    .into(holder.ivPhoto);
            if (selectedPhotos.contains(photo)) {
                holder.ivSelected.setSelected(true);
            } else {
                holder.ivSelected.setSelected(false);
            }
            holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener == null) {
                        showPhotoDetail(truePosition);
                    } else {
                        listener.onItemClick(v, truePosition);
                    }
                }
            });
            holder.ivSelected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.isSelected()) {  // 已选中，点击改为未选中
                        v.setSelected(false);
                        if (selectedPhotos.contains(photo)) {
                            selectedPhotos.remove(photo);
                        }
                    } else if (!v.isSelected()) {  // 未选中，点击改为已选中
                        if (selectedPhotos.size() > maxSelectableCount - 1) {  // 超过可选上限
                            Toast.makeText(context,
                                    String.format(context.getString(R.string.photo_picker_over_max_count_tips), maxSelectableCount),
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        v.setSelected(true);
                        if (!selectedPhotos.contains(photo)) {
                            selectedPhotos.add(photo);
                        }
                    }
                    if (selectedListener != null) {
                        selectedListener.onItemSelected(selectedPhotos.size(), maxSelectableCount);
                    }
                }
            });
        } else {
            holder.ivPhoto.setImageResource(R.drawable.photo_picker_camera);
        }
    }

    @Override
    public int getItemCount() {
        if (showCamera) {
            return photoPaths == null ? 1 : photoPaths.size() + 1;
        }
        return photoPaths == null ? 0 : photoPaths.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (showCamera && position == 0) ? ITEM_TYPE_CAMERA : ITEM_TYPE_PHOTO;
    }

    public void setMaxSelectableCount(int maxSelectableCount) {
        this.maxSelectableCount = maxSelectableCount;
    }

    public List<String> getPhotoPaths() {
        return photoPaths;
    }

    public ArrayList<String> getSelectedPhotos() {
        return selectedPhotos;
    }

    public RequestManager getGlide() {
        return glide;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnCameraClickListener(View.OnClickListener onCameraClickListener) {
        this.onCameraClickListener = onCameraClickListener;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener selectedListener) {
        this.selectedListener = selectedListener;
    }

    public void clearAll() {
        glide.onDestroy();
        if (popupWindow != null) {
            if (popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
            popupWindow.clearAll();
            popupWindow = null;
        }
    }

    private void showPhotoDetail(int position) {
        if (popupWindow == null) {
            popupWindow = new PhotoDetailPopupWin(context, photoPaths);
        }
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
        popupWindow.setCurPosition(position);
        popupWindow.showPopupWin();
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPhoto;
        private ImageView ivSelected;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            ivPhoto = (ImageView) itemView.findViewById(R.id.iv_photo);
            ivSelected = (ImageView) itemView.findViewById(R.id.iv_delete);
        }
    }
}
