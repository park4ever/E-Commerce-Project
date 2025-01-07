package platform.ecommerce.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import platform.ecommerce.interceptor.CartItemCountInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    private CartItemCountInterceptor cartItemCountInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //정적 리소스 - 홈 화면 이미지 경로
        registry.addResourceHandler("/images/main/**")
                .addResourceLocations("classpath:/static/images/main/");

        //상품 이미지 업로드 경로
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + uploadDir + "/");

        //리뷰 이미지 업로드 경로
        registry.addResourceHandler("/images/review/**")
                .addResourceLocations("file:" + uploadDir + "/review/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(cartItemCountInterceptor)
                .addPathPatterns("/**");
    }
}
