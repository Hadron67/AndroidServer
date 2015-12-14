package com.cfy.project3;

import android.content.Context;

/**
 * Created by cfy on 15-12-14.
 *
 */
public interface Settable {
    String getName();
    String getValue();
    void startSettingAction(Context ctx);
}
