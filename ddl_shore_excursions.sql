-- DDL for schema creation

-- Table: port
CREATE TABLE port (
    port_code VARCHAR(10) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    region VARCHAR(100) NOT NULL,
    description TEXT,
    map_link TEXT,
    site_highlights TEXT,
    fun_facts TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100) NOT NULL
);

-- Table: tour
CREATE TABLE tour (
    tour_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    port_code VARCHAR(10) NOT NULL,
    tour_name VARCHAR(200) NOT NULL,
    duration_minutes INTEGER CHECK (duration_minutes > 0),
    category VARCHAR(100),
    description TEXT,
    highlights TEXT,
    booking_conditions TEXT,
    meal_included BOOLEAN NOT NULL DEFAULT FALSE,
    activity_level VARCHAR(20) CHECK (activity_level IN ('LOW','MODERATE','HIGH','EXTREME')),
    accessibility_info TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100) NOT NULL,
    version INTEGER NOT NULL DEFAULT 0
);

-- Table: port_image
CREATE TABLE port_image (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    port_code VARCHAR(10) NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    display_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100) NOT NULL
);

-- Table: tour_image
CREATE TABLE tour_image (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tour_id UUID NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    display_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100) NOT NULL
);

-- Table: app_user
CREATE TABLE app_user (
    user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(150) NOT NULL UNIQUE,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    failed_login_attempts INTEGER NOT NULL DEFAULT 0,
    locked_until TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100) NOT NULL
);

-- Table: user_role
CREATE TABLE user_role (
    user_id UUID NOT NULL,
    role VARCHAR(50) NOT NULL CHECK (role IN ('ROLE_DB_ADMIN','ROLE_EDITOR','ROLE_USER','ROLE_VIEWER')),
    PRIMARY KEY (user_id, role)
);

-- Table: audit_log
CREATE TABLE audit_log (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    entity_type VARCHAR(50) NOT NULL,
    entity_id VARCHAR(255) NOT NULL,
    action VARCHAR(20) NOT NULL CHECK (action IN ('CREATE','UPDATE','DELETE','LOGIN','LOGIN_FAILED','LOGOUT')),
    performed_by VARCHAR(100) NOT NULL,
    performed_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    old_value TEXT,
    new_value TEXT
);

-- Foreign Key Constraints
ALTER TABLE tour ADD CONSTRAINT fk_tour_port FOREIGN KEY (port_code) REFERENCES port (port_code) ON DELETE RESTRICT;
ALTER TABLE port_image ADD CONSTRAINT fk_port_image_port FOREIGN KEY (port_code) REFERENCES port (port_code) ON DELETE CASCADE;
ALTER TABLE tour_image ADD CONSTRAINT fk_tour_image_tour FOREIGN KEY (tour_id) REFERENCES tour (tour_id) ON DELETE CASCADE;
ALTER TABLE user_role ADD CONSTRAINT fk_user_role_app_user FOREIGN KEY (user_id) REFERENCES app_user (user_id) ON DELETE CASCADE;

-- Indexes
CREATE INDEX idx_port_region ON port (region);
CREATE INDEX idx_tour_port_code ON tour (port_code);
CREATE INDEX idx_tour_category ON tour (category);
CREATE INDEX idx_tour_activity_level ON tour (activity_level);
CREATE INDEX idx_port_image_port_code ON port_image (port_code);
CREATE INDEX idx_tour_image_tour_id ON tour_image (tour_id);
CREATE INDEX idx_audit_log_entity ON audit_log (entity_type, entity_id);
CREATE INDEX idx_audit_log_performed_by ON audit_log (performed_by);
CREATE INDEX idx_audit_log_performed_at ON audit_log (performed_at);