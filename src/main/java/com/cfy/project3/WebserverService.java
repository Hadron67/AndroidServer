package com.cfy.project3;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import Server.Connection;
import Server.ServerConfig;
import Server.WebServer;

/**
 * Created by cfy on 15-12-13.
 *
 */
public class WebserverService extends Service{

    private BroadcastReceiver commandReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch(intent.getIntExtra("cmd",0)){
                case 0:
                    try {
                        server.startService();
                    } catch (IOException e) {
                        sendMsg(getString(R.string.server_failed_to_start));
                        e.printStackTrace();
                    }
                    sendState();
                    sendStatus();
                    break;
                case 1:
                    try {
                        server.stopService();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    sendState();
                    sendStatus();
                    break;
                case 2:
                    sendStatus();
                    sendState();
                    break;
                case 3:
                    sendConfig();
                    break;
                case 4:
                    Bundle b = intent.getExtras();
                    server.setConfig((ServerConfig) b.getSerializable("new_config"));
                    try {
                        SaveConfig(new File(Environment.getExternalStorageDirectory() + "webserver/.webserver"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    private LocalBroadcastManager mbroadcastmanager = null;

    private WebServer server = null;

    private boolean stopped = false;

    @Override
    public void onCreate() {
        super.onCreate();
        mbroadcastmanager = LocalBroadcastManager.getInstance(this);

        server = new WebServer();

        try{
            server.setConfig(LoadConfigFromFile(new File(Environment.getExternalStorageDirectory() + "/webserver/.webserver")));
        }
        catch (Exception e){
            server.chroot(Environment.getExternalStorageDirectory() + "/webserver");
        }

        IntentFilter inf = new IntentFilter();
        inf.addAction("ServerCommand");
        mbroadcastmanager.registerReceiver(commandReceiver, inf);
        server.setOnConnectionEventListener(new WebServer.OnConnectionEventListener() {
            @Override
            public void OnConnected(Connection s) {
                sendState();
            }

            @Override
            public void OnDisconnected(Connection s) {
                sendState();
            }
        });
        server.setMessageReceiver(new WebServer.MessageReceiver() {
            @Override
            public void OnReceive(String msg,int level) {
                switch (level) {
                    case 0:
                        Log.d("web server", msg);
                        break;
                    case 1:
                        sendMsg(msg);
                }
            }
        });

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            server.stopService();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMsg(String msg){
        Intent intent2 = new Intent();
        intent2.setAction("ServerResult");
        intent2.putExtra("result",0);
        intent2.putExtra("toastmsg", msg);
        mbroadcastmanager.sendBroadcast(intent2);
    }
    private void sendStatus(){
        Intent intent2 = new Intent();
        intent2.setAction("ServerResult");
        intent2.putExtra("result",1);
        intent2.putExtra("status",server.isRunning());
        mbroadcastmanager.sendBroadcast(intent2);
    }
    private void sendState(){
        Intent intent2 = new Intent();
        intent2.setAction("ServerResult");
        intent2.putExtra("result", 2);
        if(server.isRunning()) {
            String output = "port : " + server.getPort() + "\n";
            ArrayList<Connection> connections = server.getConnections();
            synchronized (connections) {
                output += "Connections : " + connections.size() + "\n";
                for (Connection ss : connections) {
                    output += ss.getIP() + ":" + ss.getPort() + "\n";
                }
                intent2.putExtra("state", output);
            }
        }
        else{
            intent2.putExtra("state", getString(R.string.server_not_running));
        }
        mbroadcastmanager.sendBroadcast(intent2);
    }

    private void sendConfig(){
        Intent intent = new Intent();
        intent.setAction("ServerResult");
        intent.putExtra("result",3);
        intent.putExtra("config",server.getConfig());
        mbroadcastmanager.sendBroadcast(intent);
    }


    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case 0:
                    Log.d("webserver",msg.obj.toString());
            }
            super.handleMessage(msg);
        }
    };

    private void initFiles(){
        String path = Environment.getExternalStorageDirectory().toString();
        File webserver = new File(path);
        if(!webserver.exists() || !webserver.isDirectory()){
            webserver.mkdir();
        }
        File www = new File(path + "/www");
        if(!www.exists() || !www.isDirectory()){
            www.mkdir();
        }
    }

    private ServerConfig LoadConfigFromFile(File file) throws IOException, ClassNotFoundException {
        ObjectInputStream is = new ObjectInputStream(new FileInputStream(file));
        return (ServerConfig) is.readObject();
    }

    private void SaveConfig(File file) throws IOException {
        ServerConfig config = server.getConfig();
        ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(file));
        os.writeObject(config);
    }
}
