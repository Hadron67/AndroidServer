package com.cfy.project3;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;

import Server.Session;
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
                    server.startService();
                    sendMsg("Server started successfully.");
                    sendState();
                    break;
                case 1:
                    server.stopService();
                    sendMsg("Server stopped successfully.");
                    sendState();
                    break;
                case 2:
                    sendStatus();
                    sendState();
                    break;
            }
        }
    };


    private WebServer server = null;


    private boolean stopped = false;

    @Override
    public void onCreate() {
        super.onCreate();
        server = new WebServer(8000);
        server.chroot(Environment.getExternalStorageDirectory().getAbsolutePath() + "/webserver");

        IntentFilter inf = new IntentFilter();
        inf.addAction("ServerCommand");
        registerReceiver(commandReceiver, inf);
        server.setOnSessionEventListener(new WebServer.OnSessionEventListener() {
            @Override
            public void OnSessionStart(Session s) {
                sendState();
            }

            @Override
            public void OnSessionStop(Session s) {
                sendState();
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
        server.stopService();
    }

    private void sendMsg(String msg){
        Intent intent2 = new Intent();
        intent2.setAction("ServerResult");
        intent2.putExtra("result",0);
        intent2.putExtra("toastmsg", msg);
        sendBroadcast(intent2);
    }
    private void sendStatus(){
        Intent intent2 = new Intent();
        intent2.setAction("ServerResult");
        intent2.putExtra("result",1);
        intent2.putExtra("status",server.isRunning());
        sendBroadcast(intent2);
    }
    private void sendState(){
        Intent intent2 = new Intent();
        intent2.setAction("ServerResult");
        intent2.putExtra("result", 2);
        if(server.isRunning()) {
            String output = "port : " + server.getPort() + "\n";
            ArrayList<Session> sessions = server.getSessions();
            output += "Sessions : " + sessions.size() + "\n";
            for (Session ss : sessions) {
                output += ss.getIP() + "\n";
            }
            intent2.putExtra("state", output);
        }
        else{
            intent2.putExtra("state", "Server not running.");
        }
        sendBroadcast(intent2);
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
}
