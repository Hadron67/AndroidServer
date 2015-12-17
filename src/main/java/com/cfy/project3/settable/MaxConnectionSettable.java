package com.cfy.project3.settable;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.cfy.project3.R;

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
        return ctx.getString(R.string.text_limitation);
    }

    @Override
    public String getValue() {
        int m = config.getMaxConnections();
        if(m == -1) return ctx.getString(R.string.text_infinity);
        return Integer.toString(config.getMaxConnections());
    }

    @Override
    public void startSettingAction() {
        final InputDialogue diag = new InputDialogue(ctx);
        diag.setTitle(getName() + " " + ctx.getString(R.string.limitation_hint));
        diag.setOnEnterClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int maxConnections = 0;
                try {
                    maxConnections = Integer.parseInt(diag.getText());
                }
                catch (NumberFormatException e){
                    Toast.makeText(ctx,ctx.getString(R.string.please_input_number), Toast.LENGTH_SHORT).show();
                    return;
                }
                if(maxConnections < -1){
                    Toast.makeText(ctx,ctx.getString(R.string.invalid_value),Toast.LENGTH_SHORT).show();
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
