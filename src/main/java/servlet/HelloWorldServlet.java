package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

// 서블릿을 상속받아 사용자 서블릿을 만든다.
// 서블릿 컨테이너(톰캣)은 서버를 시작할 때 클래스 패스에 있는 클래스 중
// HttpServlet을 상속하는 클래스를 찾아 요청 URL과 서블릿 클래스를 연결하는 Map을 생성한다.
// 서블릿 컨테이너(톰캣)은 서블릿의 라이프사이클을 관리한다.

@WebServlet("/hello")
public class HelloWorldServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        out.print("Hello World");
    }
}
