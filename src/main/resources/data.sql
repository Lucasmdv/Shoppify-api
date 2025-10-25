-- Stockify seed data aligned with current schema
-- DB: PostgreSQL | Entities: roles, permits, credentials, clients, stores, providers,
-- categories, products (+joins), transactions (optional)

-- 1) Permits (enum: READ, WRITE, DELETE, ADMIN, MANAGE_USERS, MANAGE_ROLES, GENERATE_REPORTS)
-- Ensure both code and permit string are provided (code = lowercase of enum)
INSERT INTO permits (code, permit) VALUES
 ('read', 'READ'),
 ('write', 'WRITE'),
 ('delete', 'DELETE'),
 ('admin', 'ADMIN'),
 ('manage_users', 'MANAGE_USERS'),
 ('manage_roles', 'MANAGE_ROLES'),
 ('generate_reports', 'GENERATE_REPORTS');

-- 2) Roles
INSERT INTO roles (name, description) VALUES
 ('ADMIN', 'System administrators with full access'),
 ('MANAGER', 'Store managers with reporting access'),
 ('EMPLOYEE', 'Employees with read access');

-- 3) Role -> Permit mapping
-- ADMIN: all permits
INSERT INTO role_permits (role_id, permit_id)
SELECT r.id, p.id FROM roles r, permits p WHERE r.name='ADMIN' AND p.permit IN ('READ','WRITE','DELETE','ADMIN','MANAGE_USERS','MANAGE_ROLES','GENERATE_REPORTS');

-- MANAGER: READ, WRITE, GENERATE_REPORTS
INSERT INTO role_permits (role_id, permit_id)
SELECT r.id, p.id FROM roles r, permits p WHERE r.name='MANAGER' AND p.permit IN ('READ','WRITE','GENERATE_REPORTS');

-- EMPLOYEE: READ
INSERT INTO role_permits (role_id, permit_id)
SELECT r.id, p.id FROM roles r, permits p WHERE r.name='EMPLOYEE' AND p.permit IN ('READ');

-- 4) Stores
INSERT INTO stores (store_name, address, city) VALUES
 ('Tienda Central', 'Av. Principal 123', 'Buenos Aires'),
 ('Sucursal Norte', 'Calle Norte 456', 'Córdoba');

-- 5) Clients (users)
-- Using working avatar URLs
INSERT INTO clients (client_first_name, client_last_name, client_dni, client_phone, client_img) VALUES
 ('Juan',  'Pérez',   '12345678', '+54 11 3000-0001', 'https://placehold.co/300x300?text=Juan+P%C3%A9rez'),
 ('María', 'González','23456789', '+54 11 3000-0002', 'https://placehold.co/300x300?text=Mar%C3%ADa+Gonz%C3%A1lez'),
 ('Ana',   'Martínez','34567890', '+54 11 3000-0003', 'https://placehold.co/300x300?text=Ana+Mart%C3%ADnez');

-- 6) Providers
INSERT INTO providers (business_name, tax_id, tax_address, phone, email, contact_name, active) VALUES
 ('Electro SA',        '30-12345678-9', 'Av. Corrientes 1000, CABA', '+54 11 4000-1000', 'contacto@electrosa.com',        'Sergio López',   true),
 ('Textil Moda SRL',   '30-23456789-0', 'Calle San Martín 500, CBA', '+54 351 400-2000', 'ventas@textilmoda.com',         'Laura Pérez',    true),
 ('Alimentos Frescos','30-34567890-1', 'Av. Colon 1500, CBA',       '+54 351 400-3000', 'info@alimentosfrescos.com',     'Marcos Díaz',    true),
 ('Hogar y Deco SA',  '30-45678901-2', 'Bv. Oroño 800, Rosario',    '+54 341 400-4000', 'hola@hogarydeco.com',           'Sofía Romero',   true),
 ('Juguetes Divertidos SA','30-56789012-3','Av. Santa Fe 2000, CABA','+54 11 4000-5000','ventas@juguetesdivertidos.com', 'Pablo Fernández',true);

