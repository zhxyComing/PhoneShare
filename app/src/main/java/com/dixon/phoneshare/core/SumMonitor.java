package com.dixon.phoneshare.core;

/**
 * 用于记录多文件上传下载n成功n失败
 */
public class SumMonitor {
    private int sum;
    private int success;
    private int fail;
    private OnOverListener listener;

    public SumMonitor(int sum) {
        this.sum = sum;
    }

    public void setOnOverListener(OnOverListener listener) {
        this.listener = listener;
    }

    public void addSuccess() {
        success += 1;
        overJudge();
    }

    public void addFail() {
        fail += 1;
        overJudge();
    }

    public void overJudge() {
        if (fail + success >= sum && listener != null) {
            listener.onOver();
        }
    }

    public interface OnOverListener {
        void onOver();
    }

    public int getSuccess() {
        return success;
    }

    public int getFail() {
        return fail;
    }
}