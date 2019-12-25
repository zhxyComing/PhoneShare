package com.dixon.phoneshare.download.linkdown;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dixon.base.BaseActivity;
import com.dixon.base.api.ApiService;
import com.dixon.base.transmission.ClientNetUtil;
import com.dixon.phoneshare.R;
import com.dixon.tools.Toast;

public class LinkDownloadActivity extends BaseActivity {

    private EditText etDownloadUrl, etSaveUrl;
    private TextView tvDownloadBtn, tvSpeed, tvProcess;
    private ProgressBar pbProgress;
    private boolean isDownloadRun = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_link);

        initView();
        tvDownloadBtn.setOnClickListener(downloadClick);
    }

    private void initView() {
        etDownloadUrl.setText(String.format("%s%s?path=D:/", ApiService.API_HOST, ApiService.API_DOWNLOAD));
        pbProgress.setProgress(0);
        tvProcess.setText("0%");
    }

    private View.OnClickListener downloadClick = v -> {
        if (isDownloadRun) {
            Toast.show(this, "当前已有任务在运行，请稍后...");
            return;
        }
        String downloadUrl = etDownloadUrl.getText().toString();
        String saveUrl = etSaveUrl.getText().toString();
        if (!TextUtils.isEmpty(downloadUrl)) {
            onDownloadStart();
            try {
                ClientNetUtil.download(ClientNetUtil.getNameFromUrl(downloadUrl), downloadUrl, saveUrl, new ClientNetUtil.OnProgressChangedListener() {
                    @Override
                    public void onProgress(int progress) {
                        pbProgress.setProgress(progress);
                        tvProcess.setText(progress + "%");
                    }

                    @Override
                    public void onProcess(final String message) {
                        tvProcess.setText(message);
                    }

                    @Override
                    public void onFail(final Exception e) {
                        tvProcess.setText(String.format("下载失败 %s", e.toString()));
                        onDownloadFinish();
                    }

                    @Override
                    public void onSuccess() {
                        tvProcess.setText("下载完成");
                        pbProgress.setProgress(100);
                        onDownloadFinish();
                    }
                }, new ClientNetUtil.SpeedMonitor() {

                    // 2s计算一次下载速度
                    @Override
                    public long timeInterval() {
                        return 2000;
                    }

                    @Override
                    public void onSpeedChanged(String speed) {
                        tvSpeed.setText(String.format("下载速度 %s", speed));
                    }
                });
            } catch (Exception e) {
                Toast.show(this, "错误：" + e.toString());
            }
        } else {
            Toast.show(this, "请检查你的输入是否正确");
        }
    };

    private void onDownloadFinish() {
        isDownloadRun = false;
    }

    private void onDownloadStart() {
        isDownloadRun = true;
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        etDownloadUrl = findViewById(R.id.adt_et_download_url);
        etSaveUrl = findViewById(R.id.adt_et_save_url);
        tvDownloadBtn = findViewById(R.id.adt_tv_download_btn);
        pbProgress = findViewById(R.id.adt_pb_progress);
        tvSpeed = findViewById(R.id.adt_tv_speed);
        tvProcess = findViewById(R.id.adt_tv_process);
    }
}
