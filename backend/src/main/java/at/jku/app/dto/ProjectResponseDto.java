package at.jku.app.dto;

/**
 * Response payload describing a project, including creator information and
 * whether the requesting user owns the project.
 */
public class ProjectResponseDto {
    /** The project ID. */
    public Long id;
    /** The project title. */
    public String title;
    /** The project description. */
    public String description;
    /** The creation timestamp as an ISO string. */
    public String createdAt;
    /** The ID of the user who created the project. */
    public Long createdById;
    /** The name of the user who created the project. */
    public String createdByName;
    /** Whether the requesting user is the owner of the project. */
    public boolean isOwner;
}
