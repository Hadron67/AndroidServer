package com.cfy.project3.settable;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import Server.ServerConfig;
import Views.InputDialogue;

/**
 * Created by cfy on 15-12-14.
 */
public class PortSettable implements Settable{
    private ServerConfig config;

    private Context ctx = null;

    public PortSettable(Context ctx,ServerConfig config){
        this.config = config;
        this.ctx = ctx;
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
    public void startSettingAction() {
        final InputDialogue diag = new InputDialogue(ctx);
        diag.setTitle(getName());
        diag.setOnEnterClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int port = 0;
                try {
                    port = Integer.parseInt(diag.getText());
                }
                catch (NumberFormatException e){
                    Toast.makeText(ctx,"Please input a number.",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(port > 65535 || port < 0){
                    Toast.makeText(ctx,"port out of bounds",Toast.LENGTH_SHORT).show();
                }
                else{
                    config.setPort(port);
                    diag.dismiss();
                }
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
