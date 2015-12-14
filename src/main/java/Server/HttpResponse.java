package Server;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Created by cfy on 15-12-14.
 *
 */
public class HttpResponse {

    public static final String fileNotFoundResponse = "<html><head><title>404</title></head><body><h1>404</h1><p>File Not Found.</p></body></html>";
    public static final String forbiddenResponse = "<html><head><title>403</title></head><body><h1>403</h1><p>Access Denied.</p></body></html>";
    public static final int RESCODE_OK = 200;
    public static final int RESCODE_NOT_FOUND = 404;
    public static final int RESCODE_MOVED_PERMANENTLY = 301;
    public static final int RESCODE_FORBIDDEN = 403;


    private String server_type = null;
    private int responseCode;
    private String content_type = null;
    private String accept_ranges = null;
    private String content_location = null;
    private byte[] content;

    public HttpResponse() {
        server_type = "Java - Android 4.4";
        accept_ranges = "bytes";
        content_location = "/index.html";
        content = new byte[0];
    }
    private String getResponseString(int code){
        switch (code){
            case RESCODE_OK:return "OK";
            case RESCODE_NOT_FOUND:return "no found";
            case RESCODE_MOVED_PERMANENTLY:return "Moved Permanently";
            case RESCODE_FORBIDDEN:return "Forbidden";
        }
        return null;
    }
    public void setResponseCode(int code){
        responseCode = code;
    }
    public void send(PrintStream os) throws IOException{
        os.println("HTTP/1.1 " + responseCode + " " + getResponseString(responseCode));
        os.println("Server:" + server_type);
        os.println("Content-Location:" + content_location);
        os.println("Accept-Ranges:" + accept_ranges);
        os.println("Content_Type:" + content_type);
        os.println("Content_Length:" + content.length);
        os.println("");
        os.write(content);
    }

    public void setFileContent(File file) throws IOException{
        if(file.exists() && file.isFile()) {
            content_type = getFileContentType(file.getName());

            DataInputStream is = new DataInputStream(new FileInputStream(file));
            int len = (int) file.length();
            content = new byte[len];
            is.readFully(content);
        }
    }
    public void setStringContent(String s) throws IOException{
        content_type = "text/html";
        content = s.getBytes();
    }

    private String getFileContentType(String filename){
        String content_type;
        if(filename.endsWith(".html")){
            content_type = "text/html";
        }
        else if(filename.endsWith(".js")){
            content_type = "text/javascript";
        }
        else if(filename.endsWith(".jpg")){
            content_type = "image/jpeg";
        }
        else{
            content_type = "application/octet-stream";
        }
        return content_type;
    }
}
