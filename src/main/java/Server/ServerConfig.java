package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by cfy on 15-12-14.
 */
public class ServerConfig implements Serializable,Cloneable{
    protected int port;
    protected String webRoot;
    protected String Error404Page;
    protected String Error403Page;
    protected String homepage;

    protected int maxConnections;

    public String getError404Page() {
        return Error404Page;
    }

    public void setError404Page(String error404Page) {
        Error404Page = error404Page.equals("") ? null : error404Page;
    }

    public String getError403Page() {
        return Error403Page;
    }

    public void setError403Page(String error403Page) {
        Error403Page = error403Page.equals("") ? null : error403Page;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage.equals("") ? "/index.html" : homepage;
    }

    public void LoadDefault(){
        port = 8080;
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

    public void setWebRoot(String root){
        this.webRoot = root;
    }
    public int getMaxConnections(){
        return maxConnections;
    }
    public void setMaxConnections(int c){
        if(c < -1) throw new java.lang.IllegalArgumentException("number of connections must be >= -1");
        this.maxConnections = c;
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
