package com.android.dailyplanning.app;

import android.app.Application;

import com.android.dailyplanning.entity.User;
import com.android.dailyplanning.utils.SharedPreferencesUtils;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;

public class App extends Application {
    public static App app;
    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        SharedPreferencesUtils.setContext(this);
        Bmob.initialize(this, "49e69811daf7c3e479513176aa39e61e");
    }

    public static User getUser(){
        return BmobUser.getCurrentUser(User.class);
    }
}
