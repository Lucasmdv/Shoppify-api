INSERT INTO permits (code, permit) VALUES 
('read', 'READ'),
('write', 'WRITE'),
('delete', 'DELETE'),
('admin', 'ADMIN'),
('user', 'USER'),
('manage_users', 'MANAGE_USERS'),
('manage_roles', 'MANAGE_ROLES'),
('generate_reports', 'GENERATE_REPORTS');
INSERT INTO roles (role) VALUES ('ADMIN');
INSERT INTO role_permits (role_id, permit_id) VALUES (1, 1); -- READ
INSERT INTO role_permits (role_id, permit_id) VALUES (1, 2); -- WRITE
INSERT INTO role_permits (role_id, permit_id) VALUES (1, 3); -- DELETE
INSERT INTO role_permits (role_id, permit_id) VALUES (1, 4); -- ADMIN
INSERT INTO employee (name, last_name, dni, status, active) VALUES ('Juan', 'Pérez', '12345678', 'OFFLINE', true);
INSERT INTO credentials (username, email, password, employee_dni) VALUES ('admin', 'admin@stockify.com', '$2a$10$C3282YiG0R9JBUP.OZAcgOMFdddcXVn3Um9lUyqxk8/7UmtcmP8ru', '12345678');
INSERT INTO credentials_roles (credential_id, role_id) VALUES (1, 1); -- admin -> ADMIN
