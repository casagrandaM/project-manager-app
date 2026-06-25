package at.jku.app.repository;

import at.jku.app.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for accessing {@link Status} entities.
 */
public interface StatusRepository extends JpaRepository<Status, Long> {

    /**
     * Finds a status by its display name.
     *
     * @param name The status name (e.g. "To Do", "Done")
     * @return An {@link Optional} containing the matching status, or empty if not found
     */
    Optional<Status> findByName(String name);
}