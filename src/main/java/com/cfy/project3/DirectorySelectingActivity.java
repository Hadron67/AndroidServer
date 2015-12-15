package com.cfy.project3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;

/**
 * Created by cfy on 15-12-15.
 *
 */
public class DirectorySelectingActivity extends Activity{

    private ListView fileList = null;
    private FileListAdapter adapter = null;
    private TextView text_path = null;

    private LocalBroadcastManager mbroadcastmanager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mbroadcastmanager = LocalBroadcastManager.getInstance(this);
        setContentView(R.layout.layout_choose_file);
        String path = getIntent().getStringExtra("path");
        adapter = new FileListAdapter(this,new File(path));
        fileList = (ListView) findViewById(R.id.listview_files);
        text_path = (TextView) findViewById(R.id.textview_path);
        text_path.setText(path);

        fileList.setAdapter(adapter);
        fileList.setOnItemClickListener(new FileListAdapter.FileListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File f = (File) parent.getAdapter().getItem(position);
                if(f.isDirectory())
                    text_path.setText(((File)parent.getAdapter().getItem(position)).getAbsolutePath());
                super.onItemClick(parent, view, position, id);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_select_directory, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()){
            case R.id.item_ok:
                String path = text_path.getText().toString();
                sendPath(path);
                finish();
                break;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    private void sendPath(String path){
        Intent intent = new Intent();
        intent.setAction("ChangeSettings");
        intent.putExtra("newValue", path);
        intent.putExtra("which_setting",0);
        mbroadcastmanager.sendBroadcast(intent);
    }
}
