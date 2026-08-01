package ru.matrp.launcher.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Api {

    @SerializedName("launcher_version")
    @Expose
    private Integer launcher_version;

    @SerializedName("isTest")
    @Expose
    private boolean isTest;

    @SerializedName("test_api")
    @Expose
    private boolean test_api;

    @SerializedName("api")
    @Expose
    private String api;

    @SerializedName("archives") // update at 19.03.24
    private List<Archive> archives;

    // copy matrp by EDGAR DEVELOPER / by EDGAR 3.0 https://github.com/edgar-code
    // created at 04.01.2024

    public Api(Integer launcher_version, boolean isTest, boolean test_api, String api, List<Archive> archives) {
        this.launcher_version = launcher_version;
        this.isTest = isTest;
        this.test_api = test_api;
        this.api = api;
        this.archives = archives;

    }

    public Integer getLauncherVersion() {
        return launcher_version;
    }

    public boolean getIsTest() {
        return isTest;
    }

    public boolean getTestApi() {
        return test_api;
    }

    public String getApi() {
        return api;
    }

    public List<Archive> getArchives() {
        return this.archives;
    }

}
