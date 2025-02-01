package vitalsanity.controller;

import org.springframework.security.core.AuthenticationException;
import vitalsanity.model.User;
import vitalsanity.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    // Mostrar el formulario de login personalizado
    @GetMapping("/login")
    public String loginPage(@RequestParam(value="error", required=false) String error,
                            @RequestParam(value="logout", required=false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("errorMsg", "Credenciales inválidas. Inténtalo de nuevo.");
        }
        if (logout != null) {
            model.addAttribute("msg", "Has cerrado sesión correctamente.");
        }
        return "login";
    }

    // Procesar el login (este endpoint se usará desde el formulario personalizado)
    @PostMapping("/doLogin")
    public String doLogin(@RequestParam("username") String username,
                          @RequestParam("password") String password,
                          Model model) {
        try {
            UsernamePasswordAuthenticationToken authRequest =
                    new UsernamePasswordAuthenticationToken(username, password);
            Authentication authentication = authenticationManager.authenticate(authRequest);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return "redirect:/upload";
        } catch (AuthenticationException e) {
            model.addAttribute("errorMsg", "Error en autenticación: " + e.getMessage());
            return "login";
        }
    }

    // Mostrar el formulario de registro
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    // Procesar el registro
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, Model model) {
        try {
            userService.registerUser(user);
            model.addAttribute("msg", "Registro exitoso. Por favor, inicia sesión.");
            return "login";
        } catch (Exception e) {
            model.addAttribute("errorMsg", "Error en registro: " + e.getMessage());
            return "register";
        }
    }

    // Cerrar sesión manualmente al hacer clic en "Cerrar sesión"
    @GetMapping("/logout")
    public String logoutUser(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false); // Obtiene la sesión sin crear una nueva
        if (session != null) {
            session.invalidate(); // Invalida la sesión actual
        }
        SecurityContextHolder.clearContext(); // Limpia el contexto de autenticación
        return "redirect:/login?logout";
    }
}
