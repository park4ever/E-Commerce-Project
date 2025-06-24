package platform.ecommerce.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import platform.ecommerce.config.auth.LoginMemberArgumentResolver;
import platform.ecommerce.config.interceptor.LoginCheckInterceptor;
import platform.ecommerce.interceptor.CartItemCountInterceptor;

import java.util.List;

@Configuration
@EnableConfigurationProperties(FileUploadProperties.class)
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final CartItemCountInterceptor cartItemCountInterceptor;
    private final FileUploadProperties fileUploadProperties;
    private final LoginMemberArgumentResolver loginMemberArgumentResolver;
    private final LoginCheckInterceptor loginCheckInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + fileUploadProperties.getPath());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(cartItemCountInterceptor)
                .addPathPatterns("/**");

        registry.addInterceptor(loginCheckInterceptor)
                .addPathPatterns("/cart/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginMemberArgumentResolver);
    }
}
