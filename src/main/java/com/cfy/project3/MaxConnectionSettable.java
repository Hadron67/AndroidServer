package com.cfy.project3;

import android.content.Context;

import Server.ServerConfig;

/**
 * Created by cfy on 15-12-14.
 */
public class MaxConnectionSettable implements Settable{
    private ServerConfig config;

    public MaxConnectionSettable(ServerConfig config){
        this.config = config;
    }

    @Override
    public String getName() {
        return "Maxium connection count";
    }

    @Override
    public String getValue() {
        return Integer.toString(config.getMaxConnections());
    }

    @Override
    public void startSettingAction(Context ctx) {

    }
}
