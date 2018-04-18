package com.yumi.sample.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.yumi.photopicker.R;
import com.yumi.photopicker.listener.OnItemClickListener;
import com.yumi.photopicker.listener.OnItemRemoveListener;
import com.yumi.photopicker.widget.PhotoDetailPopupWin;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yumi on 2016/9/29.
 */
public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>{
    private Context context;
    private List<String> photoPaths = new ArrayList<>();
    private LayoutInflater inflater;
    private PhotoDetailPopupWin popupWindow;
    private boolean canDelete;
    private int itemHeight;
    private RequestManager glide;
    private OnItemClickListener listener = null;

    private OnItemRemoveListener removeListener = null;

    public PhotoAdapter(Context context, boolean canDelete) {
        init(context, canDelete);
    }

    public PhotoAdapter(Context context, boolean canDelete, OnItemClickListener listener) {
        init(context, canDelete);
        this.listener = listener;
    }

    private void init(Context context, boolean canDelete) {
        this.context = context;
        this.canDelete = canDelete;
        glide = Glide.with(context);
        inflater = LayoutInflater.from(context);
    }

    public void addPhotoPaths(List<String> photoPaths) {
        this.photoPaths.addAll(photoPaths);
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.photo_picker_show_item, parent, false);
        return new PhotoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PhotoViewHolder holder, final int position) {
        glide.load(photoPaths.get(position))
                .centerCrop()
                .thumbnail(0.1f)
                .error(R.mipmap.photo_picker_ic_broken_image_black_48dp)
                .into(holder.ivPhoto);
        if (canDelete) {
            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Glide.clear(holder.ivPhoto);
                    photoPaths.remove(position);
                    notifyDataSetChanged();
                    if (removeListener != null) {
                        removeListener.onItemRemoved();
                    }
                }
            });
        }
        holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(v, position);
                } else {
                    showPhotoDetail(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return photoPaths == null ? 0 : photoPaths.size();
    }

    public List<String> getPhotoPaths() {
        return photoPaths;
    }

    public int getItemHeight() {
        return itemHeight;
    }

    public RequestManager getGlide() {
        return glide;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnItemRemoveListener(OnItemRemoveListener removeListener) {
        this.removeListener = removeListener;
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

    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPhoto;
        private ImageView ivDelete;

        public PhotoViewHolder(final View itemView) {
            super(itemView);
            ivPhoto = (ImageView) itemView.findViewById(R.id.iv_photo);
            ivDelete = (ImageView) itemView.findViewById(R.id.iv_delete);
            if (!canDelete) {
                ivDelete.setVisibility(View.GONE);
            }
            if (itemHeight > 0) {
                return;
            }
            itemView.getViewTreeObserver().addOnPreDrawListener(preDrawListener);
        }

        private ViewTreeObserver.OnPreDrawListener preDrawListener = new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                itemHeight = itemView.getMeasuredHeight();
                if (itemHeight > 0) {
                    itemView.getViewTreeObserver().removeOnPreDrawListener(preDrawListener);
                }
                return true;
            }
        };
    }
}
