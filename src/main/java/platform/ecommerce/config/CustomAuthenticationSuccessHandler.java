package platform.ecommerce.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        SavedRequest savedRequest = (SavedRequest) request.getSession()
                .getAttribute("SPRING_SECURITY_SAVED_REQUEST");

        if (savedRequest != null) {
            //인증 전에 요청한 경로로 redirect
            response.sendRedirect(savedRequest.getRedirectUrl());
            System.out.println("Authentication success for user: " + authentication.getName());

        } else {
            //기본적으로 홈 화면으로 redirect
            response.sendRedirect("/");
            System.out.println("Authentication success for user: " + authentication.getName());

        }
    }
}
