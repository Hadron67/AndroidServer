package Server;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by cfy on 15-12-13.
 *
 */
public class Session {
    private Socket client;
    private WebServer server;

    private boolean isRunning = false;

    DateFormat df = new SimpleDateFormat("yyyy-MM-dd   HH:mm:ss");

    public Session(WebServer server,Socket client){
        this.client = client;
        this.server = server;
    }


    private void run() {

        PrintStream os = null;
        try {
            String ip = client.getInetAddress().toString();
            int destport = client.getPort();
            server.sendmsg("connect to : " + ip + " : " + destport);
            os = new PrintStream(client.getOutputStream());
            DataInputStream is = new DataInputStream(client.getInputStream());

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String inline = reader.readLine();
            server.sendmsg("Received : " + inline);

            if(isMethod(inline,"GET")){
                doGET(inline,os);
                os.flush();
            }

            String s;
            while(!(s = reader.readLine()).isEmpty()){
                server.sendmsg("Received from client : " + '"'  + s + '"');
            }
            client.close();
            return;
        }
        catch(IOException e){
            e.printStackTrace();
        }
        catch (NullPointerException e){
            server.sendmsg("Error occurs !");
            server.sendmsg(e.toString());
        }
        if(os != null){
            os.flush();
            os.close();
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void start(){
        isRunning = true;
        server.callOnSessionStart(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Session.this.run();
                server.callOnSessionStop(Session.this);
            }
        }).start();
    }

    private static boolean isMethod(String s,String method){
        if(s == null) return false;
        if(s.length() > 0){
            if(s.substring(0,3).equalsIgnoreCase(method)) return true;
        }
        return false;
    }

    private void doGET(String request,PrintStream os){
        String[] s = request.split(" ");
        String type = s[0];
        String filename = s[1];
        server.sendmsg("filename : " + server.root + s[1]);
        File requested = new File(server.root + filename);

        if(filename.equals("/")){
            os.println("HTTP/1.1 301 Moved Permanently");
            os.println("Location:http:/index.html");
        }
        else if(requested.exists() && requested.isFile()){
            os.println("HTTP/1.1 200 OK");
            sendFile(requested,os);
        }
        else if(requested.isDirectory()){
            os.println("HTTP/1.1 200 OK");
            File[] files = requested.listFiles();
            for(int i = 0;i < files.length;i++){
                if(files[i].isFile()){
                    sendFile(files[i],os);
                }
            }
        }
        else{
            String response = "<html><head><title>404</title></head><body><h1>404</h1><p>File Not Found.</p></body></html>";
            os.println("HTTP/1.1 404 no found");
            os.println("Content_Type:text/html");
            os.println("Content_Length:" + response.length() + 2);
            os.println();
            os.println(response);
        }
    }

    private void sendFile(File file,PrintStream os){
        String filename = file.getName();
        String content_type = "text/html";
        if(filename.endsWith(".js")){
            content_type = "text/javascript";
        }
        else if(filename.endsWith(".jpg")){
            content_type = "image/jpeg";
        }
        try {
            DataInputStream is = new DataInputStream(new FileInputStream(file));
            int len = (int) file.length();
            byte[] buf = new byte[len];
            is.readFully(buf);
            os.println("Content_Type:" + content_type);
            os.println("Content_Length:" + len);
            os.println("");
            os.write(buf);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public String getIP(){
        return client.getInetAddress().toString() + ":" + client.getPort();
    }

}
