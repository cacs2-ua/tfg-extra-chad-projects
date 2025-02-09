package vitalsanity.config;

import vitalsanity.interceptor.AuthInterceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration to register interceptors.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Apply AdminInterceptor to /registrados and /registrados/**
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/upload/**");
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/download/**");
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/signer/**");
    }
}


