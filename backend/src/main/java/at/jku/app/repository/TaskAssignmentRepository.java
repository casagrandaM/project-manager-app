package at.jku.app.repository;

import at.jku.app.entity.TaskAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for accessing {@link TaskAssignment} entities.
 */
public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, Long> {

    /**
     * Finds all assignments for a given task.
     *
     * @param taskId The task ID
     * @return The list of task assignments
     */
    List<TaskAssignment> findByTaskId(Long taskId);

    /**
     * Finds all assignments where a specific user is the assignee.
     *
     * @param assigneeId The assignee's user ID
     * @return The list of task assignments for the user
     */
    List<TaskAssignment> findByAssigneeId(Long assigneeId);

    /**
     * Finds all task assignments for tasks belonging to a given project.
     *
     * @param projectId The project ID
     * @return The list of task assignments across all tasks in the project
     */
    List<TaskAssignment> findByTaskProjectId(Long projectId);
}