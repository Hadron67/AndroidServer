package com.cfy.project3;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Objects;

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
            switch(intent.getIntExtra("result",3)){
                case 3:
                    Bundle b = intent.getExtras();
                    config = (ServerConfig) b.getSerializable("config");
                    sadapter.setConfigs(getSettables(config));
                    break;
            }
        }
    };

    private ListView list_settings = null;
    private SettingsAdapter sadapter = null;
    private ServerConfig config = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_settings);
        list_settings = (ListView) findViewById(R.id.listview_settings);
        sadapter = new SettingsAdapter(this);
        list_settings.setAdapter(sadapter);
        list_settings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Settable sa = (Settable) parent.getAdapter().getItem(position);
                sa.startSettingAction(SettingsActivity.this);
            }
        });



        IntentFilter inf = new IntentFilter();
        inf.addAction("ServerResult");
        registerReceiver(mReceiver, inf);

        sendGetConfig();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        sendConfig();
    }

    private ArrayList<Settable> getSettables(ServerConfig config){
        ArrayList<Settable> settables = new ArrayList<>();
        settables.add(new PortSettable(config));
        settables.add(new MaxConnectionSettable(config));
        return settables;
    }
    private void sendGetConfig(){
        Intent intent = new Intent();
        intent.setAction("ServerCommand");
        intent.putExtra("cmd", 3);
        sendBroadcast(intent);
    }
    private void sendConfig(){
        Intent intent = new Intent();
        intent.setAction("ServerCommand");
        intent.putExtra("cmd", 4);
        intent.putExtra("new_config",config);
        sendBroadcast(intent);
    }
}
