package com.dixon.phoneshare.upload.filefun;

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
import com.dixon.phoneshare.common.IItemData;
import com.dixon.tools.ScreenUtil;
import com.dixon.tools.SizeFormat;
import com.dixon.tools.file.FileUtil;

import java.io.File;
import java.util.List;
import java.util.Locale;

public class FileListAdapter extends BaseAdapter implements IItemData<File> {

    private List<File> mItems;
    private Context mContext;
    // 仅支持一个文件or文件夹的发送
    private File mSelectFile;

    public FileListAdapter(Context context,
                           List<File> items) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_file_list, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        File file = mItems.get(position);
        vh.tvNameView.setText(file.getName());
        //设置大小或几个子项
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                vh.tvSizeOrCountView.setText(String.format(Locale.CHINA, "%d 项", files.length));
            } else {
                vh.tvSizeOrCountView.setText("0 项");
            }
        } else {
            vh.tvSizeOrCountView.setText(SizeFormat.format(file.length()));
        }
        if (mItems.get(position).equals(mSelectFile)) {
            vh.ivSelectTag.setVisibility(View.VISIBLE);
        } else {
            vh.ivSelectTag.setVisibility(View.GONE);
        }
        loadTagImage(vh.ivImgView, file);
        return convertView;
    }

    @Override
    public List<File> getItems() {
        return mItems;
    }

    private static final class ViewHolder {

        private ImageView ivImgView, ivSelectTag;
        private TextView tvNameView, tvSizeOrCountView;

        ViewHolder(View item) {
            ivImgView = item.findViewById(R.id.ifl_iv_image);
            tvNameView = item.findViewById(R.id.ifl_tv_name);
            tvSizeOrCountView = item.findViewById(R.id.ifl_tv_size_or_count);
            ivSelectTag = item.findViewById(R.id.ifl_tv_select);
        }
    }

    public File getSelectFile() {
        return mSelectFile;
    }

    public void notifySelectFile(File selectFile) {
        this.mSelectFile = selectFile;
        notifyDataSetChanged();
    }

    /**
     * 根据不同格式设置不同标志图
     *
     * @param iv
     * @param file
     */
    private void loadTagImage(ImageView iv, final File file) {
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
