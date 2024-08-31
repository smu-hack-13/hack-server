package com.smuhack13.server.global.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;

@NoArgsConstructor(access = AccessLevel.PRIVATE) //외부에서 인스턴스 생성 불가
public class CorsConfig implements WebMvcConfigurer {

    public static CorsConfigurationSource apiConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        //데이터 교환이 가능한 URL 지정
        ArrayList<String> allowedOriginPatterns = new ArrayList<>();
        allowedOriginPatterns.add("http://localhost:8000");
        allowedOriginPatterns.add("http://localhost:3000");
        allowedOriginPatterns.add("http://3.39.239.252:8080");

        //허용하는 HTTP METHOD 지정
        ArrayList<String> allowedHttpMethods = new ArrayList<>();
        allowedHttpMethods.add("GET");
        allowedHttpMethods.add("POST");
        allowedHttpMethods.add("PUT");
        allowedHttpMethods.add("DELETE");

        configuration.setAllowedOrigins(allowedOriginPatterns); //URL 허용
        configuration.setAllowedMethods(allowedHttpMethods); //HTTP Method 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); //애플리케이션의 모든 경로(/**)에 대해 앞서 설정한 CORS 정책을 적용

        return source;
    }

}
