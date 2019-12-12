package com.dixon.phoneshare.filefun;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.dixon.base.BaseActivity;
import com.dixon.phoneshare.R;
import com.dixon.phoneshare.imgfun.activity.ImageViewerActivity;
import com.dixon.tools.Toast;
import com.dixon.tools.file.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUploadActivity extends BaseActivity {

    private ListView mFileListView;
    private TextView mUpload, mTip;

    private FileListAdapter mAdapter;
    private List<String> mHistoryPath = new ArrayList<>();
    private String mCurrentPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_upload);

        mCurrentPath = FileUtil.getSDPath();
        ///storage/emulated/0
        mAdapter = new FileListAdapter(this, FileUtil.getFileList(mCurrentPath));
        mFileListView.setAdapter(mAdapter);

        mFileListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                File selectFile = mAdapter.getItems().get(position);
                // 刷新选中状态
                if (mAdapter.getSelectFile() != null && mAdapter.getSelectFile().equals(selectFile)) {
                    mAdapter.notifySelectFile(null);
                } else {
                    mAdapter.notifySelectFile(selectFile);
                }
                notifyTip();
                return true;
            }
        });

        mFileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File file = mAdapter.getItems().get(position);
                //将当钱路径记入历史方便回退
                recordHistoryPath(file);
                //跳页
                jumpPage(file);
                //根结点 进入点击跳转选项
                jumpIntent(file);
            }
        });

        mUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });
    }

    private void jumpIntent(File file) {
        if (!file.isDirectory()) {
            String name = file.getName();
            String suffix = FileUtil.getSuffix(name);
            if (!TextUtils.isEmpty(suffix)) {
                switch (suffix) {
                    case "png":
                    case "jpg":
                    case "gif":
                    case "webP":
                    case "svg":
                        openImage(file.getPath());
                        break;
                    case "mp4":
                    case "rm":
                    case "rmvb":
                    case "mkv":
                    case "avi":
                        openVideo(file.getPath());
                        break;
                    default:
                        Toast.show(FileUploadActivity.this, "暂不支持该格式文件的跳转");
                        break;
                }
            } else {
                Toast.show(FileUploadActivity.this, "暂不支持该格式文件的跳转");
            }
        }
    }

    private void openVideo(String filePath) {
        FileUtil.openVideo(this, filePath);
    }

    private void openImage(String path) {
        Intent intent = new Intent(FileUploadActivity.this, ImageViewerActivity.class);
        intent.putExtra("path", path);
        startActivity(intent);
    }

    private void uploadFile() {

    }

    private void recordHistoryPath(File file) {
        if (file.isDirectory() && file.listFiles() != null) {
            mHistoryPath.add(mCurrentPath);
        }
    }

    private void notifyTip() {
        File file = mAdapter.getSelectFile();
        if (file == null) {
            mTip.setText("未选文件");
        } else {
            mTip.setText(String.format("已选文件：%s", file.getName()));
        }
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();

        mFileListView = findViewById(R.id.afu_lv_file_list);
        mUpload = findViewById(R.id.afu_tv_file_upload);
        mTip = findViewById(R.id.afu_tv_select_tip);
    }

    @Override
    public void onBackPressed() {
        if (mHistoryPath.isEmpty()) {
            super.onBackPressed();
        } else {
            jumpPage(new File(mHistoryPath.remove(mHistoryPath.size() - 1)));
        }
    }

    private void jumpPage(File file) {
        if (file.isDirectory() && file.listFiles() != null) {
            //更新最新当前路径
            mCurrentPath = file.getPath();
            //进入下一级
            mAdapter.getItems().clear();
            mAdapter.getItems().addAll(FileUtil.getFileList(file.getPath()));
            mAdapter.notifyDataSetChanged();
        }
    }
}
