package com.cfy.project3.settable;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import Server.ServerConfig;
import Views.InputDialogue;

/**
 * Created by cfy on 15-12-14.
 */
public class MaxConnectionSettable implements Settable{
    private ServerConfig config;

    private Context ctx;

    public MaxConnectionSettable(Context ctx,ServerConfig config){
        this.config = config;
        this.ctx = ctx;
    }

    @Override
    public String getName() {
        return "Limitation";
    }

    @Override
    public String getValue() {
        int m = config.getMaxConnections();
        if(m == -1) return "Infinity";
        return Integer.toString(config.getMaxConnections());
    }

    @Override
    public void startSettingAction() {
        final InputDialogue diag = new InputDialogue(ctx);
        diag.setTitle(getName() + " (-1 for infinity)");
        diag.setOnEnterClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int maxConnections = 0;
                try {
                    maxConnections = Integer.parseInt(diag.getText());
                }
                catch (NumberFormatException e){
                    Toast.makeText(ctx, "Please input a number.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(maxConnections < -1){
                    Toast.makeText(ctx,"Invalid value.",Toast.LENGTH_SHORT).show();
                }
                else{
                    config.setMaxConnections(maxConnections);
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
