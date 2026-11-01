## Migration Notes

1.  **PostgreSQL Version:** The generated schema is designed for PostgreSQL 15+ which natively supports `gen_random_uuid()` for UUID generation. Ensure your PostgreSQL environment meets this version requirement.

2.  **Admin User Seed Data:** The initial `db_admin` user creation is intended to be handled by the application's Flyway migration script (`V2__seed_roles_and_admin.sql`), as described in the LLD. This script will securely create the user by reading credentials from environment variables (`ADMIN_DEFAULT_USERNAME`, `ADMIN_DEFAULT_PASSWORD`) and hashing the password using BCrypt. Therefore, the DML script provided does not contain direct `INSERT` statements for the admin user for security reasons.

3.  **Application-Level Role-Based Access Control (RBAC):** While DCL scripts define PostgreSQL database roles (`role_db_admin`, `role_editor`, `role_user`, `role_viewer`) and grant table-level permissions, the application primarily enforces RBAC at its own layer using Spring Security and the `app_user` and `user_role` tables. The PostgreSQL roles are useful for direct database access by administrators or specific application database users, but the main access control will be managed by the application logic.

4.  **Audit Log Immutability:** The LLD specifies that the `audit_log` table should be immutable (no UPDATE or DELETE operations). This immutability is typically enforced at the application level or through database triggers (e.g., `BEFORE UPDATE` or `BEFORE DELETE` triggers that raise an exception). These specific trigger definitions are not included in the DDL scripts but should be considered for implementation if strict database-level enforcement is required.

5.  **Time Zone Handling:** All timestamp columns use `TIMESTAMP WITH TIME ZONE`, which is best practice for storing time-zone-aware timestamps and ensures consistency across different geographical locations.