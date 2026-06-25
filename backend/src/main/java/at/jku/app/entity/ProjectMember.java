package at.jku.app.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * JPA entity representing the membership of a user in a project, including
 * whether that user holds project manager privileges.
 */
@Entity
@Data
@Table(name="project_members")
public class ProjectMember {
    /** Unique, auto-generated identifier. */
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    /** The member user. */
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    /** The project the user is a member of. */
    @ManyToOne
    @JoinColumn(name="project_id")
    private Project project;

    /** Whether the member has project manager privileges. */
    @Column
    private boolean isProjectManager;

}
