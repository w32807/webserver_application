package http;

import enums.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

import java.util.HashMap;
import java.util.Map;

import static enums.HttpMethod.*;

public class RequestLine {
    private HttpMethod method;
    // 반환값이 여러개인 메소드가 있을 때는 클래스를 만들어 리팩토링
    private static final Logger log = LoggerFactory.getLogger(RequestLine.class);

    private String path;
    private Map<String, String> params = new HashMap<>();

    public RequestLine(String requestLine){
        String[] tokens = requestLine.split(" ");
        if(tokens.length != 3){
            throw new IllegalArgumentException(requestLine + "이 형식에 맞지 않습니다.");
        }
        method = HttpMethod.valueOf(tokens[0]);
        if(method.isPost()){
            path = tokens[1];
            return;
        }

        int index = tokens[1].indexOf("?");
        if(index == -1){
            path = tokens[1];
        }else {
            path = tokens[1].substring(0, index);
            params = HttpRequestUtils.parseQueryString(tokens[1].substring(index + 1));
        }
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getParams() {
        return params;
    }
}
