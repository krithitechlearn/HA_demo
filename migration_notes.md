# Migration Notes

*   The DML script for the `db_admin` user uses a placeholder UUID and password hash. These should be securely generated and configured for production environments.
*   The LLD mentions Flyway migrations (`V1__init_schema.sql`, `V2__seed_roles_and_admin.sql`, etc.). The generated DDL and DML scripts correspond to what would typically be found in `V1__init_schema.sql` and `V2__seed_roles_and_admin.sql` respectively. It is recommended to integrate these scripts into the Flyway migration process.
*   The `audit_log` table is designed to be immutable. Application logic or database triggers should enforce that no `UPDATE` or `DELETE` operations are allowed on this table.
