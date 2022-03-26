package util;

import model.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.WebServer;

import java.util.Map;

public class UserCreateTests {
    private static final Logger log = LoggerFactory.getLogger(UserCreateTests.class);

    @Test
    public void parseGetQueryString() throws Exception{
        String requestLine = "GET /user/create?userId=111&password=222&name=333&email=1234@google.com HTTP/1.1";
        Map<String, String> map = HttpRequestUtils.parseQueryString(getParamString(requestLine));
        log.info("map ==> {}", map);
        User user = new User(map.get("userId"), map.get("password"), map.get("name"), map.get("email"));
        log.info("user ==> {}", user);
    }

    private String getParamString(String requestLine){
        return requestLine.split(" ")[1].split("\\?")[1];
    }
}
