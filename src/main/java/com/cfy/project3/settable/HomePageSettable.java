package com.cfy.project3.settable;

import android.content.Context;
import android.content.Intent;

import com.cfy.project3.FileChooseActivity;
import com.cfy.project3.R;

import Server.ServerConfig;

/**
 * Created by cfy on 15-12-15.
 */
public class HomePageSettable implements Settable{
    private Context ctx = null;
    private ServerConfig config = null;

    public HomePageSettable(Context ctx,ServerConfig config){
        this.ctx = ctx;
        this.config = config;
    }


    @Override
    public String getName() {
        return ctx.getString(R.string.home_page);
    }

    @Override
    public String getValue() {
        return "." + config.getHomepage();
    }

    @Override
    public void startSettingAction() {
        Intent intent = new Intent(ctx, FileChooseActivity.class);
        intent.putExtra("Locked",false);
        intent.putExtra("path",config.getWebRoot());
        intent.putExtra("which_setting",3);
        ctx.startActivity(intent);
    }
}
