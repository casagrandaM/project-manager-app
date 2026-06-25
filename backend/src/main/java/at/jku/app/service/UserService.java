package at.jku.app.service;

import at.jku.app.entity.User;
import at.jku.app.repository.UserRepository;
import at.jku.app.security.data.AppUserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for managing {@link User} entities.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
	
	public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
	}

    /**
     * Retrieves all users.
     *
     * @return The list of all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Retrieves the currently authenticated user.
     *
     * @return The authenticated user
     *
     * @throws IllegalStateException If no authenticated user exists or the user cannot be found
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !(authentication.getPrincipal() instanceof AppUserPrincipal principal)) {
            throw new IllegalStateException("No authenticated user");
        }
        
        return userRepository.findById(principal.getUser().getId())
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }

    /**
     * Retrieves a user by its ID.
     *
     * @param id The user ID
     * @return The matching user
     *
     * @throws RuntimeException If no user with the given ID exists
     */
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    /**
     * Retrieves a user by their email address.
     *
     * @param email The user email address
     * @return The matching user
     *
     * @throws RuntimeException If no user with the given email address exists
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    /**
     * Creates and persists a new user.
     *
     * @param user The user to create
     * @return The created user
     */
    public User createUser(User user) {
        return userRepository.save(user);
    }

    /**
     * Updates the name and email address of a user.
     *
     * @param id    The user ID
     * @param name  The username
     * @param email The user email address
     */
    public void updateUser(Long id, String name, String email) {
        User user = getById(id);
        user.setName(name);
        user.setEmail(email);
        userRepository.save(user);
    }
}