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
SELECT 'USER', 'Default user role'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'USER');

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

-- USER:

INSERT INTO role_permits (role_id, permit_id)
SELECT r.id, p.id
FROM roles r, permits p
WHERE r.name = 'USER'
  AND p.permit IN ('USER')
  AND NOT EXISTS (
    SELECT 1 FROM role_permits rp WHERE rp.role_id = r.id AND rp.permit_id = p.id
);

/* ================================
   DEMO USERS (USER & ADMIN)
   ================================ */
INSERT INTO users (user_first_name, user_last_name, user_dni, user_phone, user_img)
SELECT 'Test', 'User', '40289718', '2232057591', 'https://img.freepik.com/foto-gratis/joven-hombre-barbudo-camisa-rayas_273609-5677.jpg?semt=ais_hybrid&w=740&q=80'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE user_dni = '40289718');

INSERT INTO users (user_first_name, user_last_name, user_dni, user_phone, user_img)
SELECT 'Test', 'Admin', '41689718', '2232057295', 'https://media.istockphoto.com/id/1090878494/es/foto/retrato-de-joven-sonriente-a-hombre-guapo-en-camiseta-polo-azul-aislado-sobre-fondo-gris-de.jpg?s=612x612&w=0&k=20&c=dHFsDEJSZ1kuSO4wTDAEaGOJEF-HuToZ6Gt-E2odc6U='
WHERE NOT EXISTS (SELECT 1 FROM users WHERE user_dni = '41689718');


INSERT INTO credentials (username, email, password, user_id)
SELECT 'test.user', 'user@user', '$2a$10$ETW1zaXw0ZHn2rAMvfuwE.4dF6bl/g5Gr42GNXV5uZz./moucSZ3i', u.user_id
FROM users u
WHERE u.user_dni = '40289718'
  AND NOT EXISTS (SELECT 1 FROM credentials WHERE email = 'user@user');

INSERT INTO credentials (username, email, password, user_id)
SELECT 'test.admin', 'admin@admin', '$2a$10$ETW1zaXw0ZHn2rAMvfuwE.4dF6bl/g5Gr42GNXV5uZz./moucSZ3i', u.user_id
FROM users u
WHERE u.user_dni = '41689718'
  AND NOT EXISTS (SELECT 1 FROM credentials WHERE email = 'admin@admin');

INSERT INTO credentials_roles (credential_id, role_id)
SELECT cred.id, r.id
FROM credentials cred, roles r
WHERE cred.email = 'user@user'
  AND r.name = 'USER'
  AND NOT EXISTS (
    SELECT 1 FROM credentials_roles cr WHERE cr.credential_id = cred.id AND cr.role_id = r.id
);

INSERT INTO credentials_roles (credential_id, role_id)
SELECT cred.id, r.id
FROM credentials cred, roles r
WHERE cred.email = 'admin@admin'
  AND r.name = 'ADMIN'
  AND NOT EXISTS (
    SELECT 1 FROM credentials_roles cr WHERE cr.credential_id = cred.id AND cr.role_id = r.id
);

/* ================================
   CARTS for demo users
   ================================ */
INSERT INTO cart (user_id)
SELECT u.user_id FROM users u
WHERE u.user_dni = '40289718'
  AND NOT EXISTS (SELECT 1 FROM cart c WHERE c.user_id = u.user_id);

INSERT INTO cart (user_id)
SELECT u.user_id FROM users u
WHERE u.user_dni = '41689718'
  AND NOT EXISTS (SELECT 1 FROM cart c WHERE c.user_id = u.user_id);

/* ================================
   WISHLISTS for demo users
   ================================ */
INSERT INTO wishlists (name, user_id)
SELECT 1, u.user_id FROM users u
WHERE u.user_dni = '40289718'
  AND NOT EXISTS (SELECT 1 FROM wishlists w WHERE w.user_id = u.user_id);

INSERT INTO wishlists (name, user_id)
SELECT 2, u.user_id FROM users u
WHERE u.user_dni = '41689718'
  AND NOT EXISTS (SELECT 1 FROM wishlists w WHERE w.user_id = u.user_id);

/* ================================
   STORE (singleton ID=1)
   ================================ */
