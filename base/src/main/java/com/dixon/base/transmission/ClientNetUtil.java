package com.dixon.base.transmission;

import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dixon.base.HandlerUtil;
import com.dixon.tools.SizeFormat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 参考自 https://juejin.im/post/5d7e014351882576c123fd87
 * https://www.jianshu.com/p/d176b35510c9
 * <p>
 * todo 代码未测试 可能不可用
 */
public class ClientNetUtil {

    private static OkHttpClient sClient = new OkHttpClient();

    public static void init() {
        // 增设下载监听
    }

    /**
     * 上传
     * <p>
     * 参考第一个链接封装Body体回调进度
     *
     * @param url
     * @param file
     * @param listener
     */
    public static void upload(String url, File file, final OnProgressChangedListener listener) {
        //构造上传请求，类似web表单
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("photo", file.getName(), RequestBody.create(null, file))
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"another\";filename=\"another.dex\""), RequestBody.create(MediaType.parse("application/octet-stream"), file))
                .build();

        // todo:if cant run, try-this!
//        RequestBody requestBody = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("file", file.getName(),
//                        RequestBody.create(MediaType.parse("multipart/form-data"), file))
//                .build();

        //进行包装，使其支持进度回调
        final Request request = new Request.Builder().url(url).post(ProgressHelper.addProgressRequestListener(requestBody, new UIProgressRequestListener() {
            @Override
            public void onUIRequestProgress(long bytesWrite, long contentLength, boolean done) {
                int progress = (int) (bytesWrite * 100f / contentLength);
                listener.onProgress(progress);
            }
        })).build();
        //开始请求
        sClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                HandlerUtil.runOnUiThread(() -> listener.onFail(e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    HandlerUtil.runOnUiThread(listener::onSuccess);
                } else {
                    HandlerUtil.runOnUiThread(() -> listener.onFail(new Exception("错误：" + response.code())));
                }
            }
        });
    }


    /**
     * 下载
     * <p>
     * 第一个链接的方案会永久添加拦截器，即使download完毕及时删除拦截器，也存在多线程下载拦截器干扰的问题。
     * 所以使用下面的方式。
     *
     * @param url          下载的url
     * @param saveDir      保存到的路径 会自动拼接根目录
     * @param listener     进度监听 不能为空
     * @param speedMonitor 下载速度监听 可为空
     */
    public static void download(final String name,
                                final String url,
                                final String saveDir,
                                @NonNull final OnProgressChangedListener listener,
                                @Nullable final SpeedMonitor speedMonitor) {
        Request request = new Request.Builder().url(url).addHeader("Accept-Encoding", "identity").build();
        sClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败
                HandlerUtil.runOnUiThread(() -> listener.onFail(e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                // 储存下载文件的目录
                String savePath = isExistDir(saveDir);
                try {
                    is = response.body().byteStream(); //获取byte流
                    long total = response.body().contentLength();
                    File file = new File(savePath, name);
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 100f / total);
                        Log.e("ClientNetUtil", "sum = " + sum + " total = " + total);
                        Log.e("ClientNetUtil", "progress = " + progress);
                        long finalSum = sum;
                        // 下载中
                        if (total == -1) { //有时候返回-1...这种情况触发下载量展示而非进度
                            HandlerUtil.runOnUiThread(() -> listener.onProcess("目前已下载：" + SizeFormat.format(finalSum)));
                        } else {
                            HandlerUtil.runOnUiThread(() -> listener.onProgress(progress));
                        }
                        if (speedMonitor != null) {
                            speedMonitor.calculation(sum);
                        }
                    }
                    fos.flush();
                    // 下载完成
                    HandlerUtil.runOnUiThread(listener::onSuccess);
                } catch (Exception e) {
                    HandlerUtil.runOnUiThread(() -> listener.onFail(e));
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException ignored) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        });
    }

    public interface OnProgressChangedListener {

        void onProgress(int progress);

        void onProcess(String message);

        void onFail(Exception e);

        void onSuccess();
    }

    public abstract static class SpeedMonitor {

        long startTime = 0;
        long disSum = 0;

        // 返回计算的时间间隔
        public abstract long timeInterval();

        public abstract void onSpeedChanged(String speed);

        public void calculation(long nowSum) {
            long nowTime = System.currentTimeMillis();
            // ns计算一次下载速度
            if (nowTime - startTime > timeInterval()) {
                long secondSpeed = (nowSum - disSum) / 3;
                HandlerUtil.runOnUiThread(() -> onSpeedChanged(SizeFormat.format(secondSpeed) + "/s"));
                disSum = nowSum;
                startTime = nowTime;
            }
        }
    }

    /**
     * @param saveDir
     * @return
     * @throws IOException 判断下载目录是否存在
     */
    private static String isExistDir(String saveDir) throws IOException {
        // 下载位置
        File downloadFile = new File(Environment.getExternalStorageDirectory(), saveDir);
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile();
        }
        String savePath = downloadFile.getAbsolutePath();
        return savePath;
    }

    /**
     * @param url
     * @return 从下载连接中解析出文件名
     */
    @NonNull
    public static String getNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }
}
