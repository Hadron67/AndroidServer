package com.cfy.project3.settable;

import android.content.Context;
import android.content.Intent;

import com.cfy.project3.DirectorySelectingActivity;
import com.cfy.project3.R;

import Server.ServerConfig;

/**
 * Created by cfy on 15-12-15.
 */
public class WebRootSettable implements Settable{

    private ServerConfig config = null;
    private Context ctx;

    public WebRootSettable(Context ctx,ServerConfig config){
        this.config = config;
        this.ctx = ctx;
    }
    @Override
    public String getName() {
        return ctx.getString(R.string.text_web_root);
    }

    @Override
    public String getValue() {
        return config.getWebRoot();
    }

    @Override
    public void startSettingAction() {
        Intent intent = new Intent(ctx,DirectorySelectingActivity.class);
        intent.putExtra("path",getValue());
        ctx.startActivity(intent);
    }
}
