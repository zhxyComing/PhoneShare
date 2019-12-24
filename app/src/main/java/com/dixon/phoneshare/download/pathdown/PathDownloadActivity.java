package com.dixon.phoneshare.download.pathdown;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dixon.base.api.ApiService;
import com.dixon.base.api.ApiServiceNet;
import com.dixon.base.transmission.ClientNetUtil;
import com.dixon.phoneshare.R;
import com.dixon.phoneshare.bean.PcFile;
import com.dixon.phoneshare.dialog.DialogUtil;
import com.dixon.tools.CustomDialog;
import com.dixon.tools.Toast;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class PathDownloadActivity extends Activity {

    private static final String FIRST_PAGE = "D:/";

    private GridView mGridView;
    private ProgressBar mLoadingView;
    private ImageView mSearchView;
    private EditText mPathShowView;
    private PcFileListAdapter mAdapter;

    private List<PcFile> mHistoryPath = new ArrayList<>();
    private PcFile mCurrentPcFile;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_path_download);

        initView();
        loadData();
    }

    private void initView() {
        mLoadingView.setVisibility(View.GONE);
        mGridView.setOnItemClickListener((parent, view, position, id) -> {
            PcFile file = mAdapter.getItems().get(position);
            if (file.isDirectory()) {
                //文件夹 打开
                jumpToPath(file);
            } else {
                //文件 提示下载
                showDownloadTipDialog(file);
            }
        });
    }

    private void showDownloadTipDialog(PcFile file) {
        DialogUtil.showTipDialog(this, "点击 OK 开始下载。存放目录：\nPhoneShare/", v -> {
            CustomDialog dialog = DialogUtil.showProgressDialog(PathDownloadActivity.this);
            TextView speedView = dialog.getView().findViewById(R.id.tvSpeed);
            TextView processView = dialog.getView().findViewById(R.id.tvProcess);
            ProgressBar progressBar = dialog.getView().findViewById(R.id.tvProgress);
            try {
                ClientNetUtil.download(ApiService.API_HOST + ApiService.API_DOWNLOAD + "?path=" + file.getPath(), "PhoneShare/", new ClientNetUtil.OnProgressChangedListener() {
                    @Override
                    public void onProgress(int progress) {
                        progressBar.setProgress(progress);
                    }

                    @Override
                    public void onProcess(String message) {
                        processView.setText(message);
                    }

                    @Override
                    public void onFail(Exception e) {
                        processView.setText(e.toString());
                        dialog.setCanceledOnTouchOutside(true);
                    }

                    @Override
                    public void onSuccess() {
                        processView.setText("下载完成");
                        dialog.setCanceledOnTouchOutside(true);
                    }
                }, new ClientNetUtil.SpeedMonitor() {
                    @Override
                    public long timeInterval() {
                        return 2;
                    }

                    @Override
                    public void onSpeedChanged(String speed) {
                        speedView.setText(speed);
                    }
                });
            } catch (Exception e) {
                dialog.dismiss();
                Toast.show(this, "错误：" + e.toString());
            }
        });
    }

    private void loadData() {
        mCurrentPcFile = new PcFile(FIRST_PAGE, "", 0, true);
        jumpToPathFirstIn(mCurrentPcFile);
    }

    private void jumpToPathFirstIn(PcFile file) {
        setLoadingShowStatus();
        mPathShowView.setText(file.getPath());
        ApiServiceNet.getPcFileListTest(file.getPath(), new ApiServiceNet.OnResultListener() {
            @Override
            public void onSuccess(String resultJson) {
                List<PcFile> list = parseJson(resultJson);
                loadFileGridView(list);
                setLoadingHideStatus();
            }

            @Override
            public void onFail(String msg) {
                setLoadingHideStatus();
            }
        });
    }

    private void jumpToPath(PcFile file) {
        if (isLoading) {
            Toast.show(this, "正在加载，请稍后...");
        }
        setLoadingShowStatus();
        ApiServiceNet.getPcFileListTest(file.getPath(), new ApiServiceNet.OnResultListener() {
            @Override
            public void onSuccess(String resultJson) {
                List<PcFile> list = parseJson(resultJson);
                loadFileGridView(list);
                setLoadingHideStatus();
                onPathJumpSuccessViewChange(file);
            }

            @Override
            public void onFail(String msg) {
                setLoadingHideStatus();
            }
        });
    }

    private void onPathJumpSuccessViewChange(PcFile file) {
        mPathShowView.setText(file.getPath());
        recordHistoryPath(mCurrentPcFile);
    }

    private void backToPath(PcFile file) {
        setLoadingShowStatus();
        ApiServiceNet.getPcFileListTest(file.getPath(), new ApiServiceNet.OnResultListener() {
            @Override
            public void onSuccess(String resultJson) {
                List<PcFile> list = parseJson(resultJson);
                loadFileGridView(list);
                setLoadingHideStatus();
                onPathBackSuccessViewChange(file);
            }

            @Override
            public void onFail(String msg) {
                setLoadingHideStatus();
            }
        });
    }

    private void onPathBackSuccessViewChange(PcFile file) {
        mPathShowView.setText(file.getPath());
    }

    private List<PcFile> parseJson(String resultJson) {
        //先转JsonObject
        JsonObject jsonObject = new JsonParser().parse(resultJson).getAsJsonObject();
        //再转JsonArray 加上数据头
        JsonArray jsonArray = jsonObject.getAsJsonArray("list");

        Gson gson = new Gson();
        ArrayList<PcFile> pcFileList = new ArrayList<>();

        //循环遍历
        for (JsonElement user : jsonArray) {
            //通过反射 得到UserBean.class
            PcFile pcFile = gson.fromJson(user, new TypeToken<PcFile>() {
            }.getType());
            pcFileList.add(pcFile);
        }
        return pcFileList;
    }

    private void loadFileGridView(List<PcFile> list) {
        if (mAdapter == null) {
            mAdapter = new PcFileListAdapter(this, list);
            mGridView.setAdapter(mAdapter);
        } else {
            mAdapter.getItems().clear();
            mAdapter.getItems().addAll(list);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void setStatusBar() {
        // 去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 去除状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mGridView = findViewById(R.id.apd_gv_list);
        mPathShowView = findViewById(R.id.apd_et_path_show);
        mLoadingView = findViewById(R.id.apd_pb_loading);
        mSearchView = findViewById(R.id.apd_iv_search);
    }

    private void recordHistoryPath(PcFile file) {
        if (file.isDirectory()) {
            mHistoryPath.add(file);
            // 只有当前文件是文件夹 可打来 才能添加历史并重置当前节点
            mCurrentPcFile = file;
        }
    }

    @Override
    public void onBackPressed() {
        if (mHistoryPath.isEmpty()) {
            super.onBackPressed();
        } else {
            if (isLoading) {
                Toast.show(this, "正在加载，请稍后...");
                return;
            }
            backToPath(mHistoryPath.remove(mHistoryPath.size() - 1));
        }
    }

    private void setLoadingShowStatus() {
        isLoading = true;
        mLoadingView.setVisibility(View.VISIBLE);
        mSearchView.setVisibility(View.GONE);
    }

    private void setLoadingHideStatus() {
        isLoading = false;
        mLoadingView.setVisibility(View.GONE);
        mSearchView.setVisibility(View.VISIBLE);
    }
}
