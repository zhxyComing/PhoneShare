package com.dixon.phoneshare.upload.videofun.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dixon.base.BaseActivity;
import com.dixon.base.api.ApiService;
import com.dixon.base.transmission.ClientNetUtil;
import com.dixon.phoneshare.R;
import com.dixon.phoneshare.bean.SelectItem;
import com.dixon.phoneshare.core.SumMonitor;
import com.dixon.phoneshare.dialog.DialogUtil;
import com.dixon.phoneshare.upload.imgfun.activity.ImageUploadActivity;
import com.dixon.phoneshare.upload.videofun.adapter.VideoListAdapter;
import com.dixon.tools.CustomDialog;
import com.dixon.tools.SizeFormat;
import com.dixon.tools.Toast;
import com.dixon.tools.file.FileUtil;
import com.dixon.tools.file.ImageItem;
import com.dixon.tools.file.VideoItem;

import java.io.File;
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

        mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                openVideo(mAdapter.getItems().get(position).getBean().getFilePath());
                return true;
            }
        });

        mUploadView.setOnClickListener(this);
    }

    private void openVideo(String filePath) {
        FileUtil.openVideo(this, filePath);
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
    @SuppressLint("DefaultLocale")
    private void uploadImageList() {
        if (mSelectItems.isEmpty()) {
            Toast.show(this, "请先勾选要上传的视频。");
            return;
        }

        CustomDialog dialog = DialogUtil.showProcessDialog(this);
        if (dialog == null) {
            return;
        }
        TextView processView = dialog.getView().findViewById(R.id.tvProcess);
        ProgressBar progressBar = dialog.getView().findViewById(R.id.tvProgress);
        progressBar.setMax(mSelectItems.size());
        SumMonitor monitor = new SumMonitor(mSelectItems.size());
        monitor.setOnOverListener(() -> {
            // 弹窗可取消
            dialog.setCanceledOnTouchOutside(true);
            // 展示上传结果
            processView.setText(String.format("上传完毕 成功：%d 失败：%d", monitor.getSuccess(), monitor.getFail()));
            // 刷新勾选状态
            mAdapter.notifyDataSetChanged();
        });
        String uploadLink = ApiService.API_HOST + ApiService.API_UPLOAD;
        try {
            for (VideoItem item : mSelectItems) {
                ClientNetUtil.upload(uploadLink, new File(item.getFilePath()), new ClientNetUtil.OnProgressChangedListener() {
                    @Override
                    public void onProgress(int progress) {

                    }

                    @Override
                    public void onProcess(String message) {

                    }

                    @Override
                    public void onFail(Exception e) {
                        monitor.addFail();
                    }

                    @Override
                    public void onSuccess() {
                        monitor.addSuccess();
                        // 上传成功 从勾选状态移除
                        mSelectItems.remove(item);
                        progressBar.setProgress(progressBar.getProgress() + 1);
                    }
                });
            }
        } catch (Exception e) {
            Toast.show(this, "错误：" + e.toString());
            dialog.dismiss();
        }
    }
}
