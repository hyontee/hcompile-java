package ru.matrp.launcher.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import kotlin.jvm.internal.Intrinsics;

public class FaqList {
    @SerializedName("faqList")
    private List<Faq> faqList;

    public static FaqList copy$default(FaqList FaqList, List<Faq> list, int i, Object obj) {
        if ((i & 1) != 0) {
            list = FaqList.faqList;
        }
        return FaqList.copy(list);
    }

    public final List<Faq> component1() {
        return this.faqList;
    }

    public final FaqList copy(List<Faq> list) {
        Intrinsics.checkNotNullParameter(list, "faqList");
        return new FaqList(list);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof FaqList) && Intrinsics.areEqual((Object) this.faqList, (Object) ((FaqList) obj).faqList);
    }

    public int hashCode() {
        return this.faqList.hashCode();
    }

    public String toString() {
        return "FaqList(faqList=" + this.faqList + ')';
    }

    public FaqList(List<Faq> list) {
        Intrinsics.checkNotNullParameter(list, "faqList");
        this.faqList = list;
    }

    public final List<Faq> getArray() {
        return this.faqList;
    }

    public final void setArray(List<Faq> list) {
        Intrinsics.checkNotNullParameter(list, "<set-?>");
        this.faqList = list;
    }
}
