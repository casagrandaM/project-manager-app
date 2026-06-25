package at.jku.app.repository;

import at.jku.app.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for accessing {@link Task} entities.
 */
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Finds all tasks belonging to a given project.
     *
     * @param projectId The project ID
     * @return The list of tasks in the project
     */
    List<Task> findByProjectId(Long projectId);
}