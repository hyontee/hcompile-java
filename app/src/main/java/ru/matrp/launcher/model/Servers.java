package ru.matrp.launcher.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Servers {

	@SerializedName("name")
	@Expose
	private String name;

	@SerializedName("color")
	@Expose
	private String color;

	@SerializedName("status")
	@Expose
	private int status;

	@SerializedName("recommend")
	@Expose
	private boolean recommend;

	@SerializedName("newstatus")
	@Expose
	private boolean newstatus;

	@SerializedName("edgar_host")
	@Expose
	private String edgar_host;

	@SerializedName("edgar_port")
	@Expose
	private int edgar_port;

	@SerializedName("id")
	@Expose
	private int id;

	public Servers(String name, String color, int status, boolean recommend, boolean newstatus, String edgar_host, int edgar_port, int id) {
		this.name = name;
		this.color = color;
		this.status = status;
		this.recommend = recommend;
		this.newstatus = newstatus;
		this.edgar_host = edgar_host;
		this.edgar_port = edgar_port;
		this.id = id;
	}
	 
	public String getName() {
		return name;
	}

    public String getColor() {
		return color;
	}

	public int getStatus() {
		return status;
	}

	public boolean getRecommend(){
		return recommend;
	}

	public boolean getNewStatus() {
		return newstatus;
	}

	public String getEdgarHost() {
		return edgar_host;
	}

	public int getEdgarPort(){
		return edgar_port;
	}

	public int getId(){
		return id;
	}
}
