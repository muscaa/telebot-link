package com.github.muscaa.telebot.link.network;

public class StunClient extends TcpClient<StunClient, StunClientListener<StunClient>> {

    private String name;

    public StunClient() {
        super(new StunClientListener<>());
    }

    public void link(String name) {
        listener.sendC2SLinkRequest(this, name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
