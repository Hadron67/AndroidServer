package com.cfy.project3;

import android.content.Context;
import android.view.View;

import Server.ServerConfig;
import Views.InputDialogue;

/**
 * Created by cfy on 15-12-14.
 */
public class PortSettable implements Settable{
    private ServerConfig config;

    public PortSettable(ServerConfig config){
        this.config = config;
    }

    @Override
    public String getName() {
        return "Port";
    }

    @Override
    public String getValue() {
        return Integer.toString(config.getPort());
    }

    @Override
    public void startSettingAction(Context ctx) {
        final InputDialogue diag = new InputDialogue(ctx);
        diag.setTitle(getName());
        diag.setOnEnterClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                config.setPort(Integer.parseInt(diag.getText()));
                diag.dismiss();
            }
        });
        diag.setOnCancelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diag.dismiss();
            }
        });
        diag.show();
    }
}