-- 7) Categories (with working images)
INSERT INTO categories (name, img_url) VALUES
 ('Electrónicos', 'https://placehold.co/600x400?text=Electr%C3%B3nicos'),
 ('Ropa',         'https://placehold.co/600x400?text=Ropa'),
 ('Alimentos',    'https://placehold.co/600x400?text=Alimentos'),
 ('Hogar',        'https://placehold.co/600x400?text=Hogar'),
 ('Juguetes',     'https://placehold.co/600x400?text=Juguetes');

-- 8) Products (with working images)
-- Note: img_URL column name (exact case/underscore)
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity) VALUES
 ('Smartphone X', 'Pantalla 6.1", 128GB, cámara dual',          1200.00, 1200.00, 'SMX-128-BLK', '7790000000011', 'TechBrand', 'https://placehold.co/600x400?text=Smartphone+X', 15.00),
 ('Laptop Pro',   '14" i7, 16GB RAM, 512GB SSD',                2200.00, 2200.00, 'LTP-14-I7',   '7790000000028', 'TechBrand', 'https://placehold.co/600x400?text=Laptop+Pro',   8.00),
 ('Camiseta',     'Camiseta de algodón 100%',                     25.00,   25.00,  'TSH-ALG-BLA', '7790000000035', 'TextilCo',  'https://placehold.co/600x400?text=Camiseta',     120.00),
 ('Jeans',        'Jeans corte recto, denim azul',                55.00,   55.00,  'JEAN-STD-BLU','7790000000042', 'TextilCo',  'https://placehold.co/600x400?text=Jeans',        60.00),
 ('Arroz 1kg',    'Arroz premium largo fino 1kg',                 5.50,    5.50,   'ARZ-1KG',     '7790000000059', 'FoodCorp',  'https://placehold.co/600x400?text=Arroz+1kg',    300.00),
 ('Aceite 900ml', 'Aceite de oliva extra virgen 900ml',          12.00,   12.00,  'ACE-900-OLV', '7790000000066', 'FoodCorp',  'https://placehold.co/600x400?text=Aceite',       180.00),
 ('Sillón',       'Sillón reclinable tapizado',                  350.00,  350.00,  'SIL-REC',     '7790000000073', 'HomePlus',  'https://placehold.co/600x400?text=Sill%C3%B3n',  10.00),
 ('Mesa Centro',  'Mesa de centro madera y vidrio',              180.00,  180.00,  'MSC-CTR',     '7790000000080', 'HomePlus',  'https://placehold.co/600x400?text=Mesa+Centro',  20.00),
 ('Muñeca',       'Muñeca interactiva con accesorios',            35.00,   35.00,  'MUN-INT',     '7790000000097', 'ToyFun',    'https://placehold.co/600x400?text=Mu%C3%B1eca',  85.00),
 ('Bloques',      'Set de bloques de construcción 100 piezas',    20.00,   20.00,  'BLQ-100',     '7790000000103', 'ToyFun',    'https://placehold.co/600x400?text=Bloques',      90.00),
 ('Auriculares',  'Auriculares inalámbricos con estuche',         80.00,   80.00,  'EAR-BT',      '7790000000110', 'TechBrand', 'https://placehold.co/600x400?text=Auriculares',  40.00),
 ('Monitor 24"',  'Monitor 24" FHD IPS',                        170.00,  170.00,  'MON-24FHD',   '7790000000127', 'TechBrand', 'https://placehold.co/600x400?text=Monitor+24',   25.00);

