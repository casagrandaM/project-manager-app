package at.jku.app.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="project_members")
public class ProjectMember {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name="project_id")
    private Project project;

    @Column
    private boolean isProjectManager;

}
