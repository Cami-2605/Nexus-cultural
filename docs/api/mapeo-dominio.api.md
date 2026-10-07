# MAPEOS DE DOMINIO
## Mapeo Dominio -> API - Articulo

|Operación del dominio | Método HTTP | Endpoint |
|---|---|---|
|Articulo.crear(...)|POST|/articulos|
|Consultar una articulo|GET|/articulos/{id}|
|inactivar()|PUT|/articulos/{id}/inactivar|
|reactivar()|PUT|/articulos/{id}/inactivar|
|reponerStock(cantidad)|PUT|/articulos/{id}/stock|
|eliminar(tienePedidos)|DELETE|/articulos/{id}|


## Mapeo Dominio -> API - Devolución

|Operación del dominio | Método HTTP | Endpoint |
|---|---|---|
|Devolucion.crear(...)|POST|/devoluciones|
|Consultar estado de devolución|GET|/devoluciones/{id}|
|Devolucion.aprobar()|PUT|/devoluciones/{id}/aprobar|
|Devolucion.rechazar()|PUT|/devoluciones/{id}/rechazar|

---
# Mapeo Dominio -> API - Usuarios

## Mapeo Dominio -> API - Administradores

| Operación del dominio | Método HTTP | Endpoint |
| --- | --- | --- |
| Administrador.registrar(...) | POST | /administradores |
| Consultar perfil del administrador | GET | /administradores/{id} |
| cambiarNivelAcceso(...) | PATCH | /administradores/{id}/nivel-acceso |

## Mapeo Dominio -> API - Artesanos

| Operación del dominio | Método HTTP | Endpoint |
| --- | --- | --- |
| Artesano.registrar(...) | POST | /artesanos |
| Consultar ficha del artesano | GET | /artesanos/{id} |
| actualizarTrayectoria(...) | PUT | /artesanos/{id}/trayectoria |
| actualizarUbicacion(...) | PATCH | /artesanos/{id}/ubicacion |
| cambiarEstadoActivo(...) | PATCH | /artesanos/{id}/estado |

---

## Mapeo Dominio -> API - Compradores

| Operación del dominio | Método HTTP | Endpoint |
| --- | --- | --- |
| Comprador.registrar(...) | POST | /compradores |
| Consultar perfil del comprador | GET | /compradores/{id} |
| agregarFavorito(...) | POST | /compradores/{id}/favoritos |
| eliminarFavorito(...) | DELETE | /compradores/{id}/favoritos/{articuloId} |
| actualizarDireccionEntrega(...) | PUT | /compradores/{id}/direccion-entrega |

---

