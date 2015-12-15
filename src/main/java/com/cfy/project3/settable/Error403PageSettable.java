package com.cfy.project3.settable;

import android.content.Context;
import android.content.Intent;

import com.cfy.project3.FileChooseActivity;
import com.cfy.project3.R;

import Server.ServerConfig;

/**
 * Created by cfy on 15-12-15.
 */
public class Error403PageSettable implements Settable{
    private Context ctx = null;
    private ServerConfig config = null;

    public Error403PageSettable(Context ctx, ServerConfig config){
        this.ctx = ctx;
        this.config = config;
    }


    @Override
    public String getName() {
        return ctx.getString(R.string.error_403_page);
    }

    @Override
    public String getValue() {
        String s = config.getError403Page();
        return s == null ? ctx.getString(R.string.text_default) : s;
    }

    @Override
    public void startSettingAction() {
        Intent intent = new Intent(ctx, FileChooseActivity.class);
        intent.putExtra("Locked",false);
        intent.putExtra("path",config.getWebRoot());
        intent.putExtra("which_setting",2);
        ctx.startActivity(intent);
    }
}
