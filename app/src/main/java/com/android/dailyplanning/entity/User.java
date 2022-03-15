package com.android.dailyplanning.entity;

import cn.bmob.v3.BmobUser;

public class User extends BmobUser {
    private String nick;
    private String icon;

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
