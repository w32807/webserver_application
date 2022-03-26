package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

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
            if(line == null){
                return;
            }

            log.debug("request line {}", line);
            String[] tokens = line.split(" ");
            String urlWithQueryParam = tokens[1];
            // 회원가입

            while (!"".equals(line)){
                line = br.readLine();
                if(line == null) break;
                //log.debug("line {}", line);
            }

            if(urlWithQueryParam.contains("/user/create")){
                String readData = IOUtils.readData(br, 66);
                log.info("readData {}", readData);
                Map<String, String> map = HttpRequestUtils.parseQueryString(readData);
                User user = new User(map.get("userId"), map.get("password"), map.get("name"), map.get("email"));
                log.info("user {}", user);
            }

            byte[] body = Files.readAllBytes(Paths.get("./webapp/" + urlWithQueryParam));  //한글 인코딩 문제 가능성!
            DataOutputStream dos = new DataOutputStream(out);
            //byte[] body = "Hello World".getBytes();
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
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
