package com.dixon.tools.file;

import java.text.NumberFormat;

public class ImageItem implements Comparable<ImageItem> {

    private String mFilePath;
    private String mFileName;
    private String mSize;

    /**
     * 创建时间
     */
    private String mDate;

    public ImageItem(String path, String name, String size, String date) {
        this.mFilePath = path;
        this.mFileName = name;
        this.mSize = size;
        this.mDate = date;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public void setFilePath(String mFilePath) {
        this.mFilePath = mFilePath;
    }

    public String getFileName() {
        return mFileName;
    }

    public void setFileName(String mFileName) {
        this.mFileName = mFileName;
    }

    public String getFileSize() {
        NumberFormat ddf1 = NumberFormat.getNumberInstance();
        ddf1.setMaximumFractionDigits(2);

        String sizeDisplay;
        long size = Long.valueOf(mSize);
        if (size > 1048576.0) {
            double result = size / 1048576.0;
            sizeDisplay = ddf1.format(result) + "M";
        } else if (size > 1024) {
            double result = size / 1024;
            sizeDisplay = ddf1.format(result) + "K";
        } else {
            sizeDisplay = ddf1.format(size) + "B";
        }
        return sizeDisplay;
    }

    public String getOriginSize() {
        return mSize;
    }

    public long getLongFileSize() {
        return Long.valueOf(mSize);
    }

    public String getDate() {
        return mDate;
    }

    @Override
    public int compareTo(ImageItem imageItem) {
        return (int) (Long.valueOf(imageItem.getDate()) - Long.valueOf(mDate));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("FileName:").append(mFileName).append("\n")
                .append("FileDate:").append(mDate).append("\n")
                .append("FileSize:").append(getFileSize()).append("\n")
                .append("FilePath:").append(mFilePath);
        return builder.toString();
    }
}