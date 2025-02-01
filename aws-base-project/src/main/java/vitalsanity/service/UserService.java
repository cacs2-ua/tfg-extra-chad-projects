package vitalsanity.service;

import vitalsanity.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User registerUser(User user);
    User findByEmail(String email);
    User findByUserName(String username);
    User findById(Long id);

}
