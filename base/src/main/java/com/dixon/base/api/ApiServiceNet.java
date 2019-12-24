package com.dixon.base.api;

import com.dixon.base.HandlerUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApiServiceNet {

    private static OkHttpClient sClient = new OkHttpClient();

    public static void getPcFileList(String path, OnResultListener listener) {
        Request request = new Request.Builder()
                .url(ApiService.API_HOST + ApiService.API_FILE_LIST + "?path=" + path)
                .build();
        //开始请求
        sClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                HandlerUtil.runOnUiThread(() -> listener.onFail(e.toString()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    if (response.body() == null) {
                        HandlerUtil.runOnUiThread(() -> listener.onSuccess(""));
                    } else {
                        String res = response.body().string();
                        HandlerUtil.runOnUiThread(() -> listener.onSuccess(res));
                    }
                } else {
                    HandlerUtil.runOnUiThread(() -> listener.onFail("错误：" + response.code()));
                }
            }
        });
    }

    public interface OnResultListener {

        void onSuccess(String resultJson);

        void onFail(String msg);
    }

}
