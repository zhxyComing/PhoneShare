package com.dixon.phoneshare.videofun.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.dixon.base.BaseActivity;
import com.dixon.phoneshare.R;
import com.dixon.phoneshare.bean.SelectItem;
import com.dixon.phoneshare.videofun.adapter.VideoListAdapter;
import com.dixon.tools.SizeFormat;
import com.dixon.tools.file.FileUtil;
import com.dixon.tools.file.VideoItem;

import java.util.ArrayList;
import java.util.List;

public class VideoUploadActivity extends BaseActivity implements View.OnClickListener {

    private List<VideoItem> mSelectItems = new ArrayList<>();

    private VideoListAdapter mAdapter;

    private GridView mGridView;
    private TextView mUploadDataParamsView, mUploadView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_upload);

        List<VideoItem> list = FileUtil.getVideos(this);
        mAdapter = new VideoListAdapter(this, list);
        mGridView.setAdapter(mAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // UI刷新
                SelectItem<VideoItem> fileSelectItem = mAdapter.getItems().get(position);
                fileSelectItem.setHasSelect(!fileSelectItem.isHasSelect());
                mAdapter.notifyDataSetChanged();

                // 数据刷新
                if (fileSelectItem.isHasSelect()) {
                    mSelectItems.add(fileSelectItem.getBean());
                } else {
                    mSelectItems.remove(fileSelectItem.getBean());
                }
                notifyUploadData();
            }
        });

        mUploadView.setOnClickListener(this);
    }

    private void notifyUploadData() {
        mUploadDataParamsView.setText(getUploadDataParams());
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mGridView = findViewById(R.id.avu_gl_img_list);
        mUploadDataParamsView = findViewById(R.id.avu_tv_file_params);
        mUploadView = findViewById(R.id.avu_tv_file_upload);
    }

    private String getUploadDataParams() {
        long sizeNum = 0;
        for (VideoItem item : mSelectItems) {
            sizeNum += Long.valueOf(item.getOriginSize());
        }
        String res = "选取文件：" + mSelectItems.size() + "\n" +
                "文件大小：" + SizeFormat.format(sizeNum);
        return res;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.aiu_tv_file_upload:
                uploadImageList();
                break;
        }
    }

    // 上传到服务器
    private void uploadImageList() {

    }
}
