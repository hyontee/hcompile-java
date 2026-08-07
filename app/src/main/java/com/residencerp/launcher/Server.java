package com.residencerp.launcher;

/**
 * Модель игрового сервера RESIDENCE RP.
 * name  - отображаемое имя в списке
 * ip    - адрес сервера
 * port  - порт сервера
 */
public class Server {
    public final String name;
    public final String ip;
    public final int port;

    public Server(String name, String ip, int port) {
        this.name = name;
        this.ip = ip;
        this.port = port;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getAddress() {
        return ip + ":" + port;
    }
}
