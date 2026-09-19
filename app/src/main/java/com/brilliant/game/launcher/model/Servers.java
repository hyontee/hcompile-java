package com.brilliant.game.launcher.model;

public class Servers {

    private final String ip;
    private final int port;
    private final String name;
    private final int online;
    private final int maxOnline;

    public Servers(String ip, int port, String name, int online, int maxOnline) {
        this.ip = ip;
        this.port = port;
        this.name = name;
        this.online = online;
        this.maxOnline = maxOnline;
    }

    public String getName() {
        return name;
    }

    public String getIP() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public int getOnline() {
        return online;
    }

    public int getMaxOnline() {
        return maxOnline;
    }
}
