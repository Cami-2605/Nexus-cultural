# Nexus Cultural S.A.S. — Matriz de Trazabilidad de Requisitos (RTM)

> **Documento:** NEXUS-DOC-TRZ-Matriz_Trazabilidad_v1.0  
> **Proyecto:** Nexus Cultural (Ecosistema Móvil y Backend de Comercio Artesanal)  
> **Fecha de Elaboración:** Octubre de 2026  
> **Ubicación Recomendada en Repositorio:** `documentacion/trazabilidad/MATRIZ_TRAZABILIDAD.md`

---

## 1. Matriz de Trazabilidad Integral (Requisitos -> Arquitectura -> Código -> Base de Datos -> Pruebas)

|  ID Req.  | Nombre del Requisito | Proceso | Caso de Uso / Servicio Backend          | Endpoint REST                              | Pantalla / Controlador Frontend                | Entidad JPA / Tabla BD                    | Prueba Automatizada                      |                                       Estado y Hallazgo de Auditoría                                       |
| :-------: | :------------------- | :-----: | :-------------------------------------- | :----------------------------------------- | :--------------------------------------------- | :---------------------------------------- | :--------------------------------------- | :--------------------------------------------------------------------------------------------------------: |
| **RF-01** | Selección de Rol     |   P1    | N/A (Flujo UI)                          | N/A                                        | `role_selection_screen.dart`                   | N/A                                       | `widget_test.dart`                       |                                                 **CUMPLE**                                                 |
| **RF-02** | Solicitud Código OTP |   P1    | `SolicitarOtpUseCase`                   | `POST /api/usuarios/solicitar`             | `login_screen.dart` / `auth_controller.dart`   | `CodigoOtpEntity` (`codigos_otp`)         | `CodigoOtpTest.java`                     |                       **PARCIAL:** Falta rate limit; expone OTP en logs de consola.                        |
| **RF-03** | Validación OTP y JWT |   P1    | `ValidarOtpUseCase`                     | `POST /api/usuarios/verificar`             | `sms_otp_screen.dart` / `auth_controller.dart` | `CodigoOtpEntity` (`codigos_otp`)         | `CodigoOtpTest.java`                     |                     **PARCIAL:** Usa `Random` no seguro; sin bloqueo por fuerza bruta.                     |
| **RF-04** | Registro Comprador   |   P1    | `RegistrarCompradorUseCase`             | `POST /api/compradores`                    | `buyer_registration_flow.dart`                 | `CompradorEntity` (`compradores`)         | `RegistrarCompradorUseCaseTest.java`     |                                                 **CUMPLE**                                                 |
| **RF-05** | Registro Artesano    |   P1    | `RegistrarArtesanoUseCase`              | `POST /api/artesanos`                      | `artisan_registration_flow.dart`               | `ArtesanoEntity` (`artesanos`)            | `RegistrarArtesanoUseCaseTest.java`      |                                                 **CUMPLE**                                                 |
| **RF-06** | Cierre de Sesión     |   P1    | `CerrarSesionUseCase`                   | N/A (Local / JWT stateless)                | `auth_controller.dart`                         | N/A                                       | Pendiente                                |                      **PARCIAL:** Token JWT no se revoca en backend (sin blacklist).                       |
| **RF-07** | Carga Fotos AWS S3   |   P2    | `S3PresignedController`                 | `GET /api/v1/storage/presigned-url`        | `create_product_flow.dart`                     | `ArticuloEntity` (`articulo_imagenes`)    | Pendiente                                |                    **CRÍTICO:** Endpoint público sin autenticación ni límite de tamaño.                    |
| **RF-08** | Publicación Obra     |   P2    | `ArticuloService.publicarArticulo`      | `POST /api/articulos`                      | `create_product_flow.dart`                     | `ArticuloEntity` (`articulos`)            | `ArticuloTest.java`                      |                                                 **CUMPLE**                                                 |
| **RF-09** | Edición de Obra      |   P2    | `ArticuloService.actualizarInventario`  | `PUT /api/articulos/{id}/inventario`       | `create_product_flow.dart`                     | `ArticuloEntity` (`articulos`)            | Pendiente                                |    **CRÍTICO:** El frontend envía `POST /articulos` al editar, duplicando el producto en base de datos.    |
| **RF-10** | Control de Stock     |   P2    | `ArticuloService.reponerStock`          | `PATCH /api/articulos/{id}/reponer`        | `artisan_dashboard_screen.dart`                | `ArticuloEntity` (`articulos`)            | `ArticuloAgregadoTest.java`              |                                           **CUMPLE en Backend**                                            |
| **RF-11** | Búsqueda y Filtro    |   P2    | `CatalogoService.buscarCatalogo`        | `GET /api/catalogo/buscar`                 | `home_screen.dart` / `filters_screen.dart`     | `ArticuloEntity` (`articulos`)            | Pendiente                                |                                                 **CUMPLE**                                                 |
| **RF-12** | Detalle e Historia   |   P2    | `CatalogoService.obtenerArticuloPorId`  | `GET /api/articulos/{id}`                  | `product_detail_screen.dart`                   | `ArticuloEntity` (`articulos`)            | Pendiente                                |                                                 **CUMPLE**                                                 |
| **RF-13** | Carrito de Compras   |   P3    | `CarritoService.agregarItem`            | `POST /api/compradores/{id}/carrito/items` | `cart_screen.dart` / `carrito_service.dart`    | `CarritoItemEntity` (`carrito_items`)     | Pendiente                                |           **CRÍTICO:** Backend duplica filas de ítems existentes en vez de incrementar cantidad.           |
| **RF-14** | Checkout Atómico     |   P3    | `PedidoService.crearPedido`             | `POST /api/pedidos`                        | `payment_method_screen.dart`                   | `PedidoEntity` (`pedidos`)                | `PedidoTest.java`                        |   **CRÍTICO:** Frontend ejecuta bucle unitario 1x1; si falla la red, oculta error y simula pago exitoso.   |
| **RF-15** | Procesamiento Pago   |   P3    | `PedidoService.procesarPago`            | `POST /api/pedidos/pago`                   | `payment_method_screen.dart`                   | `PedidoEntity` (`pedidos`)                | `PedidoAgregadoTest.java`                |                                           **CUMPLE en Backend**                                            |
| **RF-16** | Fechas de Entrega    |   P3    | N/A (Lógica Cliente)                    | N/A                                        | `payment_method_screen.dart`                   | `PedidoEntity.fecha_entrega`              | Pendiente                                |            **DEFECTO:** Suma directa a `now.day` produce días imposibles como "33 de octubre".             |
| **RF-17** | Solicitud Garantía   |   P3    | `PedidoService.solicitarDevolucion`     | `POST /api/pedidos/devolucion`             | `refund_request_screen.dart`                   | `DevolucionEntity` (`devoluciones`)       | `PedidoTest.java`                        |                                           **CUMPLE en Backend**                                            |
| **RF-18** | Resolución Garantía  |   P3    | `PedidoService.resolverDevolucion`      | N/A (Uso interno)                          | `home_screen_admin.dart`                       | `DevolucionEntity` (`devoluciones`)       | Pendiente                                |                                           **CUMPLE en Backend**                                            |
| **RF-19** | Registro Admin       | Transv. | `RegistrarAdministradorUseCase`         | `POST /api/administradores`                | `admin_registration_screen.dart`               | `AdministradorEntity` (`administradores`) | `RegistrarAdministradorUseCaseTest.java` | **CRÍTICO:** `SecurityConfig` tiene el endpoint en `permitAll()`, permitiendo escalamiento de privilegios. |
| **RF-20** | Moderación Obras     | Transv. | `ArticuloService.inactivarArticulo`     | `PATCH /api/articulos/{id}/inactivar`      | `home_screen_admin.dart`                       | `ArticuloEntity` (`articulos`)            | Pendiente                                |                                                 **CUMPLE**                                                 |
| **RF-21** | Gamificación         | Transv. | `LogroService.evaluarLogrosPublicacion` | `GET /api/artesanos/{id}/logros`           | `home_screen_artisan.dart`                     | `ArtesanoEntity.logros`                   | Pendiente                                |                                                 **CUMPLE**                                                 |
| **RF-22** | Versión de App       | Transv. | `AppVersionController`                  | `GET /api/app-version`                     | `version_service.dart`                         | N/A (application.properties)              | Pendiente                                |                                                 **CUMPLE**                                                 |

---

## 2. Recomendaciones de Organización Documental

Para mantener la gobernanza y trazabilidad técnica del repositorio a largo plazo, se recomienda organizar los documentos `.md` generados en la siguiente estructura dentro de la carpeta `../../documentacion`:

```
documentacion/
├── requisitos/
│   └── REQUISITOS_FUNCIONALES.md       <-- Requisitos funcionales detallados (RF-01 a RF-22)
├── base_datos/
│   └── MODELO_BASE_DATOS.md            <-- Diagrama ERD y diccionario de datos PostgreSQL
├── arquitectura/
│   └── ARQUITECTURA_Y_ENDPOINTS.md     <-- Arquitectura hexagonal y contratos de API REST
├── trazabilidad/
│   └── MATRIZ_TRAZABILIDAD.md          <-- Matriz de trazabilidad extremo a extremo
├── manuales/
│   ├── Manual_Usuario_Administrador.pdf
│   ├── Manual_Usuario_Artesano.pdf
│   └── Manual_Usuario_Comprador.pdf
└── despliegue/
    └── Manual_Instalacion.pdf
```
