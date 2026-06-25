package at.jku.app.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entity representing a user role.
 */
@Entity
@Data
@Table(name="roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long code;

    private String name;
}
