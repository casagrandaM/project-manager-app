package at.jku.app.service;

import at.jku.app.entity.Status;
import at.jku.app.entity.StatusHistory;
import at.jku.app.entity.Task;
import at.jku.app.entity.User;
import at.jku.app.repository.StatusHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * Service for managing status history entries, including creating
 * new status transitions and querying the latest status of a task.
 */
@Service
public class StatusHistoryService {

    private final StatusHistoryRepository statusHistoryRepository;

    public StatusHistoryService(StatusHistoryRepository statusHistoryRepository) {
        this.statusHistoryRepository = statusHistoryRepository;
    }

    /**
     * Retrieves all status history entries for a given task.
     *
     * @param taskId The task ID
     * @return The list of status history entries
     */
    public List<StatusHistory> getByTaskId(Long taskId) {
        return statusHistoryRepository.findByTaskId(taskId);
    }

    /**
     * Persists a status history entry directly.
     *
     * @param history The status history entry to save
     * @return The saved status history entry
     */
    public StatusHistory create(StatusHistory history) {
        return statusHistoryRepository.save(history);
    }

    /**
     * Records a status change for a task, creating a new history entry
     * with the current timestamp.
     *
     * @param task   The task whose status is being changed
     * @param status The new status
     * @param user   The user performing the change
     * @return The created status history entry
     */
    public StatusHistory changeStatus(Task task, Status status, User user) {
        StatusHistory history = new StatusHistory();
        history.setTask(task);
        history.setStatus(status);
        history.setCreatedBy(user);
        history.setCreatedAt(LocalDateTime.now());

        return statusHistoryRepository.save(history);
    }

    /**
     * Determines the latest status of a task by finding the most recent
     * history entry.
     *
     * @param taskId The task ID
     * @return The latest {@link Status}, or {@code null} if no history exists
     */
    public Status getLatestStatus(Long taskId) {
        return statusHistoryRepository.findByTaskId(taskId)
                .stream()
                .max(Comparator.comparing(StatusHistory::getCreatedAt))
                .map(StatusHistory::getStatus)
                .orElse(null);
    }

    /**
     * Deletes all status history entries for a given task.
     *
     * @param taskId The task ID
     */
    @Transactional
    public void deleteByTaskId(Long taskId) {
        statusHistoryRepository.deleteByTaskId(taskId);
    }
}