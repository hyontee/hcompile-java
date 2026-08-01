package ru.matrp.launcher.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Archive {
    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("zip_path")
    @Expose
    private String zip_path;

    @SerializedName("size")
    @Expose
    private long size;

    @SerializedName("urls")
    @Expose
    private String urls;

    @SerializedName("paths")
    @Expose
    private List<ArchivePath> paths;

    // copy matrp by EDGAR DEVELOPER / by EDGAR 3.0 https://github.com/edgar-code
    // Загрузка by EDGAR 3.0

    public Archive(String type, String zip_path, long size, String urls, List<ArchivePath> paths) {
        this.type = type;
        this.zip_path = zip_path;
        this.size = size;
        this.urls = urls;
        this.paths = paths;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getZip_path() {
        return zip_path;
    }

    public void setZip_path(String zip_path) {
        this.zip_path = zip_path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getUrls() {
        return urls;
    }

    public void setUrls(String urls) {
        this.urls = urls;
    }

    public List<ArchivePath> getPaths() {
        return paths;
    }

    public void setPaths(List<ArchivePath> paths) {
        this.paths = paths;
    }
}
