package at.jku.app.dto;

/**
 * Request payload for updating an existing project.
 */
public class ProjectUpdateDto {
    /** The new title of the project. */
    public String title;
    /** The new description of the project. */
    public String description;
}
