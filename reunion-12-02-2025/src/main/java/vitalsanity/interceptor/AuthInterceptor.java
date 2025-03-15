package vitalsanity.interceptor;

import vitalsanity.authentication.ManagerUserSession;
import vitalsanity.controller.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private ManagerUserSession managerUserSession;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Long userId = managerUserSession.usuarioLogeado();

        if (userId == null) {

            throw new NotFoundException("Página no encontrada.");
        }

        return true;
    }
}


