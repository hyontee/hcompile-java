package ru.matrp.launcher.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FilesList {
    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("size")
    @Expose
    private String size;

    @SerializedName("hash")
    @Expose
    private String hash;

    @SerializedName("path")
    @Expose
    private String path;

    @SerializedName("url")
    @Expose
    private String url;

    // copy matrp by EDGAR DEVELOPER / by EDGAR 3.0 https://github.com/edgar-code
    // created at 13.01.2024

    // Конструктор
    public FilesList(String name, String size, String hash, String path, String url) {
        this.name = name;
        this.size = size;
        this.hash = hash;
        this.path = path;
        this.url = url;
    }

    // Геттеры
    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }

    public String getHash() {
        return hash;
    }

    public String getPath() {
        return path;
    }

    public String getUrl() {
        return url;
    }
}
