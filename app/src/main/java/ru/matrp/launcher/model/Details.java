package ru.matrp.launcher.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Details {
    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("skin")
    @Expose
    private String skin;

    @SerializedName("sex")
    @Expose
    private String sex;

    @SerializedName("lvl")
    @Expose
    private String lvl;

    @SerializedName("exp")
    @Expose
    private String exp;

    @SerializedName("needexp")
    @Expose
    private String needexp;

    @SerializedName("cash")
    @Expose
    private String cash;

    @SerializedName("bank")
    @Expose
    private String bank;

    @SerializedName("number")
    @Expose
    private String number;

    @SerializedName("fraction")
    @Expose
    private String fraction;

    @SerializedName("rank")
    @Expose
    private String rank;

    @SerializedName("promo")
    @Expose
    private String promo;

    @SerializedName("admin")
    @Expose
    private String admin;

    @SerializedName("house")
    @Expose
    private String house;

    @SerializedName("donate")
    @Expose
    private String donate;

    @SerializedName("business")
    @Expose
    private String business;

    // Конструктор
    public Details(String status, String name, String skin, String sex, String lvl, String exp, String needexp, String cash, String bank, String number, String fraction, String rank, String promo, String admin, String house, String donate, String business) {
        this.status = status;
        this.name = name;
        this.skin = skin;
        this.sex = sex;
        this.lvl = lvl;
        this.exp = exp;
        this.needexp = needexp;
        this.cash = cash;
        this.bank = bank;
        this.number = number;
        this.fraction = fraction;
        this.rank = rank;
        this.promo = promo;
        this.admin = admin;
        this.house = house;
        this.donate = donate;
        this.business = business;
    }

    @Override
    public String toString() {
        return "UserDetails{" +
                "status=" + status +
                ", name='" + name + '\'' +
                ", skin='" + skin + '\'' +
                ", sex='" + sex + '\'' +
                ", lvl='" + lvl + '\'' +
                ", exp='" + exp + '\'' +
                ", cash='" + cash + '\'' +
                ", bank='" + bank + '\'' +
                ", number='" + number + '\'' +
                ", fraction='" + fraction + '\'' +
                ", rank='" + rank + '\'' +
                ", promo='" + promo + '\'' +
                ", admin='" + admin + '\'' +
                ", house='" + house + '\'' +
                ", donate='" + donate + '\'' +
                ", business='" + business + '\'' +
                '}';
    }

    public String getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public String getSkin() {
        return skin;
    }

    public String getSex() {
        return sex;
    }

    public String getLvl() {
        return lvl;
    }

    public String getExp() {
        return exp;
    }

    public String getNeedExp() {
        return needexp;
    }

    public String getCash() {
        return cash;
    }

    public String getBank() {
        return bank;
    }

    public String getNumber() {
        return number;
    }

    public String getFraction() {
        return fraction;
    }

    public String getRank() {
        return rank;
    }

    public String getPromo() {
        return promo;
    }

    public String getAdmin() {
        return admin;
    }

    public String getHouse() {
        return house;
    }

    public String getDonate() {
        return donate;
    }

    public String getBusiness() {
        return business;
    }

}
