package com.cfy.project3;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cfy.project3.settable.Error403PageSettable;
import com.cfy.project3.settable.Error404PageSettable;
import com.cfy.project3.settable.HomePageSettable;
import com.cfy.project3.settable.MaxConnectionSettable;
import com.cfy.project3.settable.PortSettable;
import com.cfy.project3.settable.Settable;
import com.cfy.project3.settable.WebRootSettable;

import java.util.ArrayList;

import Server.ServerConfig;

/**
 * Created by cfy on 15-12-14.
 */
public class SettingsActivity extends Activity{
    public class SettingsAdapter extends BaseAdapter{
        private ArrayList<Settable> configs = null;

        private Context ctx;

        public SettingsAdapter(Context ctx){
            this.ctx  = ctx;
            configs = new ArrayList<>();
        }

        public void setConfigs(ArrayList<Settable> configs){
            this.configs = configs;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return configs.size();
        }

        @Override
        public Object getItem(int position) {
            return configs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = LayoutInflater.from(ctx).inflate(R.layout.settings_list_item,null);
            }
            TextView name = (TextView) convertView.findViewById(R.id.text_name);
            TextView value = (TextView) convertView.findViewById(R.id.text_value);
            name.setText(configs.get(position).getName());
            value.setText(configs.get(position).getValue());

            return convertView;
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("ServerResult")){
                switch(intent.getIntExtra("result",3)){
                    case 3:
                        Bundle b = intent.getExtras();
                        config = (ServerConfig) b.getSerializable("config");
                        sadapter.setConfigs(getSettables(config));
                        break;
                }
            }
            else if(intent.getAction().equals("ChangeSettings")){
                switch(intent.getIntExtra("which_setting",0)){
                    case 0:
                        String path = intent.getStringExtra("newValue");
                        config.setWebRoot(path);
                        sadapter.notifyDataSetChanged();
                        break;
                    case 1:
                        String path2 = intent.getStringExtra("newValue");
                        config.setError404Page(path2);
                        sadapter.notifyDataSetChanged();
                    case 2:
                        String path3 = intent.getStringExtra("newValue");
                        config.setError403Page(path3);
                        sadapter.notifyDataSetChanged();
                        break;
                    case 3:
                        String path4 = intent.getStringExtra("newValue");
                        String h = path4.substring(config.getWebRoot().length(),path4.length());
                        config.setHomepage(h);
                        sadapter.notifyDataSetChanged();
                        break;
                }
            }
        }
    };

    private ListView list_settings = null;
    private SettingsAdapter sadapter = null;
    private ServerConfig config = null;

    private LocalBroadcastManager mbroadcastmanager = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mbroadcastmanager = LocalBroadcastManager.getInstance(this);
        setContentView(R.layout.layout_settings);
        list_settings = (ListView) findViewById(R.id.listview_settings);
        sadapter = new SettingsAdapter(this);
        list_settings.setAdapter(sadapter);
        list_settings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Settable sa = (Settable) parent.getAdapter().getItem(position);
                sa.startSettingAction();
            }
        });



        IntentFilter inf = new IntentFilter();
        inf.addAction("ServerResult");
        inf.addAction("ChangeSettings");
        mbroadcastmanager.registerReceiver(mReceiver, inf);

        sendGetConfig();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_ok:
                sendConfig();
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mbroadcastmanager.unregisterReceiver(mReceiver);
    }

    private ArrayList<Settable> getSettables(ServerConfig config){
        ArrayList<Settable> settables = new ArrayList<>();
        settables.add(new PortSettable(this,config));
        settables.add(new WebRootSettable(this,config));
        settables.add(new Error404PageSettable(this,config));
        settables.add(new Error403PageSettable(this,config));
        settables.add(new HomePageSettable(this,config));
        settables.add(new MaxConnectionSettable(this,config));
        return settables;
    }
    private void sendGetConfig(){
        Intent intent = new Intent();
        intent.setAction("ServerCommand");
        intent.putExtra("cmd", 3);
        mbroadcastmanager.sendBroadcast(intent);
    }
    private void sendConfig(){
        Intent intent = new Intent();
        intent.setAction("ServerCommand");
        intent.putExtra("cmd", 4);
        intent.putExtra("new_config",config);
        mbroadcastmanager.sendBroadcast(intent);
    }
}