INSERT INTO stores (id, store_name, address, city,phone, facebook, instagram, twitter)
SELECT 1, 'Shoppify', 'Talcahuano 5123', 'Mar del Plata', '2236057991',
       'https://facebook.com/shoppify', 'https://instagram.com/shoppify', 'https://x.com/shoppify'
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
SELECT 'Electronica', 'https://images.unsplash.com/photo-1562408590-e32931084e23?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8ZWxlY3Ryb25pY3xlbnwwfHwwfHx8MA%3D%3D'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Electronica');

INSERT INTO categories (name, img_url)
SELECT 'Zapatillas', 'https://images.unsplash.com/photo-1588361861040-ac9b1018f6d5?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8Y2FsemFkb3xlbnwwfHwwfHx8MA%3D%3D'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Zapatillas');


INSERT INTO categories (name, img_url)
SELECT 'Ropa', 'https://images.unsplash.com/photo-1445205170230-053b83016050?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8N3x8Y2xvdGhlc3xlbnwwfHwwfHx8MA%3D%3D'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Ropa');

INSERT INTO categories (name, img_url)
SELECT 'Home', 'https://images.unsplash.com/photo-1480074568708-e7b720bb3f09?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8aG9tZXxlbnwwfHwwfHx8MA%3D%3D'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Home');

-- Extra category used in mappings
INSERT INTO categories (name, img_url)
SELECT 'Accesorios', 'https://plus.unsplash.com/premium_photo-1681276170683-706111cf496e?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MXx8YWNjZXNvcmllc3xlbnwwfHwwfHx8MA%3D%3D'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Accesorios');

/* ================================
   PRODUCTOS
   ================================ */

