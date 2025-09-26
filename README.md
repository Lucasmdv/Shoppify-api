# Shoppify 🛒 — *Plataforma integral de e-commerce con gestión avanzada de usuarios y productos.*

**Shoppify** es una aplicación construida con **Spring Boot** que permite gestionar una tienda online completa, incluyendo usuarios y sus permisos, catálogo de productos, ventas, métricas, y compras a proveedores para reabastecimiento de stock.

## ✨ Características principales

- 🔹 Gestión avanzada de usuarios con diferentes roles y permisos.
- 🔹 Catálogo completo de productos con categorías, variantes y reseñas.
- 🔹 Carrito de compras y proceso de checkout optimizado.
- 🔹 Gestión de inventario con alertas de stock bajo.
- 🔹 Compras a proveedores para reabastecimiento automático.
- 🔹 Métricas y analíticas de ventas, productos y clientes.
- 🔹 Sistema de promociones, cupones y descuentos.
- 🔹 Generación de facturas y reportes en PDF.

## 🔧 Diseño y patrones aplicados

Este proyecto tiene un enfoque profesional con el objetivo de aplicar distintos conceptos del ecosistema Spring Boot, como:

- Arquitectura por capas (Controladores, Servicios, Repositorios).
- Patrón DTO (Data Transfer Object) para la transferencia de datos entre capas.
- Mapeo de objetos con MapStruct.
- Validaciones con Hibernate Validator.
- Manejo centralizado de excepciones.
- Autenticación y autorización con Spring Security y JWT.
- API RESTful con HATEOAS para mejorar la navegabilidad.
- Documentación de API con OpenAPI/Swagger.

## 🛠️ Tecnologías utilizadas

| Tecnología              | Descripción                                     |
|-------------------------|-------------------------------------------------|
| Spring Boot 3.2.5       | Framework principal para backend                |
| Java 21                 | Versión del lenguaje                            |
| Spring Data JPA         | Persistencia de datos                           |
| PostgreSQL              | Base de datos principal                         |
| Spring Security         | Autenticación y autorización                    |
| JWT                     | Tokens para autenticación                       |
| Lombok                  | Reducción de código boilerplate                 |
| MapStruct               | Mapeo automático entre objetos                  |
| Hibernate Validator     | Validación de datos                             |
| Hibernate Envers        | Auditoría de entidades                          |
| Thymeleaf               | Motor de plantillas para emails                 |
| OpenAPI/Swagger         | Documentación de API                            |
| Spring HATEOAS          | Enlaces hipermedia para API REST                |
| Docker                  | Contenedorización de la aplicación              |

## 📁 Estructura del proyecto
```plaintext
org.shoppify
│
├── config
│   └── Configuraciones de la aplicación
│
├── controller
│   ├── product
│   │   ├── ProductController
│   │   ├── ProductCategoryController
│   │   ├── ProductVariantController
│   │   ├── ProductReviewController
│   ├── user
│   │   ├── UserController
│   │   ├── AdminController
│   │   ├── CustomerController
│   │   ├── RoleController
│   │   ├── PermissionController
│   ├── order
│   │   ├── OrderController
│   │   ├── OrderItemController
│   │   ├── ShippingController
│   │   ├── PaymentController
│   ├── supplier
│   │   ├── SupplierController
│   │   ├── PurchaseOrderController
│   ├── cart
│   │   ├── CartController
│   │   ├── WishlistController
│   ├── marketing
│   │   ├── PromotionController
│   │   ├── CouponController
│   │   ├── DiscountController
│   ├── analytics
│   │   ├── SalesMetricsController
│   │   ├── CustomerMetricsController
│   │   ├── InventoryMetricsController
│   ├── CategoryController
│   ├── SearchController
│   ├── PdfGeneratorController
│   ├── NotificationController
│   ├── AuditController
│
├── dto
│   ├── request
│   │   ├── product
│   │   ├── user
│   │   ├── order
│   │   ├── supplier
│   │   ├── analytics
│   ├── response
│       ├── product
│       ├── user
│       ├── order
│       ├── supplier
│       ├── analytics
│
├── model
│   ├── assembler
│   ├── entity
│   ├── exception
│   ├── mapper
│   ├── specification
│
├── security
│   ├── controller
│   ├── model
│   ├── service
│   ├── filter
│   ├── config
│
├── service
│   ├── product
│   ├── user
│   ├── order
│   ├── supplier
│   ├── analytics
│   ├── notification
│   ├── payment
│
├── util
│   ├── Clases de utilidad
```

## Authors

- [@Newbie1337x](https://github.com/Newbie1337x)
- [@joacoloool](https://github.com/joacoloool)
- [@Lucasmdv](https://github.com/Lucasmdv)
