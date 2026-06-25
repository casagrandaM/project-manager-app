package at.jku.app.repository;

import at.jku.app.entity.StatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Repository for accessing and managing {@link StatusHistory} entries.
 */
public interface StatusHistoryRepository extends JpaRepository<StatusHistory, Long> {

    /**
     * Finds all status history entries for a given task.
     *
     * @param taskId The task ID
     * @return The list of status history entries for the task
     */
    List<StatusHistory> findByTaskId(Long taskId);

    /**
     * Finds all status history entries for tasks belonging to a given project.
     *
     * @param projectId The project ID
     * @return The list of status history entries across all tasks in the project
     */
    List<StatusHistory> findByTaskProjectId(Long projectId);

    /**
     * Deletes all status history entries associated with a given task.
     *
     * @param taskId The task ID
     */
    @Modifying
    @Query("DELETE FROM StatusHistory sh WHERE sh.task.id = :taskId")
    void deleteByTaskId(Long taskId);
}