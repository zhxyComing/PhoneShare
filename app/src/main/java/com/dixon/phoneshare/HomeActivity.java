package com.dixon.phoneshare;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.dixon.base.BaseActivity;
import com.dixon.phoneshare.dialog.DialogUtil;
import com.dixon.phoneshare.download.linkdown.LinkDownloadActivity;
import com.dixon.phoneshare.upload.filefun.FileUploadActivity;
import com.dixon.phoneshare.upload.imgfun.activity.ImageUploadActivity;
import com.dixon.phoneshare.upload.videofun.activity.VideoUploadActivity;
import com.dixon.tools.Toast;

public class HomeActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout mImgTabView, mVideoTabView, mFileTabView, mDownloadTabView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        runPermissionDetection();

        mImgTabView.setOnClickListener(this);
        mVideoTabView.setOnClickListener(this);
        mFileTabView.setOnClickListener(this);
        mDownloadTabView.setOnClickListener(this);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mImgTabView = findViewById(R.id.am_ll_img_tab);
        mVideoTabView = findViewById(R.id.am_ll_video_tab);
        mFileTabView = findViewById(R.id.am_ll_file_tab);
        mDownloadTabView = findViewById(R.id.am_ll_download_tab);
    }

    /**
     * 申请读写权限
     *
     * @return
     */
    private boolean runPermissionDetection() {
        if (Build.VERSION.SDK_INT >= 23) {
            final String[] permissions = {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };

            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //没权限 弹窗提示
                    DialogUtil.showTipDialog(HomeActivity.this, "App 需要读写权限以保证传输功能运行正常。", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            HomeActivity.this.requestPermissions(permissions, 0);
                        }
                    });
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.show(HomeActivity.this, "权限已获取");
        } else {
            Toast.show(HomeActivity.this, "权限获取失败");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.am_ll_img_tab:
                startActivity(new Intent(HomeActivity.this, ImageUploadActivity.class));
                break;
            case R.id.am_ll_video_tab:
                startActivity(new Intent(HomeActivity.this, VideoUploadActivity.class));
                break;
            case R.id.am_ll_file_tab:
                startActivity(new Intent(HomeActivity.this, FileUploadActivity.class));
                break;
            case R.id.am_ll_download_tab:
                startActivity(new Intent(HomeActivity.this, LinkDownloadActivity.class));
                break;
        }
    }
}
