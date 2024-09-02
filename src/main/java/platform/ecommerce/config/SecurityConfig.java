package platform.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import platform.ecommerce.controller.CustomLogoutSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomLogoutSuccessHandler customLogoutSuccessHandler;

    public SecurityConfig(CustomLogoutSuccessHandler customLogoutSuccessHandler) {
        this.customLogoutSuccessHandler = customLogoutSuccessHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*@Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        //정적 리소스가 보안 필터를 거치지 않기 위함.
        return (web) -> web.ignoring().requestMatchers("/assets/**", "/vendor/**");
    }*/

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/logout")
                )
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/", "/signup", "/login").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasRole("USER")
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .defaultSuccessUrl("/")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }
}
