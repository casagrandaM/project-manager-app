package at.jku.app.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * JPA entity representing a project. A project groups tasks and members and is
 * owned by the user who created it.
 */
@Entity
@Data
@Table(name = "projects")
public class Project {
    /** Unique, auto-generated identifier. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The project title. */
    @Column
    private String title;

    /** The project description. */
    @Column
    private String description;

    /** Timestamp of when the project was created. */
    @Column
    private LocalDateTime createdAt;

    /** The user who created and owns the project. */
    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private User createdBy;

}
