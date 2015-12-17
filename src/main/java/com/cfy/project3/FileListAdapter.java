package com.cfy.project3;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by cfy on 15-12-15.
 */
public class FileListAdapter extends BaseAdapter{

    private Context ctx;
    private List<File> files = null;
    private boolean hasParent;
    private boolean limited = false;
    private String top = null;

    private final Handler mhandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {


            super.handleMessage(msg);
        }
    };

    private static Comparator fileSorter = new Comparator() {
        @Override
        public int compare(Object lhs, Object rhs) {
            File f1 = (File) lhs;
            File f2 = (File) rhs;
            String s1 = f1.getName();
            String s2 = f2.getName();
                if(f1.isDirectory() && f2.isDirectory()){
                    return s1.compareTo(s2);
                }
                else if(f1.isDirectory()){
                    return -1;
                }
                else if(f2.isDirectory()){
                    return 1;
                }
                else{
                    return s1.compareTo(s2);
                }
        }
    };

    public FileListAdapter(Context ctx,File path){
        this.ctx = ctx;
        top = path.getAbsolutePath();
        setPath(path);
    }

    public void setPath(File path){
        files = new ArrayList<>();
        if(!path.exists() || path.isFile()){
            throw new IllegalArgumentException("Argument 2 must be a directory.");
        }
        File[] f = path.listFiles();
        if(f != null)
            Collections.addAll(files,f);
        Collections.sort(files,fileSorter);

        File parent = path.getParentFile();

        if(parent != null && !(limited && parent.getAbsolutePath().equals(top))){
            files.add(0,parent);
            hasParent = true;
        }
        else{
            hasParent = false;
        }
    }
    public void setLimited(boolean limited){
        this.limited = limited;
    }

    @Override
    public int getCount() {
        return files.size();
    }

    @Override
    public Object getItem(int position) {
        return files.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(ctx).inflate(R.layout.layout_filelistitem,null);
        }
        ImageView ic_file = (ImageView) convertView.findViewById(R.id.imgview_file);
        TextView text_name = (TextView) convertView.findViewById(R.id.textview_filename);

        File f = files.get(position);
        String name;
        if(position == 0 && hasParent){
            name = ctx.getString(R.string.parent_directory);
        }
        else{
            name = f.getName();
        }
        text_name.setText(name);
        if(f.isDirectory()){
            ic_file.setImageResource(R.mipmap.ic_folder);
        }
        else{
            ic_file.setImageResource(R.mipmap.ic_file);
        }
        return convertView;
    }

    public static class FileListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            FileListAdapter adapter = (FileListAdapter) parent.getAdapter();
            File file = (File) adapter.getItem(position);
            if(file.isDirectory()){
                adapter.setPath(file);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
