package at.jku.app.service;

import at.jku.app.entity.Status;
import at.jku.app.repository.StatusRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for retrieving task statuses.
 */
@Service
public class StatusService {

    private final StatusRepository statusRepository;

    public StatusService(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    /**
     * Retrieves all available statuses.
     *
     * @return The list of all statuses
     */
    public List<Status> getAllStatuses() {
        return statusRepository.findAll();
    }

    /**
     * Retrieves a status by its ID.
     *
     * @param id The status ID
     * @return The matching {@link Status}
     * @throws RuntimeException if no status with the given ID exists
     */
    public Status getById(Long id) {
        return statusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Status not found"));
    }

    /**
     * Retrieves a status by its display name.
     *
     * @param name The status name (e.g. "To Do")
     * @return The matching {@link Status}
     * @throws RuntimeException if no status with the given name exists
     */
    public Status getByName(String name) {
        return statusRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Status not found"));
    }
}