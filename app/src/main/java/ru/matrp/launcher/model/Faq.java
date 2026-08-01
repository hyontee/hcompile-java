package ru.matrp.launcher.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Faq {
    @SerializedName("caption")
    @Expose
    private String caption;

    @SerializedName("text")
    @Expose
    private String text;

    // copy matrp by EDGAR DEVELOPER / by EDGAR 3.0 https://github.com/edgar-code

    public Faq(String caption, String text) {
        this.caption = caption;
        this.text = text;

    }

    public String getCaption() {
        return caption;
    }

    public String getText() {
        return text;
    }
}
