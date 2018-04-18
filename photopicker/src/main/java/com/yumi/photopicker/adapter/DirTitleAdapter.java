package com.yumi.photopicker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.yumi.photopicker.R;
import com.yumi.photopicker.model.PhotoDir;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yumi on 2016/9/15.
 */
public class DirTitleAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private RequestManager glide;

    private List<PhotoDir> photoDirs = new ArrayList<>();

    public DirTitleAdapter(Context context, List<PhotoDir> photoDirs) {
        init(context, photoDirs);
    }

    private void init(Context context, List<PhotoDir> photoDirs) {
        this.context = context;
        this.photoDirs = photoDirs;
        glide = Glide.with(context);
        inflater = LayoutInflater.from(context);
    }

    public void setPhotoDirs(List<PhotoDir> photoDirs) {
        this.photoDirs = photoDirs;
    }

    @Override
    public int getCount() {
        return photoDirs == null ? 0 : photoDirs.size();
    }

    @Override
    public Object getItem(int position) {
        return photoDirs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.photo_pick_dir_pre_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.bindData(position);

        return convertView;
    }

    public class ViewHolder {
        public ImageView ivDirPre;
        public TextView tvDirName;
        public TextView tvDirChildNum;
        public View rootView;

        public ViewHolder(View rootView) {
            ivDirPre = (ImageView) rootView.findViewById(R.id.iv_dir_pre);
            tvDirName = (TextView) rootView.findViewById(R.id.tv_dir_name);
            tvDirChildNum = (TextView) rootView.findViewById(R.id.tv_dir_child_num);
            this.rootView = rootView;
        }

        public void bindData(final int position) {
            PhotoDir photoDir = photoDirs.get(position);
            glide.load(photoDir.photoPaths.get(0))
                    .dontAnimate()
                    .thumbnail(0.1f)
                    .into(ivDirPre);
            tvDirName.setText(photoDir.getDirName());
            tvDirChildNum.setText(String.format(context.getString(R.string.photo_picker_dir_child_num), photoDir.photoPaths.size()));
        }
    }
}
