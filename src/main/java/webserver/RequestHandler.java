package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Map;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());
        // 1. inputStream으로 http 요청을 읽어 들임 (bufferReader 검색)
        // 2. java files readallbytes로 index.html을 File로 읽어들인 후 byte로 response에 담기
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

            // 요청의 1번째 라인은 Http Request의 Requeset Line이다.
            // Http 메소드, uri, HTTP 버전을 포함한다.
            String line = br.readLine();

            log.debug("request line {}", line);
            String[] tokens = line.split(" ");
            String httpMethod = tokens[0];
            String urlWithQueryParam = tokens[1];
            int contentLength = 0;
            boolean isLogined = false;

            // bufferedReader를 1줄 씩 읽으며, 개행문자가 나오면 
            // 1. Get Method일 때 - 헤더를 다 읽고 끝
            // 2. Post Method일 때 - 헤더를 다 읽고 바디가 남음
            while (!"".equals(line)){
                line = br.readLine();
                if(line == null) break;
                if(line.contains("Content-Length")){
                    contentLength = Integer.parseInt(line.substring(line.indexOf(" ") + 1));
                }
                //log.debug("line {}", line);
            }

            DataOutputStream dos = new DataOutputStream(out);

            // 회원가입
            if(urlWithQueryParam.contains("/user/create") && "POST".equals(httpMethod)){
                String readData = IOUtils.readData(br, contentLength);
                log.info("readData {}", readData);
                Map<String, String> map = HttpRequestUtils.parseQueryString(readData);
                User user = new User(map.get("userId"), map.get("password"), map.get("name"), map.get("email"));
                log.info("user {}", user);
                DataBase.addUser(user);
                response302Header(dos, contentLength, "index.html");
            }

            // 로그인
            if(urlWithQueryParam.contains("/user/login") && "POST".equals(httpMethod)){
                String readData = IOUtils.readData(br, contentLength);
                log.info("readData {}", readData);
                Map<String, String> map = HttpRequestUtils.parseQueryString(readData);
                String userId = map.get("userId");
                User user = DataBase.findUserById(userId);
                if(user != null){
                    // 등록된 사용자가 있다면, 비밀번호 체크
                    String password = map.get("password");
                    if(password.equals(user.getPassword())){
                        log.info("login success");
                        isLogined = true;
                    }else {
                        log.info("login fail");
                    }
                }else {
                    log.info("login fail");
                }
            }

            byte[] body = Files.readAllBytes(Paths.get("./webapp/" + urlWithQueryParam));  //한글 인코딩 문제 가능성!
            //byte[] body = "Hello World".getBytes();
            response200Header(dos, body.length, isLogined);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, boolean isLogined) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Set-Cookie: logined" + isLogined);
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, int lengthOfBodyContent, String location) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: ../" + location + "\r\n");
            //dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private boolean isHtml(String param){
        return param.contains(".html");
    }

    private String getParamString(String requestLine){
        return requestLine.split(" ")[1].split("\\?")[1];
    }
}
