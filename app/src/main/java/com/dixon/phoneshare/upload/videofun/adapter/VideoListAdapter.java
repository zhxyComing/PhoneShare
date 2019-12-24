package com.dixon.phoneshare.upload.videofun.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dixon.phoneshare.R;
import com.dixon.phoneshare.bean.SelectItem;
import com.dixon.phoneshare.common.IItemData;
import com.dixon.tools.TimeFormat;
import com.dixon.tools.file.VideoItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VideoListAdapter extends BaseAdapter implements IItemData<SelectItem<VideoItem>> {

    private List<SelectItem<VideoItem>> mItems;
    private Context mContext;

    public VideoListAdapter(Context context,
                            List<VideoItem> items) {
        this.mContext = context;
        mItems = new ArrayList<>();
        for (VideoItem item : items) {
            mItems.add(new SelectItem<>(item));
        }
    }

    @Override
    public int getCount() {
        return mItems == null ? 0 : mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return this.mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            // 暂时用image的Layout
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_image_list, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        VideoItem fileItem = mItems.get(position).getBean();
        vh.tvTimeView.setText(TimeFormat.longToString(Long.valueOf(fileItem.getDate())));
        vh.tvSizeView.setText(fileItem.getFileSize());
        Glide.with(mContext)
                .load(Uri.fromFile(new File(fileItem.getFilePath())))
                .into(vh.ivImgView);
        if (mItems.get(position).isHasSelect()) {
            vh.ivSelectTag.setVisibility(View.VISIBLE);
        } else {
            vh.ivSelectTag.setVisibility(View.GONE);
        }
        return convertView;
    }

    @Override
    public List<SelectItem<VideoItem>> getItems() {
        return mItems;
    }

    private static final class ViewHolder {

        private ImageView ivImgView, ivSelectTag;
        private TextView tvTimeView, tvSizeView;

        ViewHolder(View item) {
            ivImgView = item.findViewById(R.id.iil_iv_image);
            tvTimeView = item.findViewById(R.id.iil_tv_time);
            tvSizeView = item.findViewById(R.id.iil_tv_size);
            ivSelectTag = item.findViewById(R.id.iil_iv_select);

        }
    }
}
