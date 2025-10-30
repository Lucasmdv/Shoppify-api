-- Idempotent seed for roles, permits, store, categories, products and mappings
-- Target DB: PostgreSQL

/* ================================
   PERMITS
   (enum names in uppercase; code is lowercase and unique)
   ================================ */
/*INSERT INTO permits (code, permit)
SELECT 'read', 'READ'
WHERE NOT EXISTS (SELECT 1 FROM permits WHERE code = 'read');

INSERT INTO permits (code, permit)
SELECT 'write', 'WRITE'
WHERE NOT EXISTS (SELECT 1 FROM permits WHERE code = 'write');

INSERT INTO permits (code, permit)
SELECT 'delete', 'DELETE'
WHERE NOT EXISTS (SELECT 1 FROM permits WHERE code = 'delete');

INSERT INTO permits (code, permit)
SELECT 'manage_users', 'MANAGE_USERS'
WHERE NOT EXISTS (SELECT 1 FROM permits WHERE code = 'manage_users');

INSERT INTO permits (code, permit)
SELECT 'manage_roles', 'MANAGE_ROLES'
WHERE NOT EXISTS (SELECT 1 FROM permits WHERE code = 'manage_roles');

INSERT INTO permits (code, permit)
SELECT 'generate_reports', 'GENERATE_REPORTS'
WHERE NOT EXISTS (SELECT 1 FROM permits WHERE code = 'generate_reports');

*/
--Simplificado para presentacion de frontend
INSERT INTO permits (code, permit)
SELECT 'admin', 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM permits WHERE code = 'admin');

INSERT INTO permits (code, permit)
SELECT 'user', 'USER'
WHERE NOT EXISTS (SELECT 1 FROM permits WHERE code = 'user');




/* ================================
   ROLES (name is unique)
   ================================

 INSERT INTO roles (name, description)
SELECT 'MANAGER', 'Manager role with write and reporting permissions'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'MANAGER');

INSERT INTO roles (name, description)
SELECT 'EMPLOYEE', 'Employee role with read permissions'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'EMPLOYEE');
   */

INSERT INTO roles (name, description)
SELECT 'ADMIN', 'Administrator role with full permissions'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ADMIN');


INSERT INTO roles (name, description)
SELECT 'CLIENT', 'Default client role'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'CLIENT');

/* ================================
   ROLE → PERMIT mappings (avoid duplicates)
   ================================ */
-- ADMIN: READ, WRITE, DELETE, ADMIN, MANAGE_USERS, MANAGE_ROLES, GENERATE_REPORTS
INSERT INTO role_permits (role_id, permit_id)
SELECT r.id, p.id
FROM roles r, permits p
WHERE r.name = 'ADMIN'
  AND p.permit IN ('ADMIN')
  AND NOT EXISTS (
    SELECT 1 FROM role_permits rp WHERE rp.role_id = r.id AND rp.permit_id = p.id
);

-- CLIENT:

INSERT INTO role_permits (role_id, permit_id)
SELECT r.id, p.id
FROM roles r, permits p
WHERE r.name = 'CLIENT'
  AND p.permit IN ('USER')
  AND NOT EXISTS (
    SELECT 1 FROM role_permits rp WHERE rp.role_id = r.id AND rp.permit_id = p.id
);

/* ================================
   STORE (singleton ID=1)
   ================================ */
INSERT INTO stores (id, store_name, address, city,phone)
SELECT 1, 'Shoppify', 'Calle Falsa 123', 'Mar del Plata', '999999999'
WHERE NOT EXISTS (SELECT 1 FROM stores WHERE id = 1);

/* ================================
   HOME CAROUSEL (store_id=1)
   ================================ */
INSERT INTO store_home_carousel (store_id, url, title, href)
SELECT 1, 'https://http2.mlstatic.com/D_NQ_788667-MLA96325313369_102025-OO.webp', 'Welcome to Shoppify', '/'
WHERE NOT EXISTS (
    SELECT 1 FROM store_home_carousel WHERE store_id = 1 AND url = 'https://http2.mlstatic.com/D_NQ_788667-MLA96325313369_102025-OO.webp'
);

INSERT INTO store_home_carousel (store_id, url, title, href)
SELECT 1, 'https://http2.mlstatic.com/D_NQ_853625-MLA95865472368_102025-OO.webp', 'Big Sale', '/sale'
WHERE NOT EXISTS (
    SELECT 1 FROM store_home_carousel WHERE store_id = 1 AND url = 'https://http2.mlstatic.com/D_NQ_853625-MLA95865472368_102025-OO.webp'
);

INSERT INTO store_home_carousel (store_id, url, title, href)
SELECT 1, 'https://http2.mlstatic.com/D_NQ_639856-MLA95484130475_102025-OO.webp', 'New Arrivals', '/new'
WHERE NOT EXISTS (
    SELECT 1 FROM store_home_carousel WHERE store_id = 1 AND url = 'https://http2.mlstatic.com/D_NQ_639856-MLA95484130475_102025-OO.webp'
);

/* ================================
   CATEGORIES
   ================================ */
INSERT INTO categories (name, img_url)
SELECT 'Electronics', 'https://picsum.photos/id/180/400/300'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Electronics');

INSERT INTO categories (name, img_url)
SELECT 'Clothing', 'https://picsum.photos/id/250/400/300'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Clothing');

