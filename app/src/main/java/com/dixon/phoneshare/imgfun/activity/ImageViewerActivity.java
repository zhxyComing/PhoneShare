package com.dixon.phoneshare.imgfun.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dixon.phoneshare.R;

import java.io.File;

public class ImageViewerActivity extends Activity {

    private ImageView mViewer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_image_viewer);

        String filePath = getIntent().getStringExtra("path");
        Glide.with(this)
                .load(Uri.fromFile(new File(filePath)))
                .into(mViewer);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mViewer = findViewById(R.id.aiv_iv_viewer);
    }

    private void setStatusBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //状态栏 color
        getWindow().setStatusBarColor(getResources().getColor(android.R.color.white));
    }
}
