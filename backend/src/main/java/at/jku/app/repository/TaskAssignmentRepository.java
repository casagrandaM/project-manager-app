package at.jku.app.repository;

import at.jku.app.entity.TaskAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, Long> {

    List<TaskAssignment> findByTaskId(Long taskId);

    List<TaskAssignment> findByAssigneeId(Long assigneeId);

    List<TaskAssignment> findByTaskProjectId(Long projectId);
}