-- 1
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Audífonos Deportivos Mini', 'Audífonos in-ear con ajuste deportivo, resistencia al sudor y micrófono integrado. Batería de 10 horas y control táctil sencillo.', 4599.00, 32000.00, 'AC01801', '100000000001', 'SoundBrand', 'https://images.unsplash.com/photo-1739764574592-1dcd5d978a53?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8QXVkJUMzJUFEZm9ub3MlMjBEZXBvcnRpdm9zJTIwTWluaXxlbnwwfHwwfHx8MA%3D%3D', 60, 45, 5
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'AC01801');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'AC01801' AND c.name = 'Electronica'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 2
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Cámara Deportiva 4K', 'Cámara de acción 4K con estabilización electrónica, sumergible y accesorios para casco y bici.', 19999.00, 160000.00, 'CAM01901', '100000000002', 'ActionCam', 'https://images.unsplash.com/photo-1583773393757-949c61acb868?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8N3x8QyVDMyVBMW1hcmElMjBEZXBvcnRpdmElMjA0S3xlbnwwfHwwfHx8MA%3D%3D', 18, 26, 10
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'CAM01901');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'CAM01901' AND c.name = 'Electronica'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 3
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Auricular Over-Ear Premium', 'Auricular circumaural con drivers de 40mm y cancelación pasiva, ideal para estudio y viajes.', 10999.00, 80000.00, 'AH02001', '100000000003', 'SoundBrand', 'https://media.istockphoto.com/id/1366982098/es/foto/auriculares-inal%C3%A1mbricos-sobre-la-oreja-cuero-gris-aislado-sobre-fondo-blanco-con-maqueta-de.webp?a=1&b=1&s=612x612&w=0&k=20&c=iDaEPCUaKzU6vQxxlkY1qsPlZJHVqEmXurSxIO3FbYc=', 22, 30, 0
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'AH02001');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'AH02001' AND c.name = 'Electronica'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 4
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Cargador Rápido USB-C 65W', 'Cargador compacto 65W con USB-C PD, ideal para notebooks ligeros y smartphones.', 2999.00, 22000.00, 'CH02101', '100000000004', 'ChargePro', 'https://images.unsplash.com/photo-1618911138919-dcabd0bd6108?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTh8fENhcmdhZG9yJTIwUiVDMyVBMXBpZG8lMjBVU0ItQyUyMDY1V3xlbnwwfHwwfHx8MA%3D%3D', 120, 600, 0
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'CH02101');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'CH02101' AND c.name = 'Electronica'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 5
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Soporte Laptop Ajustable', 'Soporte para laptop con ángulo regulable y ventilación para mejorar la disipación de calor.', 2499.00, 18000.00, 'SP02201', '100000000005', 'OfficeGear', 'https://media.istockphoto.com/id/1125614886/es/foto/pie-soporte-de-port%C3%A1til-con-notebook-en-el-escritorio.webp?a=1&b=1&s=612x612&w=0&k=20&c=bjDYGXWi491LJ1dU7IfVn-tZS_a9a-puw9z4DAbDvjQ=', 80, 150, 0
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'SP02201');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'SP02201' AND c.name = 'Electronica'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 6
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Smart Plug WiFi', 'Enchufe inteligente con temporizador y compatibilidad con asistentes de voz.', 1999.00, 15000.00, 'SP02301', '100000000006', 'HomeIoT', 'https://images.unsplash.com/photo-1758640265844-5da0c0a4b896?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8U21hcnQlMjBQbHVnJTIwV2lGaXxlbnwwfHwwfHx8MA%3D%3D', 200, 220, 0
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'SP02301');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'SP02301' AND c.name = 'Home'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 7
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Altavoz Inteligente Mini', 'Altavoz inteligente compacto con asistente por voz y buena reproducción para ambientes pequeños.', 3999.00, 30000.00, 'AS02401', '100000000007', 'SoundBrand', 'https://images.unsplash.com/photo-1610563634205-79b98fc239e6?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8QWx0YXZveiUyMEludGVsaWdlbnRlJTIwTWluaXxlbnwwfHwwfHx8MA%3D%3D', 85, 90, 0
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'AS02401');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'AS02401' AND c.name = 'Electronica'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 8
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Base de Carga Wireless 15W', 'Base inalámbrica de carga rápida 15W compatible con la mayoría de smartphones modernos.', 2199.00, 17000.00, 'BQ02501', '100000000008', 'ChargePro', 'https://images.unsplash.com/photo-1763543037341-acb3594a09d8?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8QmFzZSUyMGRlJTIwQ2FyZ2ElMjBXaXJlbGVzcyUyMDE1V3xlbnwwfHwwfHx8MA%3D%3D', 140, 210, 0
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'BQ02501');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'BQ02501' AND c.name = 'Electronica'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 9
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Mochila para Laptop 15"', 'Mochila urbana con compartimento acolchado para notebook y múltiples bolsillos organizadores.', 6599.00, 38000.00, 'BG02601', '100000000009', 'CityPack', 'https://images.unsplash.com/photo-1585501954260-372cec60d355?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8TW9jaGlsYSUyMHBhcmElMjBMYXB0b3AlMjAxNSUyMnxlbnwwfHwwfHx8MA%3D%3D', 55, 140, 5
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'BG02601');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'BG02601' AND c.name = 'Accesorios'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 10
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Batería Externa 20.000mAh', 'Powerbank con doble salida USB y protección contra sobrecargas.', 4799.00, 36000.00, 'PB02701', '100000000010', 'PowerMax', 'https://media.istockphoto.com/id/1453393171/es/foto/powerbank-nuevo-potente-color-oscuro-con-dos-entradas-usb-sobre-un-fondo-blanco-ilustraci%C3%B3n.webp?a=1&b=1&s=612x612&w=0&k=20&c=gu3lBUwaytp0K4Q-7qwaFsxQ0wcA8wUM0CDbRNJiK0Q=', 160, 540, 0
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'PB02701');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'PB02701' AND c.name = 'Electronica'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 11
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Remera Técnica Running', 'Remera deportiva con tejido transpirable y secado rápido, ideal para running.', 2699.00, 16000.00, 'RT02801', '100000000011', 'SportWear', 'https://images.unsplash.com/photo-1637712181274-e3e268d248f6?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8NHx8UmVtZXJhJTIwVCVDMyVBOWNuaWNhJTIwUnVubmluZ3xlbnwwfHwwfHx8MA%3D%3D', 140, 420, 10
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'RT02801');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'RT02801' AND c.name = 'Ropa'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 12
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Short de Entrenamiento', 'Short ligero con cintura elástica y bolsillos ocultos para llaves.', 1899.00, 11000.00, 'SH02901', '100000000012', 'SportWear', 'https://images.unsplash.com/photo-1762744826900-ff6cdf807d79?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8U2hvcnQlMjBkZSUyMEVudHJlbmFtaWVudG98ZW58MHx8MHx8fDA%3D', 130, 260, 0
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'SH02901');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'SH02901' AND c.name = 'Ropa'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 13
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Pantalón Deportivo Largo', 'Pantalón con tejido térmico y ajuste ceñido en tobillos, pensado para entrenamiento en clima frío.', 3999.00, 28000.00, 'PD03001', '100000000013', 'SportWear', 'https://images.unsplash.com/photo-1580764194528-6a4158e3dab0?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MjB8fFBhbnRhbCVDMyVCM24lMjBEZXBvcnRpdm8lMjBMYXJnb3xlbnwwfHwwfHx8MA%3D%3D', 90, 120, 15
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'PD03001');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'PD03001' AND c.name = 'Ropa'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 14
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Zapatillas Urbanas Casual', 'Zapatillas para uso diario con suela acolchada y diseño minimalista.', 7499.00, 50000.00, 'SN03101', '100000000014', 'FootBrand', 'https://media.istockphoto.com/id/2215077612/es/foto/casual-shoes-isolated-on-white-background-sport-running-sneakers.webp?a=1&b=1&s=612x612&w=0&k=20&c=GxPwx1hfnMBRt7loTqxVgVkvrNZEjFzbY0Sk0rseZEQ=', 45, 210, 10
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'SN03101');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'SN03101' AND c.name = 'Zapatillas'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 15
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Zapatillas Trail Pro', 'Zapatillas de trail con agarre avanzado y refuerzo en puntera para terrenos irregulares.', 9999.00, 72000.00, 'SN03201', '100000000015', 'TrailMaster', 'https://images.unsplash.com/photo-1563635419376-78d400e5588e?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8WmFwYXRpbGxhcyUyMFRyYWlsJTIwUHJvfGVufDB8fDB8fHww', 38, 80, 5
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'SN03201');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'SN03201' AND c.name = 'Zapatillas'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 16
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Camisa Casual Hombre', 'Camisa de algodón con corte moderno y botones resistentes.', 3299.00, 22000.00, 'CM03301', '100000000016', 'FashionBrand', 'https://images.unsplash.com/photo-1618088129969-bcb0c051985e?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8Q2FtaXNhJTIwQ2FzdWFsJTIwSG9tYnJlfGVufDB8fDB8fHww', 70, 90, 0
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'CM03301');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'CM03301' AND c.name = 'Ropa'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 17
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Buzo con Capucha', 'Buzo de algodón con capucha ajustable y bolsillos frontales tipo canguro.', 5499.00, 42000.00, 'BZ03401', '100000000017', 'FashionBrand', 'https://media.istockphoto.com/id/154960461/es/foto/sudor-camisa-roja-sobre-fondo-blanco.webp?a=1&b=1&s=612x612&w=0&k=20&c=1H2ZjH8Lnt-C20PRkFvNyWfQUkGVLw9zNLgEIza8Efo=', 60, 75, 20
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'BZ03401');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'BZ03401' AND c.name = 'Ropa'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 18
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Campera Softshell Mujer', 'Campera softshell ligera, cortaviento y repelente al agua.', 8999.00, 65000.00, 'JK03501', '100000000018', 'FashionBrand', 'https://media.istockphoto.com/id/1412625760/es/foto/mujer-joven-con-mochila-elegante-el-d%C3%ADa-de-oto%C3%B1o-espacio-para-mensajes-de-texto.webp?a=1&b=1&s=612x612&w=0&k=20&c=TozcpQxov69x-rqV2th3QzcYiiEaTneTVr8vEBf3Ihw=', 35, 40, 15
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'JK03501');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'JK03501' AND c.name = 'Ropa'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 19
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Smartwatch Lite', 'Reloj deportivo con notificaciones y seguimiento de actividades básicas.', 8999.00, 65000.00, 'SW03601', '100000000019', 'TechBrand', 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=500&auto=format&fit=crop&q=60', 50, 110, 10
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'SW03601');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'SW03601' AND c.name = 'Electronica'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 20
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Correa Deportiva para Smartwatch', 'Correa de silicona hipoalergénica, compatible con modelos comunes.', 499.00, 3500.00, 'CR03701', '100000000020', 'AccessoriesCo', 'https://images.unsplash.com/photo-1719523677277-ffdf2113ef62?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8Q29ycmVhJTIwRGVwb3J0aXZhJTIwcGFyYSUyMFNtYXJ0d2F0Y2h8ZW58MHx8MHx8fDA%3D', 300, 95, 0
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'CR03701');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'CR03701' AND c.name = 'Accesorios'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 21
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Alfombra de Yoga Antideslizante', 'Esterilla con textura antideslizante y 6mm de grosor para mayor confort.', 2199.00, 16000.00, 'YG03801', '100000000021', 'FitGear', 'https://media.istockphoto.com/id/600372270/es/foto/estera-de-yoga-verde-y-sobre-fondo-blanco.webp?a=1&b=1&s=612x612&w=0&k=20&c=AflSU7jkXZDTYVXms1840UcJpr3bc9b_I1QQtN1lolY=', 95, 70, 5
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'YG03801');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'YG03801' AND c.name = 'Accesorios'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 22
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Set de Cuchillos de Cocina', 'Set de 6 cuchillos de acero inoxidable con mango ergonómico y soporte magnético.', 7499.00, 52000.00, 'CK03901', '100000000022', 'KitchenPro', 'https://images.unsplash.com/photo-1609467334293-030ac6448fd8?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8U2V0JTIwZGUlMjBDdWNoaWxsb3MlMjBkZSUyMENvY2luYXxlbnwwfHwwfHx8MA%3D%3D', 40, 18, 0
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'CK03901');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'CK03901' AND c.name = 'Home'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 23
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Lámpara de Mesa LED', 'Lámpara con brillo regulable y puerto USB integrado para carga.', 3199.00, 25000.00, 'LM04001', '100000000023', 'HomeLight', 'https://images.unsplash.com/photo-1591445245952-4df9a055e19f?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8TCVDMyVBMW1wYXJhJTIwZGUlMjBNZXNhJTIwTEVEfGVufDB8fDB8fHww', 70, 33, 0
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'LM04001');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'LM04001' AND c.name = 'Home'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 24
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Hervidor Eléctrico 1.7L', 'Hervidor rápido con apagado automático y filtro desincrustante.', 3999.00, 30000.00, 'HV04101', '100000000024', 'HomeAppliance', 'https://media.istockphoto.com/id/2161643846/es/foto/hervidor-el%C3%A9ctrico-negro-de-agua-caliente-aislada-sobre-fondo-blanco-con-trayectoria-de-recorte.webp?a=1&b=1&s=612x612&w=0&k=20&c=MMTXVwPpTYc-hIp7mLoOMIuindWUgIOTRP1CkZ_HAZw=', 44, 28, 0
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'HV04101');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'HV04101' AND c.name = 'Home'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 25
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Set de Vasos Térmicos (6)', 'Juego de 6 vasos térmicos de acero inoxidable para bebidas calientes y frías.', 2899.00, 21000.00, 'VT04201', '100000000025', 'KitchenPro', 'https://media.istockphoto.com/id/2148321498/es/foto/dos-tazas-de-papel-blanco-para-caf%C3%A9-caliente-para-llevar-taza-de-caf%C3%A9-para-bebidas-calientes.webp?a=1&b=1&s=612x612&w=0&k=20&c=f2dmRQAXrzWteEQ3A01k682F9eCCOuHtDwzr8tUhQu8=', 120, 65, 0
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'VT04201');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'VT04201' AND c.name = 'Home'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 26
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Sábana King 200 hilos', 'Juego de sábanas 100% algodón peinado, tacto suave y alta durabilidad.', 7999.00, 60000.00, 'SB04301', '100000000026', 'HomeTextile', 'https://media.istockphoto.com/id/1854148520/es/foto/cama-blanca-con-almohadas-grandes.webp?a=1&b=1&s=612x612&w=0&k=20&c=EImQ6hsoIbXPbqu_0WedVKyFr9Q8Kq32vwWTFX6xsb0=', 30, 12, 5
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'SB04301');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'SB04301' AND c.name = 'Home'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 27
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Gorro de Lana Unisex', 'Gorro cálido de lana acrílica con forro interior suave.', 999.00, 7000.00, 'GR04401', '100000000027', 'WinterWear', 'https://images.unsplash.com/photo-1645475401635-88f088f224d6?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8R29ycm8lMjBkZSUyMExhbmElMjBVbmlzZXh8ZW58MHx8MHx8fDA%3D', 220, 320, 0
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'GR04401');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'GR04401' AND c.name = 'Accesorios'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 28
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Cinturón de Cuero', 'Cinturón clásico de cuero genuino con hebilla metálica resistente.', 2499.00, 18000.00, 'CT04501', '100000000028', 'StyleBrand', 'https://images.unsplash.com/photo-1664286074176-5206ee5dc878?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8Q2ludHVyJUMzJUIzbiUyMGRlJTIwQ3Vlcm98ZW58MHx8MHx8fDA%3D', 150, 270, 0
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'CT04501');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'CT04501' AND c.name = 'Accesorios'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 29
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Bolso Bandolera Mujer', 'Bolso compacto con cierre y compartimentos internos para organización.', 5599.00, 42000.00, 'BB04601', '100000000029', 'StyleBrand', 'https://media.istockphoto.com/id/2171309410/es/foto/mujer-con-camisa-azul-y-bolso-de-hombro-blanco-de-pie-casualmente.webp?a=1&b=1&s=612x612&w=0&k=20&c=p7s58kN8AfNd73rTjRyPcGZxg_YjOQH6vgkThiMq4dw=', 48, 70, 10
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'BB04601');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'BB04601' AND c.name = 'Accesorios'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 30
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Parasol para Auto', 'Parasol plegable con protección UV para ventanas laterales y parabrisas.', 1299.00, 9000.00, 'PS04701', '100000000030', 'AutoCare', 'https://media.istockphoto.com/id/1627256738/es/foto/una-sombrilla-unida-al-parabrisas-de-un-autom%C3%B3vil.webp?a=1&b=1&s=612x612&w=0&k=20&c=Lf10NmH7xSHdh_PSciqB5cYipC4GFL4UlPUGI0hoMKM=', 210, 75, 0
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'PS04701');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'PS04701' AND c.name = 'Accesorios'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 31
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Monitor 24" Full HD', 'Monitor LED 24 pulgadas con resolución Full HD y entrada HDMI.', 28999.00, 230000.00, 'MN04801', '100000000031', 'VisionPlus', 'https://media.istockphoto.com/id/2200802257/es/foto/smart-monitor-with-blank-screen-isolated-on-white-background.webp?a=1&b=1&s=612x612&w=0&k=20&c=gGuyMwWs_9yb6SEx_WdPPTboSsQBChAch89PA9CwBg0=', 24, 10, 8
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'MN04801');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'MN04801' AND c.name = 'Electronica'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 32
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Impresora Multifunción', 'Impresora multifunción con conectividad WiFi y copiadora, ideal para oficina en casa.', 45999.00, 360000.00, 'IP04901', '100000000032', 'OfficeGear', 'https://images.unsplash.com/photo-1650094980833-7373de26feb6?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8N3x8SW1wcmVzb3JhJTIwTXVsdGlmdW5jaSVDMyVCM258ZW58MHx8MHx8fDA%3D', 12, 6, 12
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'IP04901');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'IP04901' AND c.name = 'Electronica'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 33
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Soporte TV de Pared', 'Soporte para TV ajustable, apto para pantallas hasta 55".', 8999.00, 70000.00, 'ST05001', '100000000033', 'VisionPlus', 'https://media.istockphoto.com/id/1802444442/es/foto/pantalla-led-giratoria-de-soporte-de-tv-perfil-cierres-rojos-en-forma-de-x-color-negro.webp?a=1&b=1&s=612x612&w=0&k=20&c=mb9BzZwGsIJqTYrKL8KJIeBIoTJlWTaN7ICxkNtWH1k=', 40, 22, 0
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'ST05001');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'ST05001' AND c.name = 'Home'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 34
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Filtro de Agua para Grifo', 'Filtro compacto de carbón activo para reducir sabor y olor del agua de consumo.', 3499.00, 26000.00, 'FT05101', '100000000034', 'HomeHealth', 'https://images.unsplash.com/photo-1616761872827-cf5d7aa1d5b4?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8RmlsdHJvJTIwZGUlMjBBZ3VhJTIwcGFyYSUyMEdyaWZvfGVufDB8fDB8fHww', 90, 40, 0
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'FT05101');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'FT05101' AND c.name = 'Home'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 35
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Cuchara Medidora Set', 'Set de cucharas medidoras inox con gancho para colgar.', 699.00, 5000.00, 'CM05201', '100000000035', 'KitchenPro', 'https://images.unsplash.com/photo-1630623108935-73ef0cc4f7eb?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8Q3VjaGFyYSUyME1lZGlkb3JhJTIwU2V0fGVufDB8fDB8fHww', 300, 95, 0
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'CM05201');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'CM05201' AND c.name = 'Home'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 36
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Lentes Gamer Antirreflejo', 'Gafas con filtro de luz azul para proteger durante largas sesiones frente al monitor.', 2999.00, 22000.00, 'GL05301', '100000000036', 'StyleBrand', 'https://plus.unsplash.com/premium_photo-1733892954454-82800cb76845?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MXx8TGVudGVzJTIwR2FtZXIlMjBBbnRpcnJlZmxlam98ZW58MHx8MHx8fDA%3D', 120, 210, 0
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'GL05301');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'GL05301' AND c.name = 'Accesorios'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 37
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Cámara Web Full HD', 'Cámara web 1080p con micrófono integrado y autoenfoque.', 3699.00, 28000.00, 'CW05401', '100000000037', 'CamNet', 'https://images.unsplash.com/photo-1691215415846-676d3dc8d6d6?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTl8fEMlQzMlQTFtYXJhJTIwV2ViJTIwRnVsbCUyMEhEfGVufDB8fDB8fHww', 95, 50, 0
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'CW05401');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'CW05401' AND c.name = 'Electronica'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 38
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Router WiFi AC Dual Band', 'Router doméstico AC dual band con buen alcance y múltiples antenas.', 6999.00, 54000.00, 'RT05501', '100000000038', 'NetGear', 'https://images.unsplash.com/photo-1516044734145-07ca8eef8731?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8N3x8Um91dGVyJTIwV2lGaSUyMEFDJTIwRHVhbCUyMEJhbmR8ZW58MHx8MHx8fDA%3D', 48, 22, 0
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'RT05501');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'RT05501' AND c.name = 'Electronica'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 39
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Micrófono USB Condensador', 'Micrófono USB ideal para streaming, podcast y home studio.', 7499.00, 56000.00, 'MC05601', '100000000039', 'StudioPro', 'https://images.unsplash.com/photo-1718664485620-0e0a2f781120?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8TWljciVDMyVCM2Zvbm8lMjBVU0IlMjBDb25kZW5zYWRvcnxlbnwwfHwwfHx8MA%3D%3D', 32, 14, 0
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'MC05601');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'MC05601' AND c.name = 'Electronica'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 40
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Cafetera de Émbolo 8 tazas', 'Cafetera prensa francesa con jarra de vidrio templado y filtro inox.', 2499.00, 19000.00, 'CF05701', '100000000040', 'HomeAppliance', 'https://images.unsplash.com/photo-1587364139411-bc94b65ee7bc?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTZ8fENhZmV0ZXJhJTIwZGUlMjAlQzMlODltYm9sbyUyMDglMjB0YXphc3xlbnwwfHwwfHx8MA%3D%3D', 70, 160, 0
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'CF05701');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'CF05701' AND c.name = 'Home'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 41
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Plancha Vertical de Vapor', 'Plancha vertical portátil para ropa con tanque removible y potencia de vapor constante.', 7999.00, 63000.00, 'PV05801', '100000000041', 'HomeAppliance', 'https://media.istockphoto.com/id/1149083317/es/foto/vista-recortada-de-mujer-joven-sosteniendo-hierro-humeante-en-la-mano.webp?a=1&b=1&s=612x612&w=0&k=20&c=IlnPucSAqupmJk08YAGlXR9KLC-cAHDsyDtPVcS-4Bo=', 28, 9, 12
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'PV05801');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'PV05801' AND c.name = 'Home'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 42
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Kit Herramientas Básico', 'Juego de herramientas esencial con destornilladores, alicates y llaves.', 3599.00, 27000.00, 'HK05901', '100000000042', 'ToolBox', 'https://images.unsplash.com/photo-1620825141088-a824daf6a46b?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8S2l0JTIwSGVycmFtaWVudGFzJTIwQiVDMyVBMXNpY298ZW58MHx8MHx8fDA%3D', 150, 40, 0
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'HK05901');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'HK05901' AND c.name = 'Home'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 43
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Cámara Instantánea Compacta', 'Cámara instantánea para fotos formato bolsillo con film incluido.', 8999.00, 72000.00, 'CI06001', '100000000043', 'PhotoFun', 'https://images.unsplash.com/photo-1586437855769-01f981e0576b?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8QyVDMyVBMW1hcmElMjBJbnN0YW50JUMzJUExbmVhJTIwQ29tcGFjdGF8ZW58MHx8MHx8fDA%3D', 26, 12, 0
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'CI06001');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'CI06001' AND c.name = 'Electronica'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 44
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Set de Toallas (2)', 'Toallas de algodón absorbente, pack de 2 en tamaño estándar', 2599.00, 20000.00, 'TW06101', '100000000044', 'HomeTextile', 'https://images.unsplash.com/photo-1639298107786-fc9e5880016f?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8N3x8U2V0JTIwZGUlMjBUb2FsbGFzfGVufDB8fDB8fHww', 95, 32, 0
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'TW06101');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'TW06101' AND c.name = 'Home'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 45
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Pulsera Fitness', 'Pulsera con monitor de ritmo cardíaco y contador de pasos.', 4699.00, 35000.00, 'PF06201', '100000000045', 'FitBand', 'https://images.unsplash.com/photo-1510017803434-a899398421b3?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTJ8fFB1bHNlcmElMjBGaXRuZXNzfGVufDB8fDB8fHww', 78, 150, 10
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'PF06201');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'PF06201' AND c.name = 'Electronica'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 46
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Billetera Minimalista', 'Billetera compacta con protector RFID y varios compartimentos para tarjetas.', 1999.00, 15000.00, 'BW06301', '100000000046', 'StyleBrand', 'https://images.unsplash.com/photo-1620109433606-a7dfa6107d28?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8QmlsbGV0ZXJhJTIwTWluaW1hbGlzdGF8ZW58MHx8MHx8fDA%3D', 220, 180, 0
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'BW06301');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'BW06301' AND c.name = 'Accesorios'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 47
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Candado de Combinación', 'Candado de combinación para mochilas y equipaje, resistente y ligero.', 699.00, 5000.00, 'CD06401', '100000000047', 'SafeLock', 'https://media.istockphoto.com/id/185234890/es/foto/cerradura-de-combinaci%C3%B3n.webp?a=1&b=1&s=612x612&w=0&k=20&c=37o2L9cFJzZBvd3I4xwyk8u6KMvaab_3Ly2d67PcLlg=', 400, 90, 0
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'CD06401');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'CD06401' AND c.name = 'Accesorios'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 48
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Manta Polar 150x200', 'Manta polar suave, ideal para el frío en el hogar.', 3199.00, 24000.00, 'MT06501', '100000000048', 'HomeTextile', 'https://media.istockphoto.com/id/651177498/es/foto/manta-con-cinta-y-gracias-etiqueta-regalo.webp?a=1&b=1&s=612x612&w=0&k=20&c=mI64Nfcx2leE7VmnQc93YlwWjvXn0xb00p3mDVg9S0k=', 60, 20, 0
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'MT06501');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'MT06501' AND c.name = 'Home'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 49
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Gorra Visera Plana', 'Gorra casual con visera plana y ajuste trasero.', 899.00, 6500.00, 'GP06601', '100000000049', 'StreetWear', 'https://media.istockphoto.com/id/1439882514/es/foto/joven-euroasi%C3%A1tica-poni%C3%A9ndose-una-gorra-de-pelota-vista-a%C3%A9rea.webp?a=1&b=1&s=612x612&w=0&k=20&c=TWLONxI2-Qv-1NhSYdUD3cILKioNgnDZJAOTzRxbULo=', 320, 460, 0
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'GP06601');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'GP06601' AND c.name = 'Accesorios'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- 50
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Altavoz Portátil Outdoor', 'Altavoz resistente al agua con autonomía larga y gancho para colgar.', 7499.00, 56000.00, 'AS06701', '100000000050', 'SoundBrand', 'https://images.unsplash.com/photo-1758996543028-a84902b022cb?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8QWx0YXZveiUyMFBvcnQlQzMlQTF0aWwlMjBPdXRkb29yfGVufDB8fDB8fHww', 46, 88, 0
WHERE NOT EXISTS (SELECT 1 FROM products WHERE sku = 'AS06701');

INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'AS06701' AND c.name = 'Electronica'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);