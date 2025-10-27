-- Idempotent seed for roles, permits, and mappings
-- Target DB: PostgreSQL

-- Permits (enum names in uppercase; code is lowercase and unique)
INSERT INTO permits (code, permit)
SELECT 'read', 'READ'
WHERE NOT EXISTS (SELECT 1 FROM permits WHERE code = 'read');

INSERT INTO permits (code, permit)
SELECT 'write', 'WRITE'
WHERE NOT EXISTS (SELECT 1 FROM permits WHERE code = 'write');

INSERT INTO permits (code, permit)
SELECT 'delete', 'DELETE'
WHERE NOT EXISTS (SELECT 1 FROM permits WHERE code = 'delete');

INSERT INTO permits (code, permit)
SELECT 'admin', 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM permits WHERE code = 'admin');

INSERT INTO permits (code, permit)
SELECT 'manage_users', 'MANAGE_USERS'
WHERE NOT EXISTS (SELECT 1 FROM permits WHERE code = 'manage_users');

INSERT INTO permits (code, permit)
SELECT 'manage_roles', 'MANAGE_ROLES'
WHERE NOT EXISTS (SELECT 1 FROM permits WHERE code = 'manage_roles');

INSERT INTO permits (code, permit)
SELECT 'generate_reports', 'GENERATE_REPORTS'
WHERE NOT EXISTS (SELECT 1 FROM permits WHERE code = 'generate_reports');

-- Roles (name is unique)
INSERT INTO roles (name, description)
SELECT 'ADMIN', 'Administrator role with full permissions'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ADMIN');

INSERT INTO roles (name, description)
SELECT 'MANAGER', 'Manager role with write and reporting permissions'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'MANAGER');

INSERT INTO roles (name, description)
SELECT 'EMPLOYEE', 'Employee role with read permissions'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'EMPLOYEE');

INSERT INTO roles (name, description)
SELECT 'CLIENT', 'Default client role'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'CLIENT');

-- Role to Permit mappings (avoid duplicates via NOT EXISTS)
-- ADMIN: READ, WRITE, DELETE, ADMIN, MANAGE_USERS, MANAGE_ROLES, GENERATE_REPORTS
INSERT INTO role_permits (role_id, permit_id)
SELECT r.id, p.id
FROM roles r, permits p
WHERE r.name = 'ADMIN' AND p.permit IN ('READ','WRITE','DELETE','ADMIN','MANAGE_USERS','MANAGE_ROLES','GENERATE_REPORTS')
AND NOT EXISTS (
    SELECT 1 FROM role_permits rp WHERE rp.role_id = r.id AND rp.permit_id = p.id
);

-- MANAGER: READ, WRITE, GENERATE_REPORTS
INSERT INTO role_permits (role_id, permit_id)
SELECT r.id, p.id
FROM roles r, permits p
WHERE r.name = 'MANAGER' AND p.permit IN ('READ','WRITE','GENERATE_REPORTS')
AND NOT EXISTS (
    SELECT 1 FROM role_permits rp WHERE rp.role_id = r.id AND rp.permit_id = p.id
);

-- EMPLOYEE: READ
INSERT INTO role_permits (role_id, permit_id)
SELECT r.id, p.id
FROM roles r, permits p
WHERE r.name = 'EMPLOYEE' AND p.permit IN ('READ')
AND NOT EXISTS (
    SELECT 1 FROM role_permits rp WHERE rp.role_id = r.id AND rp.permit_id = p.id
);

-- CLIENT: READ
INSERT INTO role_permits (role_id, permit_id)
SELECT r.id, p.id
FROM roles r, permits p
WHERE r.name = 'CLIENT' AND p.permit IN ('READ')
AND NOT EXISTS (
    SELECT 1 FROM role_permits rp WHERE rp.role_id = r.id AND rp.permit_id = p.id
);

-- Ensure singleton store (ID=1)
INSERT INTO stores (id, store_name, address, city)
SELECT 1, '', '', ''
WHERE NOT EXISTS (SELECT 1 FROM stores WHERE id = 1);

-- Seed home carousel for store 1 (idempotent per item)
INSERT INTO store_home_carousel (store_id, url, title, href)
SELECT 1, 'https://picsum.photos/id/1011/1200/400', 'Welcome to Shoppify', '/'
WHERE NOT EXISTS (
    SELECT 1 FROM store_home_carousel WHERE store_id = 1 AND url = 'https://picsum.photos/id/1011/1200/400'
);

INSERT INTO store_home_carousel (store_id, url, title, href)
SELECT 1, 'https://picsum.photos/id/1015/1200/400', 'Big Sale', '/sale'
WHERE NOT EXISTS (
    SELECT 1 FROM store_home_carousel WHERE store_id = 1 AND url = 'https://picsum.photos/id/1015/1200/400'
);

INSERT INTO store_home_carousel (store_id, url, title, href)
SELECT 1, 'https://picsum.photos/id/1025/1200/400', 'New Arrivals', '/new'
WHERE NOT EXISTS (
    SELECT 1 FROM store_home_carousel WHERE store_id = 1 AND url = 'https://picsum.photos/id/1025/1200/400'
);

-- Seed categories
INSERT INTO categories (name, img_url)
SELECT 'Electronics', 'https://picsum.photos/id/180/400/300'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Electronics');

INSERT INTO categories (name, img_url)
SELECT 'Clothing', 'https://picsum.photos/id/250/400/300'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Clothing');

INSERT INTO categories (name, img_url)
SELECT 'Home', 'https://picsum.photos/id/1080/400/300'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Home');

-- Seed products
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity)
SELECT 'Smartphone X', 'Latest generation smartphone', 1200.00, 1000.00, 'SP001', 'SP001BAR', 'TechBrand', 'https://picsum.photos/id/1/600/400', 10
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Smartphone X');

INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity)
SELECT 'Laptop Pro', 'Professional laptop', 2500.00, 2200.00, 'LP002', 'LP002BAR', 'TechBrand', 'https://picsum.photos/id/2/600/400', 7
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Laptop Pro');

INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity)
SELECT 'Cotton T-Shirt', 'Soft cotton t-shirt', 25.00, 15.00, 'TS003', 'TS003BAR', 'FashionBrand', 'https://picsum.photos/id/3/600/400', 50
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Cotton T-Shirt');

-- Link products to categories
-- Smartphone X -> Electronics
INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.name = 'Smartphone X' AND c.name = 'Electronics'
AND NOT EXISTS (
    SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id
);

-- Laptop Pro -> Electronics
INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.name = 'Laptop Pro' AND c.name = 'Electronics'
AND NOT EXISTS (
    SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id
);

-- Cotton T-Shirt -> Clothing
INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.name = 'Cotton T-Shirt' AND c.name = 'Clothing'
AND NOT EXISTS (
    SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id
);
