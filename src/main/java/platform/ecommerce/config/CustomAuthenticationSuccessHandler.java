package platform.ecommerce.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final RequestCache requestCache = new HttpSessionRequestCache();
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        //SecurityContext에 인증 정보 명확히 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //세션에 SecurityContext 저장
        HttpSession session = request.getSession();
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        //SavedRequest 가져오기
        SavedRequest savedRequest = requestCache.getRequest(request, response);

        if (savedRequest != null) {
            String redirectUrl = savedRequest.getRedirectUrl();

            //로그인 및 회원가입 페이지로 리디렉션 방지
            if (redirectUrl.contains("/login") || redirectUrl.contains("signup")) {
                redirectStrategy.sendRedirect(request, response, "/home");
                log.info("Redirecting to home to prevent redirect loop.");
            } else {
                redirectStrategy.sendRedirect(request, response, redirectUrl);
                log.info("Redirecting to originally requested URL : {}", redirectUrl);
            }

            //SavedRequest 삭제
            requestCache.removeRequest(request, response);
        } else {
            //SavedRequest가 없으면 기본 페이지로 Redirection
            redirectStrategy.sendRedirect(request, response, "/home");
            log.info("No SavedRequest found. Redirecting to home");
        }

        log.info("Authentication success for user : {}", authentication.getName());
    }
}
