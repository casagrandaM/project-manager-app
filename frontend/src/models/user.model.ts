import { Project } from './project.model';
import { Task } from './task.model';

/**
 * Represents a user in the system.
 *
 * <p>Contains user identity information along with related role, projects, and tasks.</p>
 */
export interface User {
  id: number;
  name: string;
  email: string;
  role: Role;
  createdAt?: string;
  projects?: Project[];
  tasks?: Task[];
}

/**
 * Represents a user role.
 */
export interface Role {
  id: number;
  name: string;
}
