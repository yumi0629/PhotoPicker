package com.yumi.photopicker.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.yumi.photopicker.R;
import com.yumi.photopicker.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yumi on 2016/9/14.
 */
public class PhotoPagerAdapter extends PagerAdapter {
    private List<String> paths = new ArrayList<>();
    private RequestManager mGlide;
    private OnItemClickListener listener;

    public PhotoPagerAdapter(RequestManager glide, List<String> paths) {
        this.paths = paths;
        this.mGlide = glide;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final Context context = container.getContext();
        final ImageView imageView = (ImageView) LayoutInflater.from(context).inflate(R.layout.popup_window_pic_detail, container, false);
        mGlide.load(paths.get(position))
                .dontAnimate()
                .dontTransform()
                .placeholder(R.mipmap.photo_picker_ic_photo_black_48dp)
                .error(R.mipmap.photo_picker_ic_broken_image_black_48dp)
                .into(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onItemClick(view, position);
                }
            }
        });
        container.addView(imageView);
        return imageView;
    }


    @Override
    public int getCount() {
        return paths.size();
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        Glide.clear((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setPaths(List<String> paths) {
        this.paths = paths;
    }
}
