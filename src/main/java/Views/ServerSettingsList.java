package Views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import Server.ServerConfig;

/**
 * Created by cfy on 15-12-15.
 */
public class ServerSettingsList extends ListView{

    private ServerConfig config;

    public ServerSettingsList(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
