package at.jku.app.repository;

import at.jku.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for accessing and managing {@link User} entities.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a {@link User} by their email address.
     *
     * @param email The user email address
     * @return The matching {@link User}, if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks whether a user with the given email address exists.
     *
     * @param email The user email address
     * @return {@code true} if a matching user exists
     */
    boolean existsByEmail(String email);
}
