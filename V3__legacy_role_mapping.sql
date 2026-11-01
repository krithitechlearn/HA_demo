-- V3__legacy_role_mapping.sql
-- Documents legacy IBM Domino role to RBAC role mapping
-- Related: LLD Section 5.3, 5.4, US-047, US-048
-- This is a reference/documentation table for migration audit purposes.

CREATE TABLE IF NOT EXISTS legacy_role_mapping (
    id              SERIAL PRIMARY KEY,
    legacy_role     VARCHAR(100) NOT NULL,
    mapped_role     VARCHAR(50)  NOT NULL,
    description     TEXT,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

INSERT INTO legacy_role_mapping (legacy_role, mapped_role, description) VALUES
    ('[SHXDbAdmin]',  'ROLE_DB_ADMIN', 'Legacy Domino DB Admin role maps to ROLE_DB_ADMIN'),
    ('[SHXEditor]',   'ROLE_EDITOR',   'Legacy Domino Editor role maps to ROLE_EDITOR'),
    ('[SHXUser]',     'ROLE_USER',     'Legacy Domino User role maps to ROLE_USER'),
    ('[SHXViewer]',   'ROLE_VIEWER',   'Legacy Domino Viewer role maps to ROLE_VIEWER')
ON CONFLICT DO NOTHING;
