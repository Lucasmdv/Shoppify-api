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
   DEMO USERS (CLIENT & ADMIN)
   ================================ */
INSERT INTO clients (client_first_name, client_last_name, client_dni, client_phone, client_img)
SELECT 'Test', 'Cliente', '40289718', '2232057591', 'https://img.freepik.com/foto-gratis/joven-hombre-barbudo-camisa-rayas_273609-5677.jpg?semt=ais_hybrid&w=740&q=80'
WHERE NOT EXISTS (SELECT 1 FROM clients WHERE client_dni = '40289718');

INSERT INTO clients (client_first_name, client_last_name, client_dni, client_phone, client_img)
SELECT 'Test', 'Admin', '41689718', '2232057295', 'https://media.istockphoto.com/id/1090878494/es/foto/retrato-de-joven-sonriente-a-hombre-guapo-en-camiseta-polo-azul-aislado-sobre-fondo-gris-de.jpg?s=612x612&w=0&k=20&c=dHFsDEJSZ1kuSO4wTDAEaGOJEF-HuToZ6Gt-E2odc6U='
WHERE NOT EXISTS (SELECT 1 FROM clients WHERE client_dni = '41689718');


INSERT INTO credentials (username, email, password, user_id)
SELECT 'test.client', 'client@client', '$2a$10$ETW1zaXw0ZHn2rAMvfuwE.4dF6bl/g5Gr42GNXV5uZz./moucSZ3i', c.client_id
FROM clients c
WHERE c.client_dni = '40289718'
  AND NOT EXISTS (SELECT 1 FROM credentials WHERE email = 'client@client');

INSERT INTO credentials (username, email, password, user_id)
SELECT 'test.admin', 'admin@admin', '$2a$10$ETW1zaXw0ZHn2rAMvfuwE.4dF6bl/g5Gr42GNXV5uZz./moucSZ3i', c.client_id
FROM clients c
WHERE c.client_dni = '41689718'
  AND NOT EXISTS (SELECT 1 FROM credentials WHERE email = 'admin@admin');

INSERT INTO credentials_roles (credential_id, role_id)
SELECT cred.id, r.id
FROM credentials cred, roles r
WHERE cred.email = 'client@client'
  AND r.name = 'CLIENT'
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
   PRODUCTS (3 base + 14 adicionales)
   ================================ */
-- Base (por si aún no estaban)
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Teléfono Inteligente X', 'Smartphone de última generación con pantalla AMOLED nítida, procesador de alto rendimiento y triple cámara con IA para fotos claras en cualquier situación. Incluye batería de larga duración, carga rápida y conectividad 5G para trabajo y entretenimiento sin interrupciones.', 589999.00, 520000.00, 'SP001', 'SP001BAR', 'TechBrand', 'https://images.unsplash.com/photo-1598327106026-d9521da673d1?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MzJ8fHNtYXJ0cGhvbmV8ZW58MHx8MHx8fDA%3D', 10, 320, 10
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Teléfono Inteligente X');

INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Portátil Pro', 'Portátil profesional con chasis de aluminio, procesador de 12ª generación y 32 GB de RAM que permiten ejecutar software exigente sin demoras. Integra pantalla 4K calibrada, SSD NVMe de 1 TB y sistema de enfriamiento silencioso ideal para desarrollo, diseño y edición.', 1250000.00, 1120000.00, 'LP002', 'LP002BAR', 'TechBrand', 'https://plus.unsplash.com/premium_photo-1711051475117-f3a4d3ff6778?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8bGFwdG9wfGVufDB8fDB8fHww', 7, 74, 5
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Portátil Pro');

INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Camiseta de Algodón', 'Camiseta básica confeccionada con algodón peinado hipoalergénico que mantiene la piel fresca durante todo el día. Tiene costuras reforzadas, cuello redondo suave y un ajuste relajado pensado para uso diario o uniformes.', 14999.00, 9000.00, 'TS003', 'TS003BAR', 'FashionBrand', 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8dHNoaXJ0fGVufDB8fDB8fHww', 50, 610, 15
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Camiseta de Algodón');

-- Adicionales (14)
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Auriculares Inalámbricos', 'Auriculares con cancelación activa de ruido, drivers de alta fidelidad y diseño circumaural que envuelve cómodamente la oreja. Ofrecen hasta 30 horas de autonomía, conectividad multipunto y controles táctiles para música y llamadas.', 87999.00, 62000.00, 'WH004', 'WH004BAR', 'SoundBrand', 'https://images.unsplash.com/photo-1546435770-a3e426bf472b?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8NHx8aGVhZHBob25lc3xlbnwwfHwwfHx8MA%3D%3D', 25, 280, 0
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Auriculares Inalámbricos');

INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Mouse Gamer RGB', 'Mouse ergonómico diseñado para sesiones largas de juego con sensor óptico de 16.000 DPI y switches duraderos. El sistema de iluminación RGB personalizable y los perfiles programables permiten adaptar cada macro a tu estilo.', 32999.00, 21000.00, 'GM005', 'GM005BAR', 'GameTech', 'https://images.unsplash.com/photo-1629121291243-7b5e885cce9b?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Nnx8bW91c2UlMjBnYW1pbmd8ZW58MHx8MHx8fDA%3D', 30, 190, 20
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Mouse Gamer RGB');

INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Zapatillas Deportivas', 'Zapatillas livianas con malla respirable y refuerzos laterales que brindan estabilidad en entrenamientos de alto impacto. La entresuela con amortiguación reactiva y la suela de goma antideslizante aseguran tracción en interiores y exteriores.', 89999.00, 60000.00, 'SN006', 'SN006BAR', 'FootBrand', 'https://images.unsplash.com/photo-1552346154-21d32810aba3?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8N3x8c3BvcnQlMjBzbmVha2Vyc3xlbnwwfHwwfHx8MA%3D%3D', 20, 420, 10
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Zapatillas Deportivas');

INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'TV LED 50 Pulgadas', 'Televisor 4K Ultra HD con panel LED de 50 pulgadas, Dolby Vision y tasa de refresco mejorada para imágenes fluidas. Incluye sistema Smart TV en español, asistentes de voz integrados y múltiples puertos HDMI para consolas y streaming.', 459999.00, 380000.00, 'TV007', 'TV007BAR', 'VisionPlus', 'https://plus.unsplash.com/premium_photo-1681236323432-3df82be0c1b0?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MXx8dHZ8ZW58MHx8MHx8fDA%3D', 12, 140, 12
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'TV LED 50 Pulgadas');

INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Parlante Bluetooth', 'Parlante portátil con carcasa resistente a salpicaduras, radiadores pasivos y sonido 360° claro. Ofrece conexión Bluetooth 5.0 de alcance extendido, manos libres integrado y hasta 12 horas de reproducción continua.', 63999.00, 42000.00, 'BS008', 'BS008BAR', 'SoundBrand', 'https://images.unsplash.com/photo-1531104985437-603d6490e6d4?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8c3BlYWtlcnxlbnwwfHwwfHx8MA%3D%3D', 40, 350, 0
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Parlante Bluetooth');

INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Cafetera Automática', 'Cafetera automática con molinillo integrado, programación diaria y boquilla de vapor para bebidas cremosas. El depósito removible de agua y la limpieza guiada simplifican el mantenimiento en oficinas o hogares ocupados.', 159999.00, 125000.00, 'CM009', 'CM009BAR', 'HomeAppliance', 'https://plus.unsplash.com/premium_photo-1661722983090-11783531c332?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MXx8Y29mZmUlMjBtYWtlcnxlbnwwfHwwfHx8MA%3D%3D', 15, 95, 18
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Cafetera Automática');

INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Teclado Gamer', 'Teclado mecánico con switches táctiles de respuesta rápida, placa superior de aluminio y reposamuñecas magnético. La retroiluminación RGB por tecla y el software de macros permiten personalizar perfiles para cada juego.', 69999.00, 45000.00, 'GK010', 'GK010BAR', 'GameTech', 'https://images.unsplash.com/photo-1637243218672-d338945efdf7?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8N3x8Z2FtaW5nJTIwa2V5Ym9hcmR8ZW58MHx8MHx8fDA%3D', 25, 210, 0
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Teclado Gamer');

INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Campera Impermeable Hombre', 'Campera invernal con membrana impermeable y respirable, relleno térmico sintético y costuras termoselladas para bloquear el viento. Posee capucha ajustable, múltiples bolsillos con cierres impermeables y puños interiores elásticos.', 119999.00, 80000.00, 'JK011', 'JK011BAR', 'FashionBrand', 'https://images.unsplash.com/photo-1611312449408-fcece27cdbb7?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8bWVuJTIwamFja2V0fGVufDB8fDB8fHww', 18, 160, 25
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Campera Impermeable Hombre');

INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Gafas de Sol Mujer', 'Gafas de sol elegantes con montura liviana resistente a impactos y lentes UV400 de alta claridad. Incluyen tratamiento antirreflejo, bisagras metálicas flexibles y estuche rígido para transportar en carteras o mochilas.', 34999.00, 18000.00, 'SG012', 'SG012BAR', 'StyleBrand', 'https://images.unsplash.com/photo-1624545104844-0d342896e7a6?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8c3VuZ2xhc2VzfGVufDB8fDB8fHww', 50, 500, 10
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Gafas de Sol Mujer');

INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Reloj Inteligente Pro', 'Reloj inteligente premium con monitoreo continuo de salud, GPS integrado y más de 100 modos deportivos. Integra pagos sin contacto, asistencia por voz y resistencia al agua 5 ATM para uso diario o entrenamientos.', 219999.00, 165000.00, 'SW013', 'SW013BAR', 'TechBrand', 'https://plus.unsplash.com/premium_photo-1713795721832-0f33126b4abd?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8NXx8c21hcnR3YXRjaHxlbnwwfHwwfHx8MA%3D%3D', 22, 260, 15
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Reloj Inteligente Pro');

/* ================================
INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Tablet Creativa 11', 'Tablet de 11 pulgadas con panel 2K y lápiz óptico incluido. Integra procesador de ocho núcleos, 8 GB de RAM y 256 GB de almacenamiento ampliable para streaming, ilustración y multitarea.', 369999.00, 300000.00, 'TB014', 'TB014BAR', 'InnovaTech', 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8NHx8dGFibGV0fGVufDB8fDB8fHww', 15, 55, 7
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Tablet Creativa 11');

INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Pantalón Jogger Urbano', 'Pantalón jogger de algodón con elastano, cintura ajustable y bolsillos laterales con cierre. El interior perchado brinda abrigo y la silueta cónica permite combinarlo con zapatillas o botas.', 45999.00, 25000.00, 'PJ015', 'PJ015BAR', 'UrbanWear', 'https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8OXx8cGFudHN8ZW58MHx8MHx8fDA%3D', 40, 310, 5
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Pantalón Jogger Urbano');

INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Robot Aspirador Smart', 'Robot aspirador con navegación láser, programación semanal y detección automática de alfombras. Incluye base de autovaciado, control desde app y compatibilidad con asistentes de voz.', 289999.00, 210000.00, 'RB016', 'RB016BAR', 'HomeAppliance', 'https://images.unsplash.com/photo-1580894894514-0b8d4a4e87b4?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8cm9ib3QlMjB2YWN1dW18ZW58MHx8MHx8fDA%3D', 12, 80, 12
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Robot Aspirador Smart');

INSERT INTO products (name, description, price, unit_price, sku, barcode, brand, img_URL, stock_quantity, sold_quantity, discount_percentage)
SELECT 'Mochila Urbana Antirrobo', 'Mochila con tela impermeable, cierres ocultos y puerto USB externo para cargar dispositivos. El espaldar acolchado y los compartimentos organizadores protegen notebooks de hasta 16 pulgadas.', 65999.00, 38000.00, 'BG017', 'BG017BAR', 'CityPack', 'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8YmFja3BhY2t8ZW58MHx8MHx8fDA%3D', 30, 275, 5
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Mochila Urbana Antirrobo');

   PRODUCT ↔ CATEGORY mappings
   ================================ */
-- Teléfono Inteligente X -> Electronica
INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'SP001' AND c.name = 'Electronica'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- Portátil Pro -> Electronica
INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'LP002' AND c.name = 'Electronica'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- Camiseta de Algodón -> Ropa
INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'TS003' AND c.name = 'Ropa'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- Auriculares Inalámbricos -> Electronica
INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'WH004' AND c.name = 'Electronica'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- Mouse Gamer RGB -> Electronica
INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'GM005' AND c.name = 'Electronica'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- Zapatillas Deportivas -> Ropa
INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'SN006' AND c.name = 'Ropa'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- TV LED 50 Pulgadas -> Electronica
INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'TV007' AND c.name = 'Electronica'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- Parlante Bluetooth -> Electronica
INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'BS008' AND c.name = 'Electronica'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- Cafetera Automática -> Home
INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'CM009' AND c.name = 'Home'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- Teclado Gamer -> Electronica
INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'GK010' AND c.name = 'Electronica'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- Campera Impermeable Hombre -> Ropa
INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'JK011' AND c.name = 'Ropa'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- Gafas de Sol Mujer -> Accesorios
INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'SG012' AND c.name = 'Accesorios'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- Reloj Inteligente Pro -> Electronica
INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'SW013' AND c.name = 'Electronica'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- Tablet Creativa 11 -> Electronica
INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'TB014' AND c.name = 'Electronica'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- Pantalón Jogger Urbano -> Ropa
INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'PJ015' AND c.name = 'Ropa'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- Robot Aspirador Smart -> Home
INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'RB016' AND c.name = 'Home'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);

-- Mochila Urbana Antirrobo -> Accesorios
INSERT INTO products_categories (product_id, category_id)
SELECT p.id, c.id FROM products p, categories c
WHERE p.sku = 'BG017' AND c.name = 'Accesorios'
  AND NOT EXISTS (SELECT 1 FROM products_categories pc WHERE pc.product_id = p.id AND pc.category_id = c.id);