-- 9) Product -> Category mapping
-- Use names to resolve IDs to keep this robust
INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c WHERE p.name='Smartphone X' AND c.name='Electrónicos';
INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c WHERE p.name='Laptop Pro'   AND c.name='Electrónicos';
INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c WHERE p.name='Auriculares'  AND c.name='Electrónicos';
INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c WHERE p.name='Monitor 24"' AND c.name='Electrónicos';
INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c WHERE p.name='Camiseta'     AND c.name='Ropa';
INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c WHERE p.name='Jeans'        AND c.name='Ropa';
INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c WHERE p.name='Arroz 1kg'    AND c.name='Alimentos';
INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c WHERE p.name='Aceite 900ml' AND c.name='Alimentos';
INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c WHERE p.name='Sillón'       AND c.name='Hogar';
INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c WHERE p.name='Mesa Centro'  AND c.name='Hogar';
INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c WHERE p.name='Muñeca'       AND c.name='Juguetes';
INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c WHERE p.name='Bloques'      AND c.name='Juguetes';

-- 10) Product -> Provider mapping
INSERT INTO products_providers (product_id, provider_id)
SELECT p.id, v.id FROM products p, providers v WHERE p.name IN ('Smartphone X','Laptop Pro','Auriculares','Monitor 24"') AND v.business_name='Electro SA';
INSERT INTO products_providers (product_id, provider_id)
SELECT p.id, v.id FROM products p, providers v WHERE p.name IN ('Camiseta','Jeans') AND v.business_name='Textil Moda SRL';
INSERT INTO products_providers (product_id, provider_id)
SELECT p.id, v.id FROM products p, providers v WHERE p.name IN ('Arroz 1kg','Aceite 900ml') AND v.business_name='Alimentos Frescos';
INSERT INTO products_providers (product_id, provider_id)
SELECT p.id, v.id FROM products p, providers v WHERE p.name IN ('Sillón','Mesa Centro') AND v.business_name='Hogar y Deco SA';
INSERT INTO products_providers (product_id, provider_id)
SELECT p.id, v.id FROM products p, providers v WHERE p.name IN ('Muñeca','Bloques') AND v.business_name='Juguetes Divertidos SA';

-- 11) Credentials (users able to log in)
-- Password hash for 'password123' (bcrypt)
INSERT INTO credentials (username, email, password, user_id) VALUES
 ('admin',   'admin@stockify.com',   '$2a$10$rPiEAgQNIT1TCoKi.Iy9wuaZMhDU9Ocs9XTTaP.IS6xCxfGtJ9ZYy', (SELECT c.client_id FROM clients c WHERE c.client_dni='12345678')),
 ('manager', 'manager@stockify.com', '$2a$10$rPiEAgQNIT1TCoKi.Iy9wuaZMhDU9Ocs9XTTaP.IS6xCxfGtJ9ZYy', (SELECT c.client_id FROM clients c WHERE c.client_dni='23456789')),
 ('employee','employee@stockify.com','$2a$10$rPiEAgQNIT1TCoKi.Iy9wuaZMhDU9Ocs9XTTaP.IS6xCxfGtJ9ZYy', (SELECT c.client_id FROM clients c WHERE c.client_dni='34567890'));

-- 12) Credentials -> Roles
INSERT INTO credentials_roles (credential_id, role_id)
SELECT cr.id, r.id FROM credentials cr, roles r WHERE cr.email='admin@stockify.com' AND r.name='ADMIN';
INSERT INTO credentials_roles (credential_id, role_id)
SELECT cr.id, r.id FROM credentials cr, roles r WHERE cr.email='manager@stockify.com' AND r.name='MANAGER';
INSERT INTO credentials_roles (credential_id, role_id)
SELECT cr.id, r.id FROM credentials cr, roles r WHERE cr.email='employee@stockify.com' AND r.name='EMPLOYEE';

-- Optional example transactions (uncomment if you want demo data)
-- INSERT INTO transactions (total, date_time, payment_method, description, type, store_id)
-- VALUES (1225.00, NOW(), 'CASH', 'Venta inicial', 'SALE', (SELECT id FROM stores WHERE store_name='Tienda Central'));
-- INSERT INTO sales (transaction_id, client_id)
-- VALUES ((SELECT id FROM transactions ORDER BY id DESC LIMIT 1), (SELECT client_id FROM clients WHERE client_dni='12345678'));
