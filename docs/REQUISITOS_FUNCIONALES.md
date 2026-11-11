<table width="100%" border="0" cellspacing="0" cellpadding="6" style="border-bottom: 2px solid #994422;">
  <tr>
    <td width="90" align="left" valign="middle">
      <img src="../../imagenes_para_md/logo_Nexus_Cultural.png" alt="Logo Nexus Cultural" width="75" />
    </td>
    <td align="left" valign="middle">
      <strong>NEXUS CULTURAL S.A.S.</strong><br/>
      <em>Especificación Formal de Requisitos Funcionales (RF)</em><br/>
      <small>Código: NEXUS-DOC-RF-v1.0 | Ingeniería de Software III</small>
    </td>
    <td align="right" valign="middle">
      <small><strong>Fecha:</strong> Noviembre 2026<br/><strong>Estado:</strong> Aprobado</small>
    </td>
  </tr>
</table>

<br/>

# Nexus Cultural S.A.S. — Especificación Formal de Requisitos Funcionales (RF)

> **Documento:** NEXUS-DOC-RF-Especificacion_Requisitos_Funcionales_v1.0  
> **Proyecto:** Nexus Cultural (Plataforma Móvil y Ecosistema Digital para Artesanías Ancestrales)  
> **Fecha de Elaboración:** Octubre de 2026  
> **Estado:** Documentación Técnica de Auditoría e Ingeniería de Software  
> **Ubicación Recomendada en Repositorio:** `documentacion/requisitos/REQUISITOS_FUNCIONALES.md`

---

## 1. Introducción y Alcance del Documento

El presente documento formaliza la totalidad de los **Requisitos Funcionales (RF)** correspondientes al ecosistema digital **Nexus Cultural**, articulando las capacidades requeridas para los tres procesos de negocio estratégicos de la organización:

1. **Proceso 1:** Registro, Autenticación y Gestión de Identidad Digital.
2. **Proceso 2:** Publicación, Curaduría y Gestión de Catálogo e Inventario Artesanal.
3. **Proceso 3:** Procesamiento de Pago Seguro, Gestión de Pedidos y Devoluciones Posventa.
4. **Procesos Transversales y Administrativos:** Moderación de Contenido, Gamificación (Logros Culturales), Control de Versiones Móviles y Trazabilidad.

Este artefacto técnico subsana la ausencia documental detectada durante la auditoría externa de calidad y asegura la trazabilidad bidireccional entre las necesidades del negocio, la arquitectura hexagonal en Spring Boot (Backend), la interfaz reactiva en Flutter (Frontend) y las entidades de persistencia en PostgreSQL.

---

## 2. Actores del Sistema

| Actor | Código | Descripción y Perfil |
| :--- | :---: | :--- |
| **Artesano Ancestral** | `ACT-ART` | Adulto mayor o maestro perteneciente a comunidades indígenas/tradicionales colombianas. Requiere interfaces de alta accesibilidad, tipografía legible, comandos guiados por voz y flujos asistidos para inventario y ventas. |
| **Comprador Cultural** | `ACT-CMP` | Usuario final interesado en adquirir piezas artesanales auténticas colombianas, conocer su historia ancestral y realizar pagos digitales seguros con despacho garantizado. |
| **Administrador del Sistema** | `ACT-ADM` | Personal operativo o directivo de Nexus Cultural encargado de la moderación de piezas, validación de artesanos, auditoría de pedidos y supervisión de la plataforma. |
| **Pasarela de Notificaciones** | `ACT-NOT` | Sistema externo (Firebase Cloud Messaging / Firestore Trigger Email) para la expedición de códigos OTP y notificaciones operativas. |
| **Almacenamiento Cloud (S3)** | `ACT-STG` | Proveedor de infraestructura AWS S3 para la custodia segura de material multimedia de alta resolución. |

---

## 3. Especificación Detallada de Requisitos Funcionales

### 3.1. Proceso 1: Registro, Autenticación y Gestión de Identidad

#### RF-01: Selección de Rol y Bienvenida
- **Descripción:** La aplicación móvil debe permitir al usuario seleccionar su perfil de interacción inicial (`Artesano`, `Comprador` o `Administrador`), adaptando la experiencia visual y los flujos de captura de datos de acuerdo con las capacidades del rol.
- **Entradas:** Selección de tarjeta de rol en pantalla de bienvenida.
- **Salidas:** Redirección al flujo de registro específico del rol seleccionado.
- **Criterio de Aceptación (Gherkin):**
  ```gherkin
  Escenario: Selección de rol de artesano
    Dado que un nuevo usuario ingresa por primera vez a Nexus Cultural
    Cuando selecciona la opción "Soy Maestro Artesano"
    Entonces el sistema debe iniciar el flujo adaptado con componentes de accesibilidad auditiva y tipografía grande.
  ```

