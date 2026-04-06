package at.jku.app.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name ="tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @Column
    private String title;

    @Column
    private String description;

    @Column
    private LocalDate deadline;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "modified_by_id")
    private User modifiedBy;

    @Column
    private LocalDateTime modifiedAt;

    @Column
    private String lastStepDesc;


}
