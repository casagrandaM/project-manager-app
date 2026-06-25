/**
 * Represents a single entry in a project's activity feed.
 */
export interface ActivityEvent {
  /** The kind of event that occurred. */
  type: 'TASK_CREATED' | 'STATUS_CHANGED' | 'TASK_ASSIGNED';
  /** ISO timestamp of when the event occurred. */
  timestamp: string;
  /** Name of the user who triggered the event. */
  userName: string;
  /** Title of the task the event refers to. */
  taskTitle: string;
  /** Human-readable description of the event. */
  detail: string;
}
