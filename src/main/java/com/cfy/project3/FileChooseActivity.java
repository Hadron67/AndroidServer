package com.cfy.project3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

/**
 * Created by cfy on 15-12-15.
 */
public class FileChooseActivity extends AppCompatActivity{

    private ListView filelist = null;
    private TextView text_path = null;
    private FileListAdapter adapter = null;
    private LocalBroadcastManager mbroadcastmanager = null;

    private Toolbar mToolbar = null;

    private int which_setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mbroadcastmanager = LocalBroadcastManager.getInstance(this);
        setContentView(R.layout.layout_choose_file);
        Intent intent = getIntent();
        String path = intent.getStringExtra("path");
        which_setting = intent.getIntExtra("which_setting", 0);

        filelist = (ListView) findViewById(R.id.listview_files);
        text_path = (TextView) findViewById(R.id.textview_path);
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);

        adapter = new FileListAdapter(this,new File(path));
        adapter.setLimited(intent.getBooleanExtra("Locked", false));
        text_path.setText(path);
        filelist.setAdapter(adapter);
        filelist.setOnItemClickListener(new FileListAdapter.FileListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                text_path.setText(((File) parent.getAdapter().getItem(position)).getAbsolutePath());
                super.onItemClick(parent, view, position, id);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_select_file,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        switch(item.getItemId()){
            case R.id.item_ok:
                File f = new File(text_path.getText().toString());
                if(f.exists() && f.isFile()){
                    sendResult(f.getAbsolutePath());
                    finish();
                }
                else{
                    Toast.makeText(this,getString(R.string.pleas_choose_a_file),Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.item_use_default:
                sendResult("");
                finish();
                break;
        }

        return true;
    }

    private void sendResult(String s){
        Intent intent = new Intent();
        intent.setAction("ChangeSettings");
        intent.putExtra("newValue", s);
        intent.putExtra("which_setting",which_setting);
        mbroadcastmanager.sendBroadcast(intent);
    }
}
