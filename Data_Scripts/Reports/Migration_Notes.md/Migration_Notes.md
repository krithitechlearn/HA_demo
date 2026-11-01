Migration Notes for Shore Excursions Application Database:

1.  **Initial Admin Password:** The `password_hash` for the `db_admin` user in the DML script (`Data_Scripts/DML/V2__seed_admin_user.sql`) is a placeholder (`$2a$12$EXAMPLE_BCRYPT_HASH_FOR_ADMIN_PASSWORD`). **It must be replaced with a securely generated BCrypt hash of the actual desired password** before deploying to any environment, especially production.
2.  **Database Role Passwords:** The passwords for the created database roles (`role_db_admin`, `role_editor`, `role_user`, `role_viewer`, `role_system_integrator`) in the DCL script (`Data_Scripts/DCL/V3__setup_roles_and_permissions.sql`) are placeholders (`CHANGE_ME_...`). **These should be replaced with strong, unique passwords** and managed securely, ideally via a secrets management system, before deployment.
3.  **`uuid-ossp` Extension:** The DDL script (`Data_Scripts/DDL/V1__init_schema.sql`) includes `CREATE EXTENSION IF NOT EXISTS "uuid-ossp";`. Ensure that the PostgreSQL database user executing these scripts has the necessary permissions to create extensions. This extension is crucial for `gen_random_uuid()` functionality.
4.  **Flyway Integration:** The generated scripts are structured to be compatible with Flyway (or similar database migration tools).
    *   `V1__init_schema.sql`: Contains all `CREATE TABLE`, `CREATE INDEX`, and constraint definitions.
    *   `V2__seed_admin_user.sql`: Contains the initial `INSERT` statements for the default `db_admin` user and role assignment.
    *   `V3__setup_roles_and_permissions.sql`: Contains `CREATE ROLE` and `GRANT` statements for database access control.
    It is recommended to run these scripts in the specified order.
5.  **Role-Based Access Control:** The DCL script sets up basic role-based access. Further fine-tuning of privileges (e.g., row-level security, specific function execution) might be required based on evolving application needs.
6.  **Audit Log Immutability:** The LLD notes that no `UPDATE` or `DELETE` is allowed on the `audit_log` table. This policy should be enforced either via database-level triggers or strictly by application logic.