package com.github.muscaa.telebot.link.network;

public class StunClient extends TcpClient<StunClient> {

    private String name;

    public StunClient() {
        super(new StunClientListener<>());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
