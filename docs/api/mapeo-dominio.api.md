# MAPEOS DE DOMINIO A API REST - NEXUS CULTURAL S.A.S.

## PROCESO 1: Registro y Gestión de Identidad

### Mapeo Dominio -> API - Autenticación y Seguridad (Auth & OTP)

| Operación del dominio              | Método HTTP | Endpoint                  |
|------------------------------------|-------------|---------------------------|
| solicitarCodigoOTP(telefono)       | POST        | /usuarios/solicitar       |
| validarCodigoOTP(telefono, codigo) | POST        | /usuarios/verificar{codigo} |
| autenticarUsuario(credenciales)    | POST        | /usuarios/login           |
| cerrarSesionToken()                | POST        | /usuarios/logout          |

---

### Mapeo Dominio -> API - Artesanos

| Operación del dominio | Método HTTP | Endpoint |
| --- | --- | --- |
| Artesano.registrar(...) | POST | /artesanos |
| Consultar ficha del artesano | GET | /artesanos/{id} |
| actualizarPerfilArtesano(id, ...) | PUT | /artesanos/{id} |
| actualizarTrayectoria(...) | PUT | /artesanos/{id}/trayectoria |
| actualizarUbicacion(...) | PUT | /artesanos/{id}/ubicacion |
| cambiarEstadoActivo(...) | PUT | /artesanos/{id}/estado |

---

### Mapeo Dominio -> API - Compradores

| Operación del dominio | Método HTTP | Endpoint |
| --- | --- | --- |
| Comprador.registrar(...) | POST | /compradores |
| Consultar perfil del comprador | GET | /compradores/{id} |
| actualizarPerfilComprador(id, ...) | PUT | /compradores/{id} |
| actualizarDireccionEntrega(...) | PUT | /compradores/{id}/direccion |
| agregarFavorito(...) | POST | /compradores/{id}/favoritos |
| consultarFavoritos(id) | GET | /compradores/{id}/favoritos |
| eliminarFavorito(...) | DELETE | /compradores/{id}/favoritos/{articuloId} |

---

### Mapeo Dominio -> API - Administradores

| Operación del dominio | Método HTTP | Endpoint |
| --- | --- | --- |
| Administrador.registrar(...) | POST | /administradores |
| Listar administradores | GET | /administradores |
| Consultar perfil del administrador | GET | /administradores/{id} |
| actualizarPerfilAdministrador(...) | PUT | /administradores/{id} |
| cambiarNivelAcceso(...) | PUT | /administradores/{id}/nivel_acceso |
| eliminarAdministrador(...) | DELETE | /administradores/{id} |

---

## PROCESO 2: Publicación y Gestión de Catálogo Artesanal

### Mapeo Dominio -> API - Artículos / Artesanías

| Operación del dominio | Método HTTP | Endpoint |
| --- | --- | --- |
| Articulo.crear(...) | POST | /articulos |
| Consultar un articulo | GET | /articulos/{id} |
| actualizarArticulo(id, ...) | PUT | /articulos/{id} |
| cambiarEstadoVisibilidad(...) | PUT | /articulos/{id}/estado |
| reponerStock(cantidad) | PUT | /articulos/{id}/stock |
| eliminar(tienePedidos) | DELETE | /articulos/{id} |
| subirFotografias(...) | POST | /articulos/{id}/imagenes |
| eliminarFotografia(...) | DELETE | /articulos/{id}/imagenes/{imagenId} |
| consultarMetricasProducto(id, fechaInicio, fechaFin) | GET | /articulos/{id}/metricas |

---

### Mapeo Dominio -> API - Búsqueda y Exploración Cultural

| Operación del dominio | Método HTTP | Endpoint |
| --- | --- | --- |
| listarCatalogo() | GET | /api/catalogo |
| buscarPorTitulo(query) | GET | /api/catalogo/buscar?query= |
| listarCategorias() | GET | /api/catalogo/categorias |
| buscarPorGrupo(grupo) | GET | /api/catalogo?grupo= |
| listarPorArtesano(artesanoId) | GET | /api/catalogo/artesano/{artesanoId} |
| obtenerArticulo(id) | GET | /api/articulos/{id} |

---

### Mapeo Dominio -> API - Carrito de Compras

| Operación del dominio | Método HTTP | Endpoint |
| --- | --- | --- |
| agregarItem(compradorId, request) | POST | /api/compradores/{compradorId}/carrito/items |
| obtenerCarrito(compradorId) | GET | /api/compradores/{compradorId}/carrito |
| actualizarCantidad(compradorId, itemId, cantidad) | PUT | /api/compradores/{compradorId}/carrito/items/{itemId} |
| eliminarItem(compradorId, itemId) | DELETE | /api/compradores/{compradorId}/carrito/items/{itemId} |
| vaciarCarrito(compradorId) | DELETE | /api/compradores/{compradorId}/carrito |

---

### Mapeo Dominio -> API - Promociones y Descuentos

| Operación del dominio | Método HTTP | Endpoint |
| --- | --- | --- |
| aplicarDescuento(articuloId, request) | POST | /api/articulos/{id}/descuento |
| removerDescuento(articuloId, artesanoId) | DELETE | /api/articulos/{id}/descuento |

---

### Mapeo Dominio -> API - Gamificación / Logros

| Operación del dominio | Método HTTP | Endpoint |
| --- | --- | --- |
| consultarLogros(artesanoId) | GET | /api/artesanos/{id}/logros |

---

### Mapeo Dominio -> API - Reseñas y Calificaciones

| Operación del dominio | Método HTTP | Endpoint |
| --- | --- | --- |
| crearResena(...) | POST | /articulos/{articuloId}/resenas |
| listarResenasPorArticulo(articuloId) | GET | /articulos/{articuloId}/resenas |
| responderResena(...) | POST | /articulos/{articuloId}/resenas/{resenaId}/respuestas |

---

## PROCESO 3: Procesamiento de Pago y Gestión de Devoluciones

### Mapeo Dominio -> API - Pedidos y Órdenes de Compra

| Operación del dominio | Método HTTP | Endpoint |
| --- | --- | --- |
| crearOrdenCompra(...) | POST | /pedidos |
| consultarPedido(id) | GET | /pedidos/{id} |
| listarPedidosPorComprador(...) | GET | /compradores/{compradorId}/pedidos |
| listarPedidosPorArtesano(...) | GET | /artesanos/{artesanoId}/pedidos |
| actualizarEstadoPedido(...) | PUT | /pedidos/{id}/estado |
| listarHistorialComprasComprador(...) | GET | /compradores/{compradorId}/pedidos |
| listarHistorialVentasArtesano(...) | GET | /artesanos/{artesanoId}/pedidos |
---

### Mapeo Dominio -> API - Pagos

| Operación del dominio | Método HTTP | Endpoint |
| --- | --- | --- |
| procesarPago(...) | POST | /pagos/procesar |
| consultarEstadoTransaccion(...) | GET | /pagos/{pagoId} |
| recibirWebhookPasarela(...) | POST | /pagos/webhook |

---

### Mapeo Dominio -> API - Devoluciones y Postventa

| Operación del dominio | Método HTTP | Endpoint |
| --- | --- | --- |
| Devolucion.crear(...) | POST | /devoluciones |
| Consultar estado de devolución | GET | /devoluciones/{id} |
| listarDevoluciones(...) | GET | /devoluciones |
| Devolucion.aprobar() | PUT | /devoluciones/{id}/aprobacion |
| Devolucion.rechazar() | PUT | /devoluciones/{id}/rechazo |
| procesarReembolso(...) | POST | /devoluciones/{devolucionId}/reembolsar |