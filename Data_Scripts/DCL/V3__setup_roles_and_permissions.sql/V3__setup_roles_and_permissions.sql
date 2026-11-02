-- DCL for access control
-- Create Database Roles
CREATE ROLE role_db_admin WITH LOGIN PASSWORD 'CHANGE_ME_ADMIN';
CREATE ROLE role_editor WITH LOGIN PASSWORD 'CHANGE_ME_EDITOR';
CREATE ROLE role_user WITH LOGIN PASSWORD 'CHANGE_ME_USER';
CREATE ROLE role_viewer WITH LOGIN PASSWORD 'CHANGE_ME_VIEWER';
CREATE ROLE role_system_integrator WITH LOGIN PASSWORD 'CHANGE_ME_INTEGRATOR';

-- Grant privileges to role_db_admin
-- Full access to all tables
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO role_db_admin;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO role_db_admin;

-- Grant privileges to role_editor
-- Create, read, update ports and tours; manage images
GRANT SELECT, INSERT, UPDATE ON port TO role_editor;
GRANT SELECT, INSERT, UPDATE ON tour TO role_editor;
GRANT SELECT, INSERT, UPDATE ON port_image TO role_editor;
GRANT SELECT, INSERT, UPDATE ON tour_image TO role_editor;
-- Editor should not manage users or audit logs directly, but can view
GRANT SELECT ON app_user TO role_editor;
GRANT SELECT ON user_role TO role_editor;
GRANT SELECT ON audit_log TO role_editor;

-- Grant privileges to role_user
-- Read-only on ports and tours
GRANT SELECT ON port TO role_user;
GRANT SELECT ON tour TO role_user;
GRANT SELECT ON port_image TO role_user;
GRANT SELECT ON tour_image TO role_user;

-- Grant privileges to role_viewer
-- Read-only access to ports and tours
GRANT SELECT ON port TO role_viewer;
GRANT SELECT ON tour TO role_viewer;
GRANT SELECT ON port_image TO role_viewer;
GRANT SELECT ON tour_image TO role_viewer;

-- Grant privileges to role_system_integrator
-- Read-only access to ports and tours (consuming REST APIs)
GRANT SELECT ON port TO role_system_integrator;
GRANT SELECT ON tour TO role_system_integrator;
GRANT SELECT ON port_image TO role_system_integrator;
GRANT SELECT ON tour_image TO role_system_integrator;

-- Revoke public privileges if desired (best practice for production)
-- REVOKE ALL ON SCHEMA public FROM PUBLIC;
-- GRANT USAGE ON SCHEMA public TO PUBLIC;