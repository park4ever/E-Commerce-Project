package platform.ecommerce.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import platform.ecommerce.service.GlobalService;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        // 이메일과 비밀번호 입력 값 추출
        String email = request.getParameter("email");
        boolean passwordError = exception instanceof BadCredentialsException; // 비밀번호가 틀리면 오류

        // 이메일 존재 여부는 컨트롤러에서 처리하도록 위임
        request.getSession().setAttribute("emailError", null); // 초기값으로 설정
        request.getSession().setAttribute("passwordError", passwordError ? "비밀번호가 맞지 않습니다." : null);

        // 에러가 있을 경우 로그인 페이지로 리디렉션하며 에러 메시지 전달
        getRedirectStrategy().sendRedirect(request, response, "/login?error=true");
    }
}