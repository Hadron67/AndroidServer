package Server;

import java.io.Serializable;

/**
 * Created by cfy on 15-12-14.
 */
public class ServerConfig implements Serializable,Cloneable{
    protected int port;
    protected String webRoot;
    protected int maxConnections;
    protected String Error404Page;
    protected String Error403Page;
    protected String homepage;

    public void LoadDefault(){
        port = 8000;
        webRoot = "/";
        maxConnections = -1;
        Error404Page = null;
        Error403Page = null;
        homepage = "/index.html";
    }

    public ServerConfig(){

    }

    public int getPort(){
        return port;
    }
    public void setPort(int port){
        this.port = port;
    }
    public String getWebRoot(){
        return webRoot;
    }

    public int getMaxConnections(){
        return maxConnections;
    }

    public ServerConfig clone(){
        try {
            return (ServerConfig) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

}
