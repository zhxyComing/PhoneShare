package com.dixon.phoneshare.download.pathdown;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dixon.phoneshare.R;
import com.dixon.phoneshare.bean.PcFile;
import com.dixon.phoneshare.common.IItemData;
import com.dixon.tools.ScreenUtil;
import com.dixon.tools.SizeFormat;
import com.dixon.tools.file.FileUtil;

import java.io.File;
import java.util.List;

public class PcFileListAdapter extends BaseAdapter implements IItemData<PcFile> {

    private List<PcFile> mItems;
    private Context mContext;

    public PcFileListAdapter(Context context,
                             List<PcFile> items) {
        this.mContext = context;
        mItems = items;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_pc_file_list, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        PcFile file = mItems.get(position);
        vh.tvNameView.setText(file.getName());
        //设置大小或几个子项
        if (file.isDirectory()) {
            vh.tvSizeView.setText("");
        } else {
            vh.tvSizeView.setText(SizeFormat.format(file.getSize()));
        }
        if (file.isDirectory()) {
            vh.ivImgView.setImageResource(R.mipmap.tag_download_path_directory);
        } else {
            vh.ivImgView.setImageResource(R.mipmap.tag_download_path_file);
        }
//        loadTagImage(vh.ivImgView, file);
        return convertView;
    }

    @Override
    public List<PcFile> getItems() {
        return mItems;
    }

    private static final class ViewHolder {

        private ImageView ivImgView;
        private TextView tvNameView, tvSizeView;

        ViewHolder(View item) {
            ivImgView = item.findViewById(R.id.ipfl_iv_image);
            tvNameView = item.findViewById(R.id.ipfl_tv_name);
            tvSizeView = item.findViewById(R.id.ipfl_tv_size);
        }
    }

    /**
     * 根据不同格式设置不同标志图
     *
     * @param iv
     * @param file
     */
    private void loadTagImage(ImageView iv, final PcFile file) {
        //初始化
        int padding = (int) ScreenUtil.dpToPx(mContext, 12);
        iv.setPadding(padding, padding, padding, padding);

        //执行判断
        String fileName = file.getName();
        String suffix;
        if (file.isDirectory()) {
            iv.setImageResource(R.mipmap.tag_dir);
        } else if (!TextUtils.isEmpty(suffix = FileUtil.getSuffix(fileName))) {
            switch (suffix.toLowerCase()) {
                case "png":
                case "jpg":
                case "gif":
                case "webP":
                case "svg":
                    iv.setImageResource(R.mipmap.tag_image);
                    iv.setPadding(0, 0, 0, 0);
                    Glide.with(mContext)
                            .load(Uri.fromFile(new File(file.getPath())))
                            .into(iv);
                    break;
                case "txt":
                    iv.setImageResource(R.mipmap.tag_txt);
                    break;
                case "mp4":
                case "rm":
                case "rmvb":
                case "mkv":
                case "avi":
                    iv.setImageResource(R.mipmap.tag_video);
                    iv.setPadding(0, 0, 0, 0);
                    Glide.with(mContext)
                            .load(Uri.fromFile(new File(file.getPath())))
                            .into(iv);
                    break;
                default:
                    iv.setImageResource(R.mipmap.tag_dont_konw);
                    break;
            }
        } else {
            iv.setImageResource(R.mipmap.tag_dont_konw);
        }
    }
}
