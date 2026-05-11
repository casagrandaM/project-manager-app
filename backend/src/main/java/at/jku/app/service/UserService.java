package at.jku.app.service;

import at.jku.app.entity.User;
import at.jku.app.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // =========================
    // GET ALL USERS
    // =========================
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // =========================
    // GET USER BY ID
    // =========================
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    // =========================
    // GET USER BY EMAIL
    // =========================
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    // =========================
    // CREATE USER
    // =========================
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