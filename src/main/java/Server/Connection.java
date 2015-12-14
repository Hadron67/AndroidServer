package Server;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
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
public class Connection {
    private Socket client;
    private WebServer server;

    private boolean isRunning = false;

    DateFormat df = new SimpleDateFormat("yyyy-MM-dd   HH:mm:ss");

    public Connection(WebServer server, Socket client){
        this.client = client;
        this.server = server;
    }


    private void run() throws IOException{

        PrintStream os = null;
        try {
            String ip = client.getInetAddress().toString();
            int destport = client.getPort();
            server.sendmsg("connect to : " + ip + " : " + destport,0);
            os = new PrintStream(client.getOutputStream());
            DataInputStream is = new DataInputStream(client.getInputStream());

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            HttpRequest request = new HttpRequest(reader);

            server.sendmsg(request.getRequestContent(),0);

            if(request.getMethod().equals("GET")){
                doGET(request).send(os);
                os.flush();
            }
            client.close();
            return;
        }
        catch (NullPointerException e){
            server.sendmsg("Error occurs !",0);
            server.sendmsg(e.toString(),0);
            e.printStackTrace();
        }
        if(os != null){
            os.flush();
            os.close();
            client.close();
        }
    }

    public void start(){
        isRunning = true;
        server.callOnSessionStart(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection.this.run();
                } catch (IOException e) {
                    e.printStackTrace();
                    server.sendmsg(e.toString(),0);
                }
                server.callOnSessionStop(Connection.this);
            }
        }).start();
    }

    private HttpResponse doGET(HttpRequest request) throws IOException {
        HttpResponse response = new HttpResponse();
        String filename = request.getRequectedURL();

        if(filename.equals("/")){
            response.setResponseCode(HttpResponse.RESCODE_OK);
            filename = server.config.homepage;
        }

        File file = new File(server.config.webRoot + filename);
        if(file.exists() && file.isFile()){
            response.setResponseCode(HttpResponse.RESCODE_OK);
            response.setFileContent(file);
        }
        else if(file.exists() && file.isDirectory()){
            set403Content(response);
        }
        else{
            set404Content(response);
        }

        return response;
    }

    public void disConnect() throws IOException {
        client.close();
    }


    public String getIP(){
        return client.getInetAddress().toString();
    }
    public int getPort(){
        return client.getPort();
    }

    private void set404Content(HttpResponse response) throws IOException {
        if(server.config.Error404Page != null){
            response.setFileContent(new File(server.config.Error404Page));
        }
        else{
            response.setStringContent(HttpResponse.fileNotFoundResponse);
        }
    }

    private void set403Content(HttpResponse response) throws IOException {
        if(server.config.Error403Page != null){
            response.setFileContent(new File(server.config.Error403Page));
        }
        else{
            response.setStringContent(HttpResponse.forbiddenResponse);
        }
    }
}
