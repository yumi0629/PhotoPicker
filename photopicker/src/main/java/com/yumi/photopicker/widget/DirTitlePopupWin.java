package com.yumi.photopicker.widget;

import android.content.Context;
import android.support.v7.widget.ListPopupWindow;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.yumi.photopicker.R;
import com.yumi.photopicker.adapter.DirTitleAdapter;
import com.yumi.photopicker.model.PhotoDir;

import java.util.List;

/**
 * 查看图像PopupWindow，里边是一个viewPager
 * Created by yumi on 2016/9/15.
 */
public class DirTitlePopupWin extends ListPopupWindow {

    private DirTitleAdapter titleAdapter;

    public DirTitlePopupWin(Context context, List<PhotoDir> photoDirs, View anchor) {
        super(context);
        init(context, photoDirs, anchor);
    }

    private void init(Context context, List<PhotoDir> photoDirs, View anchor) {
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        setHeight((int) (dm.heightPixels / 1.8f));
        setAnchorView(anchor);
        setModal(true);
        setBackgroundDrawable(context.getResources().getDrawable(R.color.white));
        setDropDownGravity(Gravity.BOTTOM);
        titleAdapter = new DirTitleAdapter(context, photoDirs);
        setAdapter(titleAdapter);
    }

    public void setPhotoDirs(List<PhotoDir> photoDirs) {
        titleAdapter.setPhotoDirs(photoDirs);
        titleAdapter.notifyDataSetChanged();
    }
}