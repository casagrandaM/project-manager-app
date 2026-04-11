package at.jku.app.repository;

import at.jku.app.entity.StatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StatusHistoryRepository extends JpaRepository<StatusHistory, Long> {

    List<StatusHistory> findByTaskId(Long taskId);

    List<StatusHistory> findByTaskProjectId(Long projectId);

    @Modifying
    @Query("DELETE FROM StatusHistory sh WHERE sh.task.id = :taskId")
    void deleteByTaskId(Long taskId);
}