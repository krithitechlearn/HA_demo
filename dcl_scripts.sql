-- DCL for Shore Excursions Application Modernization

-- Create roles
CREATE ROLE role_db_admin NOLOGIN;
CREATE ROLE role_editor NOLOGIN;
CREATE ROLE role_user NOLOGIN;
CREATE ROLE role_viewer NOLOGIN;

-- Grant permissions to role_db_admin (Full CRUD on all tables)
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO role_db_admin;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO role_db_admin; -- If sequences were used

-- Grant permissions to role_editor (Create, read, update ports and tours; manage images)
-- Tables: port, tour, port_image, tour_image
GRANT SELECT, INSERT, UPDATE ON port TO role_editor;
GRANT SELECT, INSERT, UPDATE ON tour TO role_editor;
GRANT SELECT, INSERT, UPDATE ON port_image TO role_editor;
GRANT SELECT, INSERT, UPDATE ON tour_image TO role_editor;
-- No DELETE for editor based on LLD

-- Grant permissions to role_user (Read-only on ports and tours)
-- Tables: port, tour
GRANT SELECT ON port TO role_user;
GRANT SELECT ON tour TO role_user;
GRANT SELECT ON port_image TO role_user; -- Users might need to see images
GRANT SELECT ON tour_image TO role_user; -- Users might need to see images

-- Grant permissions to role_viewer (Read-only access to ports and tours)
-- Tables: port, tour
GRANT SELECT ON port TO role_viewer;
GRANT SELECT ON tour TO role_viewer;
GRANT SELECT ON port_image TO role_viewer; -- Viewers might need to see images
GRANT SELECT ON tour_image TO role_viewer; -- Viewers might need to see images

-- Note: Actual users will be created in app_user table and assigned roles via user_role table.
-- Application will manage mapping app_user to PostgreSQL roles if needed, or primarily use application-level RBAC.
-- For direct database access, individual users would be created and granted these roles.
-- Example: CREATE USER app_admin WITH PASSWORD 'secure_password'; GRANT role_db_admin TO app_admin;