#### RF-02: Solicitud de Código de Verificación OTP
- **Descripción:** El sistema debe generar un código de un solo uso (OTP) numérico de 6 dígitos con una vigencia exacta de 5 minutos, despachándolo al correo electrónico registrado del usuario para autenticación sin contraseña (Passwordless).
- **Entradas:** Correo electrónico (`email`).
- **Salidas:** Mensaje de confirmación de envío y registro del código en estado `PENDIENTE` en la base de datos.
- **Regla de Negocio:** La generación del OTP debe implementar entropía criptográficamente segura (`SecureRandom`). El endpoint debe limitar las solicitudes a un máximo de 3 peticiones por minuto por dirección IP/correo para evitar saturación y denegación de servicio.

#### RF-03: Validación de Código OTP e Inicio de Sesión
- **Descripción:** El sistema debe verificar el código OTP suministrado por el usuario frente al último código emitido para dicho correo, validando que no esté expirado ni haya sido utilizado previamente.
- **Entradas:** Correo electrónico (`email`), código OTP (`codigo`).
- **Salidas:** Token de autenticación JWT firmado, rol del usuario, UUID de identidad y expiración.
- **Regla de Negocio:**
  - Máximo 3 intentos fallidos consecutivos permitidos; al cuarto intento fallido, el OTP debe invalidarse (`ESTADO_BLOQUEADO`) requiriendo una nueva solicitud.
  - Al validarse exitosamente, el OTP pasa a estado `USADO` de forma atómica y no puede reutilizarse.

#### RF-04: Registro de Perfil de Comprador
- **Descripción:** El sistema debe permitir el registro completo de un nuevo comprador, capturando sus datos de contacto y dirección física de despacho en Colombia.
- **Entradas:** Nombre completo, correo electrónico, departamento, municipio y dirección de entrega.
- **Salidas:** Creación de registro en la tabla `compradores`, asignación de UUID e inicialización de su carrito de compras y lista de favoritos vacíos.

#### RF-05: Registro de Perfil de Artesano y Trayectoria
- **Descripción:** El sistema debe capturar la identidad del artesano, su ubicación geográfica territorial y los antecedentes de su tradición o trayectoria cultural.
- **Entradas:** Nombre completo, correo electrónico, departamento, municipio, dirección de taller, años de experiencia y reconocimientos recibidos.
- **Salidas:** Creación del registro en `artesanos`, asignación de estado inicial y perfil público para catálogo.

#### RF-06: Gestión de Cierre de Sesión y Revocación
- **Descripción:** El usuario debe poder finalizar su sesión activa en el dispositivo móvil, eliminando las credenciales locales y notificando al backend la invalidación del token.
- **Salidas:** Eliminación del token local en almacenamiento seguro (`FlutterSecureStorage`) y redirección a la pantalla de bienvenida.

---

### 3.2. Proceso 2: Publicación y Gestión de Catálogo Artesanal

#### RF-07: Carga y Almacenamiento Seguro de Fotografías (AWS S3)
- **Descripción:** La plataforma debe permitir a los artesanos cargar imágenes de sus obras directamente hacia un bucket privado de AWS S3 mediante URLs prefirmadas (`presigned-url`), reduciendo la sobrecarga de ancho de banda del backend.
- **Entradas:** Extensión de archivo y MIME Type del archivo multimedia.
- **Validaciones:**
  - Solo se admiten extensiones de imagen válidas (`.jpg`, `.jpeg`, `.png`, `.webp`).
  - Tamaño máximo por archivo: 5 MB.
  - La solicitud de URL prefirmada requiere autenticación previa con rol `ROLE_ARTESANO` o `ROLE_ADMINISTRADOR`.
- **Salidas:** `uploadUrl` (S3 PUT presignada válida por 10 minutos) y `publicUrl` definitiva para persistencia.

#### RF-08: Publicación de Nueva Pieza Artesanal
- **Descripción:** El artesano autenticado debe poder publicar una obra en el catálogo cultural, especificando información comercial y el significado ancestral de la creación.
- **Entradas:** Título, descripción/historia cultural, técnica ancestral, stock inicial, categoría/grupo cultural (`Tejido`, `Cerámica`, `Orfebrería`, `Madera`, `Cuero`, `Semillas`), precio en COP y URLs de fotografías cargadas.
- **Salidas:** Creación de la entidad `Articulo` con estado `DISPONIBLE`, persistencia de URLs en `articulo_imagenes` y confirmación visual al artesano.

