package com.yumi.photopicker.widget;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.yumi.photopicker.R;
import com.yumi.photopicker.adapter.PhotoPagerAdapter;
import com.yumi.photopicker.listener.OnItemClickListener;

import java.util.List;

/**
 * 查看图像PopupWindow，里边是一个viewPager
 * Created by yumi on 2016/9/15.
 */
public class PhotoDetailPopupWin extends PopupWindow {
    private Context context;
    private ViewPager contentView;
    private PhotoPagerAdapter pagerAdapter;
    private List<String> photoPaths;

    public PhotoDetailPopupWin(Context context, List<String> photoPaths) {
        super(context);
        this.context = context;
        this.photoPaths = photoPaths;
        init(context, Glide.with(context));
    }

    private void init(Context context, final RequestManager glide) {
        contentView = new ViewPager(context);
        contentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        pagerAdapter = new PhotoPagerAdapter(glide, photoPaths);
        contentView.setAdapter(pagerAdapter);
        setContentView(contentView);
        setFocusable(true);
        setBackgroundDrawable(context.getResources().getDrawable(R.color.black));
        setAnimationStyle(R.style.popwindow_image_anim_style);
        pagerAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if (isShowing()) {
                    dismiss();
                }
            }
        });
    }

    public void showPopupWin() {
        if (context instanceof Activity && ((Activity) context).getWindow().getDecorView().getWindowToken() != null) {
            showAtLocation(((Activity) context).getWindow().getDecorView(), Gravity.CENTER, 0, 0);
        }
    }

    public void notifyData(List<String> photoPaths) {
        pagerAdapter.setPaths(photoPaths);
        pagerAdapter.notifyDataSetChanged();
    }

    public void setCurPosition(int curPosition) {
        contentView.setCurrentItem(curPosition);
        pagerAdapter.notifyDataSetChanged();
    }

    public void clearAll() {
        if (contentView != null) {
            contentView.destroyDrawingCache();
            contentView = null;
        }
    }
}
