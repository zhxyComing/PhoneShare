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
                listener.onFail(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    if (response.body() == null) {
                        listener.onSuccess("");
                    } else {
                        listener.onSuccess(response.body().string());
                    }
                } else {
                    listener.onFail("错误：" + response.code());
                }
            }
        });
    }

    private static final String test = "{\n" +
            "\t\"list\": [{\n" +
            "\t\t\t\"name\": \"Google\",\n" +
            "\t\t\t\"path\": \"D://google\",\n" +
            "\t\t\t\"size\": 10000,\n" +
            "\t\t\t\"directory\": true\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"name\": \"Google\",\n" +
            "\t\t\t\"path\": \"D://google\",\n" +
            "\t\t\t\"size\": 10000,\n" +
            "\t\t\t\"directory\": true\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"name\": \"Google\",\n" +
            "\t\t\t\"path\": \"D://google\",\n" +
            "\t\t\t\"size\": 10000,\n" +
            "\t\t\t\"directory\": false\n" +
            "\t\t}\n" +
            "\t]\n" +
            "}";

    public static void getPcFileListTest(String path, OnResultListener listener) {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            HandlerUtil.runOnUiThread(() -> listener.onSuccess(test));
        }).start();
    }

    public interface OnResultListener {

        void onSuccess(String resultJson);

        void onFail(String msg);
    }

}
