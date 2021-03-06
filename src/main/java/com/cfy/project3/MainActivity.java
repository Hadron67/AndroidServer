package com.cfy.project3;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import Server.WebServer;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar = null;

    private BroadcastReceiver serverReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch(intent.getIntExtra("result",0)){
                case 0:
                    Toast.makeText(MainActivity.this,intent.getStringExtra("toastmsg"),Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    running = intent.getBooleanExtra("status",false);
                    setButtonStatus(running);
                    break;
                case 2:
                    tv_log.setText(intent.getStringExtra("state") + "\n");
                    break;
            }
        }
    };


    private ImageButton btn_control = null;
    private TextView tv_log = null;


    private boolean running = false;

    private LocalBroadcastManager mbroadcastmanager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mbroadcastmanager = LocalBroadcastManager.getInstance(this);
        setContentView(R.layout.activity_main);
        btn_control = (ImageButton) findViewById(R.id.btn_control);
        tv_log = (TextView) findViewById(R.id.textview_servermessage);
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);

        tv_log.setMovementMethod(new ScrollingMovementMethod());

        initActionbar();

        IntentFilter inf = new IntentFilter();
        inf.addAction("ServerResult");
        mbroadcastmanager.registerReceiver(serverReceiver, inf);

        Intent intent = new Intent(this,WebserverService.class);

        startService(intent);

        sendgetStatus();

        btn_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(running){
                    sendStopServer();
                }
                else{
                    sendStartServer();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            if(!running) {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
            }
            else {
                Toast.makeText(this,getString(R.string.must_stop_server),Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mbroadcastmanager.unregisterReceiver(serverReceiver);
    }

    private void initActionbar(){
        setSupportActionBar(mToolbar);
    }

    private void sendStartServer(){
        Intent intent = new Intent();
        intent.setAction("ServerCommand");
        intent.putExtra("cmd",0);
        mbroadcastmanager.sendBroadcast(intent);
    }

    private void sendStopServer(){
        Intent intent = new Intent();
        intent.setAction("ServerCommand");
        intent.putExtra("cmd",1);
        mbroadcastmanager.sendBroadcast(intent);
    }
    private void sendgetStatus(){
        Intent intent = new Intent();
        intent.setAction("ServerCommand");
        intent.putExtra("cmd",2);
        mbroadcastmanager.sendBroadcast(intent);
    }
    private void setButtonStatus(boolean r){
        this.running = r;
        if(r){
            btn_control.setImageResource(R.mipmap.ic_stop);
        }
        else{
            btn_control.setImageResource(R.mipmap.ic_start);
        }
    }
}
