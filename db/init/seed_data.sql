BEGIN;

-- ========================================
-- ROLES
-- ========================================
INSERT INTO roles (code, name) VALUES
                                   (1, 'ADMIN'),
                                   (2, 'USER')
    ON CONFLICT DO NOTHING;

-- ========================================
-- USERS
-- ========================================
INSERT INTO users (name, email, role_id, created_at, auth_data)
VALUES
    ('Admin User', 'admin@task.com',
     (SELECT id FROM roles WHERE code = 1),
     NOW(),
     NULL),

    ('Test User', 'user@task.com',
     (SELECT id FROM roles WHERE code = 2),
     NOW(),
     NULL)
    ON CONFLICT (email) DO NOTHING;

-- ========================================
-- PROJECTS
-- ========================================
INSERT INTO projects (title, description, created_at, created_by_id)
VALUES
    ('Demo Project', 'Projekt für Testzwecke',
     NOW(),
     (SELECT id FROM users WHERE email = 'admin@task.com'));

-- ========================================
-- PROJECT MEMBERS
-- ========================================
INSERT INTO project_members (user_id, project_id, is_project_manager)
VALUES
    (
        (SELECT id FROM users WHERE email = 'admin@task.com'),
        (SELECT id FROM projects WHERE title = 'Demo Project'),
        TRUE
    ),
    (
        (SELECT id FROM users WHERE email = 'user@task.com'),
        (SELECT id FROM projects WHERE title = 'Demo Project'),
        FALSE
    )
    ON CONFLICT DO NOTHING;

-- ========================================
-- STATUSES
-- ========================================
INSERT INTO statuses (name, code) VALUES
                                      ('To Do', 1),
                                      ('In Progress', 2),
                                      ('Done', 3)
    ON CONFLICT DO NOTHING;

-- ========================================
-- TASKS
-- ========================================
INSERT INTO tasks (
    project_id,
    title,
    description,
    deadline,
    created_by,
    created_at,
    last_step_desc
)
VALUES
    (
        (SELECT id FROM projects WHERE title = 'Demo Project'),
        'Setup Backend',
        'Spring Boot Projekt initialisieren',
        CURRENT_DATE + INTERVAL '7 days',
        (SELECT id FROM users WHERE email = 'admin@task.com'),
        NOW(),
        'Initial created'
    ),
    (
        (SELECT id FROM projects WHERE title = 'Demo Project'),
        'Setup Frontend',
        'Angular Projekt erstellen',
        CURRENT_DATE + INTERVAL '10 days',
        (SELECT id FROM users WHERE email = 'admin@task.com'),
        NOW(),
        'Initial created'
    );

-- ========================================
-- TASK ASSIGNMENTS
-- ========================================
INSERT INTO task_assignments (task_id, assignee_id, created_by_id, created_at)
VALUES
    (
        (SELECT id FROM tasks WHERE title = 'Setup Backend'),
        (SELECT id FROM users WHERE email = 'user@task.com'),
        (SELECT id FROM users WHERE email = 'admin@task.com'),
        NOW()
    ),
    (
        (SELECT id FROM tasks WHERE title = 'Setup Frontend'),
        (SELECT id FROM users WHERE email = 'user@task.com'),
        (SELECT id FROM users WHERE email = 'admin@task.com'),
        NOW()
    )
    ON CONFLICT DO NOTHING;

-- ========================================
-- STATUS HISTORY
-- ========================================
INSERT INTO status_histories (task_id, status_id, user_id, created_at)
VALUES
    (
        (SELECT id FROM tasks WHERE title = 'Setup Backend'),
        (SELECT id FROM statuses WHERE code = 1),
        (SELECT id FROM users WHERE email = 'admin@task.com'),
        NOW()
    ),
    (
        (SELECT id FROM tasks WHERE title = 'Setup Frontend'),
        (SELECT id FROM statuses WHERE code = 1),
        (SELECT id FROM users WHERE email = 'admin@task.com'),
        NOW()
    );

COMMIT;