package http;

import enums.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> params = new HashMap<>();
    private RequestLine requestLine;

    public HttpRequest(InputStream in){
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line = br.readLine(); // 첫번째 라인은 Request Line이 된다.

            if(line == null) return;

            requestLine = new RequestLine(line);

            line = br.readLine();
            while (!"".equals(line)){
                String[] tokens = line.split(":");
                headers.put(tokens[0].trim(), tokens[1].trim());
                line = br.readLine();
            }

            if(getMethod().isPost()){
                String body = IOUtils.readData(br, Integer.parseInt(headers.get("Content-Length")));
                params = HttpRequestUtils.parseQueryString(body);
            }else {
                params = requestLine.getParameters();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getHeaders(String name) {
        return headers.get(name);
    }

    public String getParams(String name) {
        return params.get(name);
    }
}
