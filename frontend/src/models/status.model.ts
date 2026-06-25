/**
 * Represents a task status.
 *
 * <p>Defines the workflow state of a task, including its unique identifier,
 * display name, and numeric status code.</p>
 */
export interface Status {
  id: number;
  name: string;
  code: number;
}