INSERT INTO categories (name, img_url)
SELECT 'Home', 'https://picsum.photos/id/1080/400/300'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Home');

-- Extra category used in mappings
INSERT INTO categories (name, img_url)
SELECT 'Accessories', 'https://picsum.photos/id/340/400/300'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Accessories');

/* ================================
   PRODUCTS (3 base + 10 adicionales)
   ================================ */
-- Base (por si aún no estaban)
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity)
SELECT 'Smartphone X', 'Latest generation smartphone', 1200.00, 1000.00, 'SP001', 'SP001BAR', 'TechBrand', 'https://picsum.photos/id/1/600/400', 10
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Smartphone X');

INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity)
SELECT 'Laptop Pro', 'Professional laptop', 2500.00, 2200.00, 'LP002', 'LP002BAR', 'TechBrand', 'https://picsum.photos/id/2/600/400', 7
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Laptop Pro');

INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity)
SELECT 'Cotton T-Shirt', 'Soft cotton t-shirt', 25.00, 15.00, 'TS003', 'TS003BAR', 'FashionBrand', 'https://picsum.photos/id/3/600/400', 50
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Cotton T-Shirt');

-- Adicionales (10)
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity)
SELECT 'Wireless Headphones', 'Noise-cancelling wireless headphones', 150.00, 100.00, 'WH004', 'WH004BAR', 'SoundBrand', 'https://picsum.photos/id/4/600/400', 25
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Wireless Headphones');

INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity)
SELECT 'Gaming Mouse', 'Ergonomic gaming mouse with RGB lighting', 60.00, 40.00, 'GM005', 'GM005BAR', 'GameTech', 'https://picsum.photos/id/5/600/400', 30
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Gaming Mouse');

INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity)
SELECT 'Sports Sneakers', 'Lightweight and durable sports sneakers', 90.00, 65.00, 'SN006', 'SN006BAR', 'FootBrand', 'https://picsum.photos/id/6/600/400', 20
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Sports Sneakers');

INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity)
SELECT 'LED TV 50"', '4K Ultra HD Smart TV', 700.00, 500.00, 'TV007', 'TV007BAR', 'VisionPlus', 'https://picsum.photos/id/7/600/400', 12
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'LED TV 50"');

INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity)
SELECT 'Bluetooth Speaker', 'Portable high-quality speaker', 45.00, 30.00, 'BS008', 'BS008BAR', 'SoundBrand', 'https://picsum.photos/id/8/600/400', 40
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Bluetooth Speaker');

INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity)
SELECT 'Coffee Maker', 'Automatic coffee maker', 120.00, 90.00, 'CM009', 'CM009BAR', 'HomeAppliance', 'https://picsum.photos/id/9/600/400', 15
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Coffee Maker');

INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity)
SELECT 'Gaming Keyboard', 'Mechanical keyboard with RGB lighting', 110.00, 75.00, 'GK010', 'GK010BAR', 'GameTech', 'https://picsum.photos/id/10/600/400', 25
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Gaming Keyboard');

INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity)
SELECT 'Men Jacket', 'Winter waterproof jacket', 80.00, 55.00, 'JK011', 'JK011BAR', 'FashionBrand', 'https://picsum.photos/id/11/600/400', 18
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Men Jacket');

INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity)
SELECT 'Women Sunglasses', 'UV400 protective sunglasses', 35.00, 20.00, 'SG012', 'SG012BAR', 'StyleBrand', 'https://picsum.photos/id/12/600/400', 50
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Women Sunglasses');

INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity)
SELECT 'Smartwatch Pro', 'Smartwatch with health monitoring features', 200.00, 150.00, 'SW013', 'SW013BAR', 'TechBrand', 'https://picsum.photos/id/13/600/400', 22
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Smartwatch Pro');

/* ================================
   PRODUCT ↔ CATEGORY mappings
   ================================ */
-- Smartphone X -> Electronics
INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.name = 'Smartphone X' AND c.name = 'Electronics'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- Laptop Pro -> Electronics
INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.name = 'Laptop Pro' AND c.name = 'Electronics'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- Cotton T-Shirt -> Clothing
INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.name = 'Cotton T-Shirt' AND c.name = 'Clothing'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- Wireless Headphones -> Electronics
INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.name = 'Wireless Headphones' AND c.name = 'Electronics'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- Gaming Mouse -> Electronics
INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.name = 'Gaming Mouse' AND c.name = 'Electronics'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- Sports Sneakers -> Clothing
INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.name = 'Sports Sneakers' AND c.name = 'Clothing'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- LED TV 50" -> Electronics
INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.name = 'LED TV 50"' AND c.name = 'Electronics'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- Bluetooth Speaker -> Electronics
INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.name = 'Bluetooth Speaker' AND c.name = 'Electronics'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- Coffee Maker -> Home
INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.name = 'Coffee Maker' AND c.name = 'Home'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- Gaming Keyboard -> Electronics
INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.name = 'Gaming Keyboard' AND c.name = 'Electronics'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- Men Jacket -> Clothing
INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.name = 'Men Jacket' AND c.name = 'Clothing'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- Women Sunglasses -> Accessories
INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.name = 'Women Sunglasses' AND c.name = 'Accessories'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- Smartwatch Pro -> Electronics
INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.name = 'Smartwatch Pro' AND c.name = 'Electronics'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);
