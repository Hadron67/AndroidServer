package Server;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by cfy on 15-12-13.
 */
public class WebServer {
    public interface OnSessionEventListener{
        void OnSessionStart(Session s);
        void OnSessionStop(Session s);
    }

    private OnSessionEventListener msessionlistener = null;

    private Socket client = null;
    private ServerSocket ssocket = null;

    protected String root = "/";

    private Handler handler = null;

    private int count;
    private int port;

    private boolean isRunning = false;

    private ArrayList<Session> sessions = null;

    public WebServer(int port){
        this.port = port;
        sessions = new ArrayList<>();
    }

    public void setMessageHandler(Handler handler){
        this.handler = handler;
    }
    public void setOnSessionEventListener(OnSessionEventListener listener){
        this.msessionlistener = listener;
    }
    public int getPort(){
        return port;
    }

    protected void sendmsg(String msg){
        if(handler == null){
            Log.d("Web server",msg);
        }
        else {
            Message m = new Message();
            m.what = 0;
            m.obj = msg;
            handler.sendMessage(m);
        }
    }

    public void chroot(String root){
        this.root = root;
    }

    public boolean isRunning(){
        return isRunning;
    }


    public void startService(){
        isRunning = true;
        try {
            ssocket = new ServerSocket(port);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while(isRunning){
                        try {
                            client = ssocket.accept();
                            Session s = new Session(WebServer.this,client);
                            s.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        ssocket.close();
                        client.close();
                    }
                    catch (IOException e){
                        e.printStackTrace();
                    }
                    ssocket = null;
                    client = null;
                }
            }).start();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void stopService(){
        isRunning = false;
    }

    protected void callOnSessionStart(Session s){
        sessions.add(s);
        if(msessionlistener != null){
            msessionlistener.OnSessionStart(s);
        }
        else{
            sendmsg("session started");
        }
    }
    protected void callOnSessionStop(Session s){
        sessions.remove(s);
        if(msessionlistener != null){
            msessionlistener.OnSessionStop(s);
        }
        else{
            sendmsg("session stoppped");
        }
    }

    public final ArrayList<Session> getSessions(){
        return sessions;
    }
    private boolean containsSession(Session s){
        for(Session ss : sessions){
            if(ss.getIP().equals(ss.getIP())) return true;
        }
        return false;
    }
}
