package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cfy on 15-12-14.
 */
public class HttpRequest {

    private String requestContent = null;

    private String method = null;
    private String requectedURL = null;
    private String version = null;
    private Map<String,String> params = null;

    public HttpRequest(){
        requestContent = "";
        params = new HashMap<>();
    }
    public HttpRequest(BufferedReader reader) throws IOException{
        this();
        String s;
        while(!(s = reader.readLine()).isEmpty()){
            parse(s);
            requestContent += s;
        }
    }
    public String getMethod(){
        return method;
    }

    public String getRequectedURL(){
        return requectedURL;
    }
    private void parse(String line){
        if(method == null && line.startsWith("GET")){
            String[] d = line.split(" ");
            method = d[0];
            requectedURL = d[1];
            version = d[2];

            if(requectedURL.contains("?")){
                String[] a = requectedURL.split("\\?");
                requectedURL = a[0];
            }
        }
        else if(method == null && line.startsWith("POST")){
            String[] d = line.split(" ");
            method = d[0];
            requectedURL = d[1];
            version = d[2];

            if(requectedURL.contains("?")){
                String[] a = requectedURL.split("\\?");
                requectedURL = a[0];
            }
        }
    }

    public String getRequestContent(){
        return requestContent;
    }
}
