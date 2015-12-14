package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by cfy on 15-12-13.
 */
public class WebServer {
    public interface OnConnectionEventListener {
        void OnConnected(Connection s);
        void OnDisconnected(Connection s);
    }
    public interface MessageReceiver{
        void OnReceive(String msg,int level);
    }

    private OnConnectionEventListener mConnectionlistener = null;

    private MessageReceiver mMReceiver = null;

    private Socket client = null;
    private ServerSocket ssocket = null;


    protected ServerConfig config;

    private boolean isRunning = false;

    private ArrayList<Connection> connections = null;

    public WebServer(int port){
        connections = new ArrayList<>();

        config = new ServerConfig();
        config.LoadDefault();
        config.port = port;
    }

    public void setOnConnectionEventListener(OnConnectionEventListener listener){
        this.mConnectionlistener = listener;
    }

    public void setMessageReceiver(MessageReceiver receiver){
        this.mMReceiver = receiver;
    }
    public int getPort(){
        return config.port;
    }

    protected void sendmsg(String msg,int level){
        if(mMReceiver != null){
            mMReceiver.OnReceive(msg,level);
        }
    }

    public void chroot(String root){
        config.webRoot = root;
    }

    public boolean isRunning(){
        return isRunning;
    }


    public void startService() throws IOException {
        ssocket = new ServerSocket(config.port);
        sendmsg("server started successfully.",1);
        isRunning = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(isRunning){
                    try {
                        client = ssocket.accept();
                        Connection s = new Connection(WebServer.this,client);
                        if(config.maxConnections >=0 && connections.size() >= config.maxConnections){
                            s.disConnect();
                        }
                        else {
                            s.start();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    ssocket.close();
                    client.close();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                ssocket = null;
                client = null;
                sendmsg("server stopped.",1);
            }
        }).start();
    }

    public void setConfig(ServerConfig config){
        this.config = config;
    }

    public ServerConfig getConfig(){
        return this.config.clone();
    }

    public void stopService() throws IOException {
        isRunning = false;
        ssocket.close();
    }

    protected void callOnSessionStart(Connection s){
        connections.add(s);
        if(mConnectionlistener != null){
            mConnectionlistener.OnConnected(s);
        }
        else{
            sendmsg("session started",0);
        }
    }
    protected void callOnSessionStop(Connection s){
        connections.remove(s);
        if(mConnectionlistener != null){
            mConnectionlistener.OnDisconnected(s);
        }
        else{
            sendmsg("session stoppped",0);
        }
    }

    public final ArrayList<Connection> getConnections(){
        return connections;
    }
}
