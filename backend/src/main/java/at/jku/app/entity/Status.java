package at.jku.app.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entity representing a task status (e.g. "To Do", "In Progress", "Done").
 */
@Entity
@Data
@Table(name = "statuses")
public class Status {

    /** The unique status ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The display name of the status */
    @Column
    private String name;

    /** A numeric code used for ordering or identification */
    @Column
    private Long code;
}