/**
 * Represents a project as returned by the backend.
 */
export interface Project {
  id: number;
  title: string;
  description?: string;
  createdAt?: string;
  /** ID of the user who created the project. */
  createdById?: number;
  /** Name of the user who created the project. */
  createdByName?: string;
  /** Whether the current user is the owner of the project. */
  isOwner?: boolean;
}

/**
 * Payload for creating a new project.
 */
export interface CreateProject {
  title: string;
  description?: string;
}

/**
 * Payload for updating an existing project.
 */
export interface UpdateProject {
  title: string;
  description?: string;
}
