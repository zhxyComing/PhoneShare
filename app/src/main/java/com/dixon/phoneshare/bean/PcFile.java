package com.dixon.phoneshare.bean;

public class PcFile {

    private String path;
    private String name;
    private long size;
    private boolean directory;

    public PcFile() {

    }

    public PcFile(String path, String name, long size, boolean directory) {
        this.path = path;
        this.name = name;
        this.size = size;
        this.directory = directory;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isDirectory() {
        return directory;
    }

    public void setDirectory(boolean directory) {
        this.directory = directory;
    }

    @Override
    public String toString() {
        return "PcFile{" +
                "path='" + path + '\'' +
                ", name='" + name + '\'' +
                ", size=" + size +
                ", directory=" + directory +
                '}';
    }
}