#### RF-09: Actualización y Edición de Obra 
- **Descripción:** El artesano debe poder modificar el título, descripción, precio y stock de una obra previamente publicada.
- **Entradas:** Identificador único de la obra (`articuloId`), identificador del artesano propietario y campos actualizados.
- **Regla de Negocio:** El sistema debe invocar métodos de actualización idempotentes (`PUT /api/articulos/{id}` o `PUT /api/articulos/{id}/inventario`). Queda terminantemente prohibido utilizar el flujo de creación (`POST /api/articulos`) durante operaciones de edición.

#### RF-10: Control y Reposición de Inventario
- **Descripción:** El sistema debe permitir el ajuste rápido de unidades disponibles (stock) por parte del artesano o automáticamente tras una compra confirmada.
- **Entradas:** `articuloId`, incremento o decremento numérico y motivo del movimiento.
- **Regla de Negocio:** Si el stock llega a 0, el artículo pasa automáticamente a estado `AGOTADO` y no puede agregarse a nuevos carritos.

#### RF-11: Exploración y Búsqueda del Catálogo Cultural
- **Descripción:** Los compradores y visitantes pueden explorar la totalidad de obras disponibles, realizar búsquedas por palabra clave y filtrar por categoría cultural o departamento.
- **Entradas:** Término de búsqueda (`query`) y/o categoría (`grupo`).
- **Salidas:** Lista de artículos disponibles con paginación optimizada (máximo 20 ítems por página).

#### RF-12: Detalle de la Obra y Narrativa Cultural
- **Descripción:** La vista de detalle debe presentar la historia ancestral de la pieza, el perfil verificado del maestro artesano que la elaboró y un desglose transparente del pago directo que recibe la comunidad (mínimo el 90% del valor total).

---

### 3.3. Proceso 3: Carrito, Pagos y Devoluciones

#### RF-13: Gestión Unificada del Carrito de Compras
- **Descripción:** El comprador puede agregar obras al carrito, modificar cantidades y eliminar ítems.
- **Regla de Negocio:**
  - Si un artículo ya se encuentra presente en el carrito del comprador, el sistema debe incrementar la cantidad existente en la misma tupla, sin generar registros duplicados.
  - La cantidad solicitada no puede superar el stock disponible del artículo en tiempo real.

#### RF-14: Creación Integral de Pedido Multiproducto (Checkout Atómico)
- **Descripción:** Al iniciar el proceso de compra, el sistema debe consolidar todos los artículos del carrito dentro de una única orden de pedido (`Pedido`) con sus correspondientes líneas de detalle (`DetallePedidoEntity`).
- **Entradas:** Identificador del comprador, dirección de despacho y lista de ítems del carrito.
- **Regla de Negocio:** La creación del pedido y la reserva de stock deben ejecutarse bajo una transacción atómica (`@Transactional`). Si algún artículo no cuenta con stock suficiente, la transacción completa debe abortarse.

#### RF-15: Procesamiento de Pago y Conciliación
- **Descripción:** El sistema debe registrar la confirmación del pago mediante medios nacionales (Tarjeta, PSE, Nequi/Daviplata).
- **Entradas:** Identificador del pedido, monto cobrado, método de pago y referencia bancaria externa.
- **Validaciones:**
  - El monto reportado por el cliente debe coincidir exactamente con el valor calculado en el backend a partir de los precios unitarios almacenados en base de datos.
  - Al confirmarse el pago, el pedido transiciona a estado `CONFIRMADO` y el carrito del comprador se vacía en la base de datos.

#### RF-16: Cálculo de Fecha Estimada de Entrega
- **Descripción:** El sistema debe proyectar la fecha estimada de entrega sumando días hábiles a la fecha del pedido mediante aritmética de calendarios estándar (`DateTime.add(Duration(days: N))`), garantizando la correcta transición entre meses y años.

#### RF-17: Solicitud de Devolución y Ejercicio de Garantía
- **Descripción:** El comprador debe poder radicar una solicitud de devolución dentro del plazo legal de 5 días hábiles posteriores a la entrega del producto (Ley 1480 de 2011 - Estatuto del Consumidor).
- **Entradas:** `pedidoId`, motivo de la devolución y descripción del estado de la pieza.
- **Regla de Negocio:** El pedido debe encontrarse en estado `ENTREGADO`. No se permite más de una solicitud de devolución por pedido. El pedido pasa a estado `EN_DEVOLUCION`.

#### RF-18: Resolución Administrativa de Devolución
- **Descripción:** El administrador puede aprobar o rechazar una solicitud de devolución radicada. Si es aprobada, el pedido pasa a `DEVUELTO` y se gestiona el reembolso; si es rechazada, retorna a `ENTREGADO`.

---

### 3.4. Procesos Administrativos y Transversales

#### RF-19: Registro Restringido de Administradores
- **Descripción:** La creación de cuentas con privilegios administrativos (`ROLE_ADMINISTRADOR`) solo puede ser ejecutada por un administrador autenticado con nivel `SUPER_ADMIN`.
- **Regla de Seguridad:** El endpoint `POST /api/administradores` debe estar estrictamente protegido bajo `hasRole('ADMINISTRADOR')`.

