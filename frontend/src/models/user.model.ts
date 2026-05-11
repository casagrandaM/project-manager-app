import {Project} from './project.model';
import {Task} from './task.model';

export interface User {
  id: number;
  name: string;
  email: string;
  role: Role;
  createdAt?: string;
  projects?: Project[];
  tasks?: Task[];
}

export interface Role {
  id: number;
  name: string;
}
