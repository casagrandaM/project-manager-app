package at.jku.app.service;

import at.jku.app.entity.User;
import at.jku.app.repository.UserRepository;
import at.jku.app.security.data.AppUserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !(authentication.getPrincipal() instanceof AppUserPrincipal principal)) {
            throw new IllegalStateException("No authenticated user");
        }
        
        return userRepository.findById(principal.getUser().getId())
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }
    
    public void updateUser(Long id, String name, String email) {
        User user = getById(id);
        user.setName(name);
        user.setEmail(email);
        userRepository.save(user);
    }
}