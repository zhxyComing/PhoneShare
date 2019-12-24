package com.dixon.phoneshare.download.linkdown;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dixon.base.transmission.ClientNetUtil;
import com.dixon.phoneshare.R;

public class LinkDownloadActivity extends AppCompatActivity {

    private EditText etDownloadUrl, etSaveUrl;
    private TextView tvDownloadBtn, tvProgress, tvSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_temp);

        tvDownloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String downloadUrl = etDownloadUrl.getText().toString();
                String saveUrl = etSaveUrl.getText().toString();
                // todo ? URLEncode?
                if (!TextUtils.isEmpty(downloadUrl)) {
                    ClientNetUtil.download(downloadUrl, saveUrl, new ClientNetUtil.OnProgressChangedListener() {
                        @Override
                        public void onProgress(final int progress) {
                            tvProgress.post(new Runnable() {
                                @Override
                                public void run() {
                                    tvProgress.setText(progress + "%");
                                }
                            });
                        }

                        @Override
                        public void onProcess(final String message) {
                            tvProgress.post(new Runnable() {
                                @Override
                                public void run() {
                                    tvProgress.setText(message);
                                }
                            });
                        }

                        @Override
                        public void onFail(final Exception e) {
                            tvProgress.post(new Runnable() {
                                @Override
                                public void run() {
                                    tvProgress.setText("下载失败 " + e.toString());
                                }
                            });
                        }

                        @Override
                        public void onSuccess() {
                            tvProgress.post(new Runnable() {
                                @Override
                                public void run() {
                                    tvProgress.setText("下载完成");
                                }
                            });
                        }

                        @Override
                        public void onSpeedProgress(final String speed) {
                            tvProgress.post(new Runnable() {
                                @Override
                                public void run() {
                                    tvSpeed.setText("下载速度 " + speed + "/s");
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        etDownloadUrl = findViewById(R.id.adt_et_download_url);
        etSaveUrl = findViewById(R.id.adt_et_save_url);
        tvDownloadBtn = findViewById(R.id.adt_tv_download_btn);
        tvProgress = findViewById(R.id.adt_tv_progress);
        tvSpeed = findViewById(R.id.adt_tv_speed);
    }
}
