DO $$
    BEGIN
        DROP TABLE IF EXISTS
            status_histories,
            task_assignments,
            tasks,
            project_members,
            projects,
            statuses,
            users,
            roles
            CASCADE;
    END $$;

CREATE TABLE roles (
                       id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                       code        BIGINT,
                       name        VARCHAR(200) NOT NULL
);

CREATE TABLE users (
                       id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                       name         VARCHAR(200),
                       email        VARCHAR(254) NOT NULL UNIQUE,
                       role_id      BIGINT REFERENCES roles(id),
                       created_at   timestamptz NOT NULL,
                       auth_data    TEXT
);

CREATE TABLE projects (
                          id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                          title           VARCHAR(300) NOT NULL,
                          description     VARCHAR(1000),
                          created_at      timestamptz NOT NULL,
                          created_by_id   BIGINT REFERENCES users(id)
);

CREATE TABLE project_members (
                                 id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                 user_id             BIGINT NOT NULL REFERENCES users(id),
                                 project_id          BIGINT NOT NULL REFERENCES projects(id),
                                 is_project_manager  BOOLEAN NOT NULL DEFAULT false,
                                 CONSTRAINT uq_project_member UNIQUE (user_id, project_id)
);

CREATE TABLE tasks (
                       id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                       project_id       BIGINT NOT NULL REFERENCES projects(id),
                       title            VARCHAR(300) NOT NULL,
                       description      VARCHAR(1000),
                       deadline         DATE,
                       created_by       BIGINT REFERENCES users(id),
                       created_at       timestamptz NOT NULL,
                       modified_by_id   BIGINT REFERENCES users(id),
                       modified_at      timestamptz,
                       last_step_desc   VARCHAR(500)
);

CREATE TABLE task_assignments (
                                  id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                  task_id       BIGINT NOT NULL REFERENCES tasks(id),
                                  assignee_id   BIGINT NOT NULL REFERENCES users(id),
                                  created_by_id BIGINT REFERENCES users(id),
                                  created_at    timestamptz NOT NULL,
                                  CONSTRAINT uq_task_assignment UNIQUE (task_id, assignee_id)
);

CREATE TABLE statuses (
                          id     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                          name   VARCHAR(100) NOT NULL,
                          code   BIGINT
);

CREATE TABLE status_histories (
                                  id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                  task_id     BIGINT NOT NULL REFERENCES tasks(id),
                                  status_id   BIGINT NOT NULL REFERENCES statuses(id),
                                  user_id     BIGINT REFERENCES users(id),
                                  created_at  timestamptz NOT NULL
);

COMMIT;