#### RF-20: Moderación y Estado de Artículos
- **Descripción:** Los administradores tienen la facultad de suspender, inactivar o reactivar artículos del catálogo que no cumplan con las directrices de autenticidad cultural.

#### RF-21: Sistema de Logros y Gamificación Artesanal
- **Descripción:** El sistema debe evaluar automáticamente y otorgar insignias culturales a los artesanos según su nivel de actividad (e.g., "Primer Maestro Creador", "Guardián de la Tradición", "Maestro Consagrado").

#### RF-22: Control de Versiones Móviles y Actualización Forzosa
- **Descripción:** La aplicación móvil debe consultar el endpoint `/api/app-version` en el arranque. Si la versión local es inferior a `minVersion` y `forceUpdate` es verdadero, la aplicación debe desplegar un diálogo modal bloqueante con enlace a la tienda o APK oficial, impidiendo operaciones inseguras con contratos desactualizados.

---

## 4. Matriz de Priorización de Requisitos (MoSCoW)

| Código | Nombre del Requisito | Proceso | Prioridad MoSCoW | Estado Actual |
| :---: | :--- | :---: | :---: | :---: |
| **RF-01** | Selección de Rol y Bienvenida | 1 | **Must Have** | Implementado |
| **RF-02** | Solicitud de OTP (Vía Correo) | 1 | **Must Have** | Implementado  |
| **RF-03** | Validación OTP y Emisión JWT | 1 | **Must Have** | Implementado  |
| **RF-04** | Registro de Comprador | 1 | **Must Have** | Implementado |
| **RF-05** | Registro de Artesano | 1 | **Must Have** | Implementado |
| **RF-06** | Cierre de Sesión Seguro | 1 | **Should Have** | Parcial |
| **RF-07** | Carga de Imágenes a S3 | 2 | **Must Have** | Implementado |
| **RF-08** | Publicación de Artículo | 2 | **Must Have** | Implementado |
| **RF-09** | Edición de Artículo  | 2 | **Must Have** | Implementado |
| **RF-10** | Control de Stock | 2 | **Must Have** | Implementado en backend |
| **RF-11** | Búsqueda y Filtro de Catálogo | 2 | **Must Have** | Implementado |
| **RF-12** | Detalle e Historia Cultural | 2 | **Should Have** | Implementado |
| **RF-13** | Gestión de Carrito sin Duplicados | 3 | **Must Have** | Implementado |
| **RF-14** | Checkout Atómico Multiproducto | 3 | **Must Have** | Implementado |
| **RF-15** | Procesamiento de Pago | 3 | **Must Have** | Implementado en backend |
| **RF-16** | Cálculo de Fechas de Entrega | 3 | **Should Have** | Implementado  |
| **RF-17** | Solicitud de Devolución | 3 | **Must Have** | Implementado en backend |
| **RF-18** | Resolución de Devoluciones | 3 | **Should Have** | Implementado en backend |
| **RF-19** | Alta Restringida de Administrador | Transv. | **Must Have** | Implementado |
| **RF-20** | Moderación de Contenido | Transv. | **Should Have** | Implementado en backend y admin UI |
| **RF-21** | Control de Versiones Móvil | Transv. | **Must Have** | Implementado |


## **Escala MoSCoW**

Es una técnica estándar de priorización de requisitos en Ingeniería de Software y metodologías ágiles (creada por Dai Clegg en Oracle)

| Letra | Significado | Traducción / Interpretación práctica | Ejemplo en Nexus Cultural |
| :---: | :--- | :--- | :--- |
| **M** | **Must have** | Obligatorio / Indispensable. Sin esto, el sistema no sirve o no se puede entregar. | Registro, OTP + JWT, catálogo en S3, carrito, checkout y pago (RF-01 a RF-05, RF-07 a RF-11, RF-13 a RF-15, RF-17, RF-19, RF-21). |
| **o** | *(relleno)* | — | — |
| **S** | **Should have** | Importante / Debería estar. Aporta mucho valor, pero si falla o se ajusta no rompe el corazón del negocio. | Historia cultural detallada (RF-12), fechas estimadas de entrega (RF-16), moderación (RF-20), cierre de sesión (RF-06). |
| **C** | **Could have** | Deseable / Podría tenerlo. Son extras estéticos o de conveniencia si sobra tiempo y presupuesto. | Logros culturales avanzados o mejoras visuales secundarias. |
| **o** | *(relleno)* | — | — |
| **W** | **Won't have (this time)** | No se hará (en esta versión). Queda explícitamente fuera del alcance actual para evitar que el proyecto se desborde. | Facturación electrónica DIAN o integración física en tiempo real con transportadoras. |