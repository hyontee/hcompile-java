package ru.matrp.launcher.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Main {
    @SerializedName("servers")
    @Expose
    private String servers;

    @SerializedName("stories")
    @Expose
    private String stories;

    @SerializedName("faq")
    @Expose
    private String faq;

    @SerializedName("createCharacter") // update at 06.01.24
    @Expose
    private String createCharacter;

    @SerializedName("verifyAuth") // update at 06.01.24
    @Expose
    private String verifyAuth;

    @SerializedName("accountDetails") // update at 06.01.24
    @Expose
    private String accountDetails;

    @SerializedName("isAcc") // update at 06.01.24
    @Expose
    private String isAcc;

    @SerializedName("skinsCDN") // update at 09.01.24
    @Expose
    private String skinsCDN;

    // copy matrp by EDGAR DEVELOPER / by EDGAR 3.0 https://github.com/edgar-code
    // created at 04.01.2024

    public Main(String servers, String stories, String faq, String createCharacter, String verifyAuth, String accountDetails, String isAcc, String skinsCDN) {
        this.servers = servers;
        this.stories = stories;
        this.faq = faq;
        this.createCharacter = createCharacter;
        this.verifyAuth = verifyAuth;
        this.accountDetails = accountDetails;
        this.isAcc = isAcc;
        this.skinsCDN = skinsCDN;
    }

    public String getServers() {
        return servers;
    }

    public String getStories() {
        return stories;
    }

    public String getFaq() {
        return faq;
    }

    public String getCreateCharacter() {
        return createCharacter;
    }

    public String getVerifyAuth() {
        return verifyAuth;
    }

    public String getAccountDetails() {
        return accountDetails;
    }

    public String getIsAcc() {
        return isAcc;
    }

    public String getSkinsCDN() {
        return skinsCDN;
    }
}
