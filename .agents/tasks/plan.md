# Plan de Implementación — Proceso 2: Gestión y Publicación del Catálogo Artesanal

> **Contexto del proyecto**
> - Spring Boot 3.4.5 · Java 21 · Gradle (Groovy DSL)
> - Paquete base: `com.uniquindio.nexuscultural`
> - Capas: `domain/entity`, `domain/valueobject`, `domain/repository` (puertos),
>   `application/service`, `application/dto`, `infraestructure/persistence` (adaptadores en memoria),
>   `infraestructure/rest` (controllers), `infraestructure/rest/mapper`
> - Ortografía obligatoria: **`infraestructure`** (con e)
> - Excepción de dominio: `ReglaDominioException` (RuntimeException, solo recibe `String message`)
> - Value object `Precio` es un `record(BigDecimal monto, String moneda)`
> - Commits en español con prefijo Conventional Commits: `feat(catalogo): descripción`
> - Comando de compilación: `./gradlew build` (o `gradlew.bat build` en Windows)
> - Comando de tests: `./gradlew test`
> - **NO implementar** el Proceso 3 (pagos/pedidos)
> - Las entidades están en `domain/entity`, **no** en `domain/model`

---

## PARTE 1 — Catálogo público (exploración, búsqueda y detalle)

### 1.1 — Ampliar `ArticuloRepository` con métodos de consulta pública

Agregar los cuatro métodos de consulta de solo-lectura al puerto `ArticuloRepository`.
No requiere romper la interfaz existente: se agregan métodos nuevos.

**Archivo:**
`src/main/java/com/uniquindio/nexuscultural/domain/repository/ArticuloRepository.java`

Agregar al final de la interfaz:

```java
import com.uniquindio.nexuscultural.domain.valueobject.Grupo;
import java.util.List;

List<Articulo> listarDisponibles();
List<Articulo> buscarPorTitulo(String query);
List<Articulo> buscarPorGrupo(Grupo grupo);
List<Articulo> listarPorArtesano(UUID artesanoId);
```

**Verificar:** `gradlew.bat compileJava` — debe compilar sin errores (la implementación en memoria faltará hasta el ítem 1.2 y causará error de compilación en `ArticuloRepositoryEnMemoria`).

---

### 1.2 — Implementar los nuevos métodos en `ArticuloRepositoryEnMemoria`

Agregar las cuatro implementaciones que filtran el `Map<UUID, Articulo> articulos` interno.

**Archivo:**
`src/main/java/com/uniquindio/nexuscultural/infraestructure/persistence/ArticuloRepositoryEnMemoria.java`

Implementaciones a agregar:

```java
@Override
public List<Articulo> listarDisponibles() {
    return articulos.values().stream()
        .filter(a -> a.getEstado() == EstadoArticulo.DISPONIBLE)
        .toList();
}

@Override
public List<Articulo> buscarPorTitulo(String query) {
    if (query == null || query.isBlank()) return listarDisponibles();
    String q = query.toLowerCase();
    return articulos.values().stream()
        .filter(a -> a.getEstado() == EstadoArticulo.DISPONIBLE)
        .filter(a -> a.getTitulo().toLowerCase().contains(q))
        .toList();
}

@Override
public List<Articulo> buscarPorGrupo(Grupo grupo) {
    return articulos.values().stream()
        .filter(a -> a.getEstado() == EstadoArticulo.DISPONIBLE)
        .filter(a -> a.getGrupo() == grupo)
        .toList();
}

@Override
public List<Articulo> listarPorArtesano(UUID artesanoId) {
    return articulos.values().stream()
        .filter(a -> a.getArtesanoId().equals(artesanoId))
        .toList();
}
```

Agregar imports necesarios: `EstadoArticulo`, `Grupo`, `List`.

**Verificar:** `gradlew.bat compileJava` — compila sin errores.

---

### 1.3 — Crear `CatalogoService`

Nuevo servicio de aplicación que expone el catálogo público. No extiende `ArticuloService`; los usa en paralelo para separar responsabilidades (lectura pública vs. escritura del artesano).

**Archivo a crear:**
`src/main/java/com/uniquindio/nexuscultural/application/service/CatalogoService.java`

```java
package com.uniquindio.nexuscultural.application.service;

import com.uniquindio.nexuscultural.domain.entity.Articulo;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.repository.ArticuloRepository;
import com.uniquindio.nexuscultural.domain.valueobject.Grupo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CatalogoService {

    private final ArticuloRepository articuloRepository;

    public CatalogoService(ArticuloRepository articuloRepository) {
        this.articuloRepository = articuloRepository;
    }

    public List<Articulo> listarCatalogo() {
        return articuloRepository.listarDisponibles();
    }

    public List<Articulo> buscarPorTitulo(String query) {
        return articuloRepository.buscarPorTitulo(query);
    }

    public List<Articulo> buscarPorGrupo(Grupo grupo) {
        if (grupo == null) throw new ReglaDominioException("El grupo no puede ser nulo.");
        return articuloRepository.buscarPorGrupo(grupo);
    }

    public List<Articulo> listarPorArtesano(UUID artesanoId) {
        if (artesanoId == null) throw new ReglaDominioException("El ID del artesano es obligatorio.");
        return articuloRepository.listarPorArtesano(artesanoId);
    }

    public Articulo obtenerArticulo(UUID id) {
        return articuloRepository.buscarPorId(id)
            .orElseThrow(() -> new ReglaDominioException("Artículo no encontrado."));
    }
}
```

**Verificar:** `gradlew.bat compileJava` — sin errores.

---

### 1.4 — Crear `CatalogoController`

Nuevo controller REST que expone los endpoints de búsqueda y exploración pública.
Reutiliza `ArticuloMapper` (ya existe como `@Component`) para mapear a `ArticuloDetalleResponse`.

**Archivo a crear:**
`src/main/java/com/uniquindio/nexuscultural/infraestructure/rest/CatalogoController.java`

```java
package com.uniquindio.nexuscultural.infraestructure.rest;

import com.uniquindio.nexuscultural.application.dto.response.ArticuloDetalleResponse;
import com.uniquindio.nexuscultural.application.service.CatalogoService;
import com.uniquindio.nexuscultural.domain.valueobject.Grupo;
import com.uniquindio.nexuscultural.infraestructure.rest.mapper.ArticuloMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class CatalogoController {

    private final CatalogoService catalogoService;
    private final ArticuloMapper articuloMapper;

    public CatalogoController(CatalogoService catalogoService, ArticuloMapper articuloMapper) {
        this.catalogoService = catalogoService;
        this.articuloMapper = articuloMapper;
    }

    /** GET /api/catalogo — Lista todos los artículos disponibles */
    @GetMapping("/catalogo")
    public ResponseEntity<List<ArticuloDetalleResponse>> listarCatalogo(
            @RequestParam(required = false) Grupo grupo) {
        List<ArticuloDetalleResponse> respuesta = grupo != null
            ? catalogoService.buscarPorGrupo(grupo).stream().map(articuloMapper::toResponse).toList()
            : catalogoService.listarCatalogo().stream().map(articuloMapper::toResponse).toList();
        return ResponseEntity.ok(respuesta);
    }

    /** GET /api/catalogo/buscar?query=  — Búsqueda por título */
    @GetMapping("/catalogo/buscar")
    public ResponseEntity<List<ArticuloDetalleResponse>> buscar(
            @RequestParam String query) {
        List<ArticuloDetalleResponse> respuesta = catalogoService.buscarPorTitulo(query)
            .stream().map(articuloMapper::toResponse).toList();
        return ResponseEntity.ok(respuesta);
    }

    /** GET /api/catalogo/categorias — Lista todos los valores del enum Grupo */
    @GetMapping("/catalogo/categorias")
    public ResponseEntity<List<String>> listarCategorias() {
        List<String> categorias = Arrays.stream(Grupo.values())
            .map(Grupo::name)
            .toList();
        return ResponseEntity.ok(categorias);
    }

    /** GET /api/catalogo/artesano/{artesanoId} — Artículos de un artesano específico */
    @GetMapping("/catalogo/artesano/{artesanoId}")
    public ResponseEntity<List<ArticuloDetalleResponse>> listarPorArtesano(
            @PathVariable UUID artesanoId) {
        List<ArticuloDetalleResponse> respuesta = catalogoService.listarPorArtesano(artesanoId)
            .stream().map(articuloMapper::toResponse).toList();
        return ResponseEntity.ok(respuesta);
    }

    /** GET /api/articulos/{id} — Detalle de un artículo por ID */
    @GetMapping("/articulos/{id}")
    public ResponseEntity<ArticuloDetalleResponse> obtenerArticulo(@PathVariable UUID id) {
        return ResponseEntity.ok(articuloMapper.toResponse(catalogoService.obtenerArticulo(id)));
    }
}
```

> **Nota:** El endpoint `GET /api/articulos/{id}` se agrega aquí en `CatalogoController` con mapping `/api` para evitar colisión con `ArticuloController` (que usa `@RequestMapping("/api/articulos")`). Si se prefiere unificarlo, mover el método `obtenerArticulo` directamente a `ArticuloController` como `@GetMapping("/{id}")`.

**Verificar:** `gradlew.bat build` — compila y el contexto de Spring arranca.

---

## PARTE 2 — Carrito de compras

### 2.1 — Crear entidad de dominio `CarritoItem`

Entidad sencilla (no usa Lombok para seguir el patrón de `Articulo.java`).
La validación de `cantidad > 0` vive en el constructor.

**Archivo a crear:**
`src/main/java/com/uniquindio/nexuscultural/domain/entity/CarritoItem.java`

```java
package com.uniquindio.nexuscultural.domain.entity;

import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;

import java.util.UUID;

public class CarritoItem {

    private final UUID id;
    private final UUID compradorId;
    private final UUID articuloId;
    private int cantidad;

    public CarritoItem(UUID compradorId, UUID articuloId, int cantidad) {
        if (compradorId == null) throw new ReglaDominioException("El comprador es obligatorio.");
        if (articuloId == null) throw new ReglaDominioException("El artículo es obligatorio.");
        if (cantidad <= 0) throw new ReglaDominioException("La cantidad debe ser mayor a cero.");
        this.id = UUID.randomUUID();
        this.compradorId = compradorId;
        this.articuloId = articuloId;
        this.cantidad = cantidad;
    }

    /** Constructor de reconstrucción para el repositorio (no revalida reglas de creación). */
    public static CarritoItem reconstruir(UUID id, UUID compradorId, UUID articuloId, int cantidad) {
        CarritoItem item = new CarritoItem(compradorId, articuloId, cantidad);
        // No expone el id en el constructor principal para evitar inconsistencias;
        // este factory method lo usa solo la capa de persistencia.
        return new CarritoItem(id, compradorId, articuloId, cantidad);
    }

    private CarritoItem(UUID id, UUID compradorId, UUID articuloId, int cantidad) {
        this.id = id;
        this.compradorId = compradorId;
        this.articuloId = articuloId;
        this.cantidad = cantidad;
    }

    public void actualizarCantidad(int nuevaCantidad) {
        if (nuevaCantidad <= 0) throw new ReglaDominioException("La cantidad debe ser mayor a cero.");
        this.cantidad = nuevaCantidad;
    }

    public UUID getId() { return id; }
    public UUID getCompradorId() { return compradorId; }
    public UUID getArticuloId() { return articuloId; }
    public int getCantidad() { return cantidad; }
}
```

**Verificar:** `gradlew.bat compileJava` — sin errores.

---

### 2.2 — Crear puerto `CarritoRepository`

**Archivo a crear:**
`src/main/java/com/uniquindio/nexuscultural/domain/repository/CarritoRepository.java`

```java
package com.uniquindio.nexuscultural.domain.repository;

import com.uniquindio.nexuscultural.domain.entity.CarritoItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CarritoRepository {

    CarritoItem guardar(CarritoItem item);

    List<CarritoItem> buscarPorComprador(UUID compradorId);

    Optional<CarritoItem> buscarPorId(UUID itemId);

    void eliminar(UUID itemId);

    void eliminarPorComprador(UUID compradorId);

    CarritoItem actualizarCantidad(UUID itemId, int nuevaCantidad);
}
```

**Verificar:** `gradlew.bat compileJava` — sin errores.

---

### 2.3 — Crear `CarritoRepositoryEnMemoria`

**Archivo a crear:**
`src/main/java/com/uniquindio/nexuscultural/infraestructure/persistence/CarritoRepositoryEnMemoria.java`

```java
package com.uniquindio.nexuscultural.infraestructure.persistence;

import com.uniquindio.nexuscultural.domain.entity.CarritoItem;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.repository.CarritoRepository;

import java.util.*;
import java.util.stream.Collectors;

public class CarritoRepositoryEnMemoria implements CarritoRepository {

    private final Map<UUID, CarritoItem> items = new HashMap<>();

    @Override
    public CarritoItem guardar(CarritoItem item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public List<CarritoItem> buscarPorComprador(UUID compradorId) {
        return items.values().stream()
            .filter(i -> i.getCompradorId().equals(compradorId))
            .collect(Collectors.toList());
    }

    @Override
    public Optional<CarritoItem> buscarPorId(UUID itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public void eliminar(UUID itemId) {
        items.remove(itemId);
    }

    @Override
    public void eliminarPorComprador(UUID compradorId) {
        items.entrySet().removeIf(e -> e.getValue().getCompradorId().equals(compradorId));
    }

    @Override
    public CarritoItem actualizarCantidad(UUID itemId, int nuevaCantidad) {
        CarritoItem item = items.get(itemId);
        if (item == null) throw new ReglaDominioException("Ítem del carrito no encontrado.");
        item.actualizarCantidad(nuevaCantidad);
        return item;
    }
}
```

**Verificar:** `gradlew.bat compileJava` — sin errores.

---

### 2.4 — Registrar bean `CarritoRepository` en `BeanConfiguration`

**Archivo a modificar:**
`src/main/java/com/uniquindio/nexuscultural/infraestructure/config/BeanConfiguration.java`

Agregar import y bean:

```java
import com.uniquindio.nexuscultural.domain.repository.CarritoRepository;
import com.uniquindio.nexuscultural.infraestructure.persistence.CarritoRepositoryEnMemoria;

// dentro de la clase:
@Bean
public CarritoRepository carritoRepository() {
    return new CarritoRepositoryEnMemoria();
}
```

**Verificar:** `gradlew.bat compileJava` — sin errores.

---

### 2.5 — Crear DTOs del carrito

Siguiendo el patrón `record` con validaciones Jakarta de los DTOs existentes.

**Archivos a crear:**

`src/main/java/com/uniquindio/nexuscultural/application/dto/request/AgregarItemCarritoRequest.java`
```java
package com.uniquindio.nexuscultural.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AgregarItemCarritoRequest(
    @NotNull(message = "El ID del artículo es obligatorio")
    UUID articuloId,

    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    int cantidad
) {}
```

`src/main/java/com/uniquindio/nexuscultural/application/dto/request/ActualizarCantidadItemRequest.java`
```java
package com.uniquindio.nexuscultural.application.dto.request;

import jakarta.validation.constraints.Min;

public record ActualizarCantidadItemRequest(
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    int cantidad
) {}
```

`src/main/java/com/uniquindio/nexuscultural/application/dto/response/CarritoItemResponse.java`
```java
package com.uniquindio.nexuscultural.application.dto.response;

import java.util.UUID;

public record CarritoItemResponse(
    UUID id,
    UUID compradorId,
    UUID articuloId,
    int cantidad
) {}
```

**Verificar:** `gradlew.bat compileJava` — sin errores.

---

### 2.6 — Crear `CarritoService`

**Archivo a crear:**
`src/main/java/com/uniquindio/nexuscultural/application/service/CarritoService.java`

```java
package com.uniquindio.nexuscultural.application.service;

import com.uniquindio.nexuscultural.application.dto.request.AgregarItemCarritoRequest;
import com.uniquindio.nexuscultural.application.dto.response.CarritoItemResponse;
import com.uniquindio.nexuscultural.domain.entity.CarritoItem;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.repository.CarritoRepository;
import com.uniquindio.nexuscultural.domain.repository.CompradorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final CompradorRepository compradorRepository;

    public CarritoService(CarritoRepository carritoRepository,
                          CompradorRepository compradorRepository) {
        this.carritoRepository = carritoRepository;
        this.compradorRepository = compradorRepository;
    }

    public CarritoItem agregarItem(UUID compradorId, AgregarItemCarritoRequest request) {
        compradorRepository.buscarPorId(compradorId)
            .orElseThrow(() -> new ReglaDominioException("Comprador no encontrado."));
        CarritoItem item = new CarritoItem(compradorId, request.articuloId(), request.cantidad());
        return carritoRepository.guardar(item);
    }

    public List<CarritoItem> obtenerCarrito(UUID compradorId) {
        compradorRepository.buscarPorId(compradorId)
            .orElseThrow(() -> new ReglaDominioException("Comprador no encontrado."));
        return carritoRepository.buscarPorComprador(compradorId);
    }

    public CarritoItem actualizarCantidad(UUID compradorId, UUID itemId, int nuevaCantidad) {
        CarritoItem item = carritoRepository.buscarPorId(itemId)
            .orElseThrow(() -> new ReglaDominioException("Ítem del carrito no encontrado."));
        if (!item.getCompradorId().equals(compradorId)) {
            throw new ReglaDominioException("El ítem no pertenece al comprador indicado.");
        }
        return carritoRepository.actualizarCantidad(itemId, nuevaCantidad);
    }

    public void eliminarItem(UUID compradorId, UUID itemId) {
        CarritoItem item = carritoRepository.buscarPorId(itemId)
            .orElseThrow(() -> new ReglaDominioException("Ítem del carrito no encontrado."));
        if (!item.getCompradorId().equals(compradorId)) {
            throw new ReglaDominioException("El ítem no pertenece al comprador indicado.");
        }
        carritoRepository.eliminar(itemId);
    }

    public void vaciarCarrito(UUID compradorId) {
        compradorRepository.buscarPorId(compradorId)
            .orElseThrow(() -> new ReglaDominioException("Comprador no encontrado."));
        carritoRepository.eliminarPorComprador(compradorId);
    }
}
```

**Verificar:** `gradlew.bat compileJava` — sin errores.

---

### 2.7 — Crear `CarritoController`

Se agregan los endpoints del carrito como sub-recurso de `/api/compradores`.
`CarritoController` convive con `CompradorController` sin colisiones porque mapea rutas distintas.

**Archivo a crear:**
`src/main/java/com/uniquindio/nexuscultural/infraestructure/rest/CarritoController.java`

```java
package com.uniquindio.nexuscultural.infraestructure.rest;

import com.uniquindio.nexuscultural.application.dto.request.ActualizarCantidadItemRequest;
import com.uniquindio.nexuscultural.application.dto.request.AgregarItemCarritoRequest;
import com.uniquindio.nexuscultural.application.dto.response.CarritoItemResponse;
import com.uniquindio.nexuscultural.application.service.CarritoService;
import com.uniquindio.nexuscultural.domain.entity.CarritoItem;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/compradores/{compradorId}/carrito")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    /** POST /api/compradores/{compradorId}/carrito/items */
    @PostMapping("/items")
    public ResponseEntity<CarritoItemResponse> agregarItem(
            @PathVariable UUID compradorId,
            @Valid @RequestBody AgregarItemCarritoRequest request) {
        CarritoItem item = carritoService.agregarItem(compradorId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(item));
    }

    /** GET /api/compradores/{compradorId}/carrito */
    @GetMapping
    public ResponseEntity<List<CarritoItemResponse>> obtenerCarrito(@PathVariable UUID compradorId) {
        List<CarritoItemResponse> respuesta = carritoService.obtenerCarrito(compradorId)
            .stream().map(this::toResponse).toList();
        return ResponseEntity.ok(respuesta);
    }

    /** PUT /api/compradores/{compradorId}/carrito/items/{itemId} */
    @PutMapping("/items/{itemId}")
    public ResponseEntity<CarritoItemResponse> actualizarCantidad(
            @PathVariable UUID compradorId,
            @PathVariable UUID itemId,
            @Valid @RequestBody ActualizarCantidadItemRequest request) {
        CarritoItem item = carritoService.actualizarCantidad(compradorId, itemId, request.cantidad());
        return ResponseEntity.ok(toResponse(item));
    }

    /** DELETE /api/compradores/{compradorId}/carrito/items/{itemId} */
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> eliminarItem(
            @PathVariable UUID compradorId,
            @PathVariable UUID itemId) {
        carritoService.eliminarItem(compradorId, itemId);
        return ResponseEntity.noContent().build();
    }

    /** DELETE /api/compradores/{compradorId}/carrito */
    @DeleteMapping
    public ResponseEntity<Void> vaciarCarrito(@PathVariable UUID compradorId) {
        carritoService.vaciarCarrito(compradorId);
        return ResponseEntity.noContent().build();
    }

    private CarritoItemResponse toResponse(CarritoItem item) {
        return new CarritoItemResponse(
            item.getId(),
            item.getCompradorId(),
            item.getArticuloId(),
            item.getCantidad()
        );
    }
}
```

**Verificar:** `gradlew.bat build` — todo el contexto Spring arranca sin errores.

---

## PARTE 3 — Promociones y descuentos

### 3.1 — Crear value object `Descuento`

`Descuento` es un record inmutable. `aplicar(Precio)` devuelve un `Precio` nuevo con el descuento calculado.

**Archivo a crear:**
`src/main/java/com/uniquindio/nexuscultural/domain/valueobject/Descuento.java`

```java
package com.uniquindio.nexuscultural.domain.valueobject;

import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Descuento(int porcentaje, String motivoDescuento) {

    public Descuento {
        if (porcentaje < 1 || porcentaje > 99) {
            throw new ReglaDominioException("El porcentaje de descuento debe estar entre 1 y 99.");
        }
        if (motivoDescuento == null || motivoDescuento.isBlank()) {
            throw new ReglaDominioException("El motivo del descuento es obligatorio.");
        }
    }

    /** Aplica el descuento a un precio y retorna el precio resultante. */
    public Precio aplicar(Precio precio) {
        if (precio == null) throw new ReglaDominioException("El precio no puede ser nulo.");
        BigDecimal factor = BigDecimal.ONE
            .subtract(BigDecimal.valueOf(porcentaje).divide(BigDecimal.valueOf(100)));
        BigDecimal nuevoMonto = precio.monto().multiply(factor).setScale(2, RoundingMode.HALF_UP);
        return new Precio(nuevoMonto, precio.moneda());
    }
}
```

**Verificar:** `gradlew.bat compileJava` — sin errores.

---

### 3.2 — Agregar soporte de descuento a `Articulo`

Agregar campo `descuento` nullable y los tres métodos de negocio.

**Archivo a modificar:**
`src/main/java/com/uniquindio/nexuscultural/domain/entity/Articulo.java`

Cambios:
1. Agregar campo `private Descuento descuento;` (nullable, sin inicializar en constructor).
2. Agregar import `import com.uniquindio.nexuscultural.domain.valueobject.Descuento;`.
3. Agregar los tres métodos al final de los métodos públicos (antes de `equals`/`hashCode`):

```java
public void aplicarDescuento(Descuento descuento) {
    if (estado == EstadoArticulo.ELIMINADO) {
        throw new ReglaDominioException("No se puede aplicar descuento a un artículo eliminado.");
    }
    if (descuento == null) throw new ReglaDominioException("El descuento no puede ser nulo.");
    this.descuento = descuento;
}

public void removerDescuento() {
    this.descuento = null;
}

public Precio getPrecioConDescuento() {
    if (descuento == null) return precio;
    return descuento.aplicar(precio);
}

public Descuento getDescuento() { return descuento; }
```

**Verificar:** `gradlew.bat compileJava` — sin errores.

---

### 3.3 — Actualizar `ArticuloDetalleResponse` y `ArticuloMapper`

Agregar dos campos opcionales a la response para exponer el precio con descuento.

**Archivo a modificar:**
`src/main/java/com/uniquindio/nexuscultural/application/dto/response/ArticuloDetalleResponse.java`

Reemplazar el record completo con:
```java
package com.uniquindio.nexuscultural.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ArticuloDetalleResponse(
    String id,
    String artesanoId,
    String titulo,
    String descripcion,
    double montoPrecio,
    String moneda,
    int stock,
    String estado,
    String grupo,
    List<String> imagenes,
    LocalDateTime fechaPublicacion,
    Double precioConDescuento,      // null si no hay descuento activo
    Integer porcentajeDescuento     // null si no hay descuento activo
) {}
```

**Archivo a modificar:**
`src/main/java/com/uniquindio/nexuscultural/infraestructure/rest/mapper/ArticuloMapper.java`

Reemplazar el método `toResponse` con:
```java
public ArticuloDetalleResponse toResponse(Articulo articulo) {
    if (articulo == null) return null;

    Double precioConDescuento = null;
    Integer porcentajeDescuento = null;

    if (articulo.getDescuento() != null) {
        precioConDescuento = articulo.getPrecioConDescuento().monto().doubleValue();
        porcentajeDescuento = articulo.getDescuento().porcentaje();
    }

    return new ArticuloDetalleResponse(
        articulo.getId() != null ? articulo.getId().toString() : null,
        articulo.getArtesanoId() != null ? articulo.getArtesanoId().toString() : null,
        articulo.getTitulo(),
        articulo.getDescripcion(),
        articulo.getPrecio() != null ? articulo.getPrecio().monto().doubleValue() : 0.0,
        articulo.getPrecio() != null ? articulo.getPrecio().moneda() : "COP",
        articulo.getStock(),
        articulo.getEstado() != null ? articulo.getEstado().name() : null,
        articulo.getGrupo() != null ? articulo.getGrupo().name() : null,
        articulo.getImagenes(),
        articulo.getFechaPublicacion(),
        precioConDescuento,
        porcentajeDescuento
    );
}
```

**Verificar:** `gradlew.bat compileJava` — sin errores.

---

### 3.4 — Crear DTO `AplicarDescuentoRequest`

**Archivo a crear:**
`src/main/java/com/uniquindio/nexuscultural/application/dto/request/AplicarDescuentoRequest.java`

```java
package com.uniquindio.nexuscultural.application.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AplicarDescuentoRequest(
    @NotNull(message = "El ID del artesano es obligatorio")
    UUID artesanoId,

    @Min(value = 1, message = "El porcentaje mínimo es 1")
    @Max(value = 99, message = "El porcentaje máximo es 99")
    int porcentaje,

    @NotBlank(message = "El motivo del descuento es obligatorio")
    String motivo
) {}
```

**Verificar:** `gradlew.bat compileJava` — sin errores.

---

### 3.5 — Agregar `aplicarDescuento` y `removerDescuento` a `ArticuloService`

**Archivo a modificar:**
`src/main/java/com/uniquindio/nexuscultural/application/service/ArticuloService.java`

Agregar import `import com.uniquindio.nexuscultural.domain.valueobject.Descuento;`
y los dos métodos al final de la clase:

```java
public Articulo aplicarDescuento(UUID articuloId, AplicarDescuentoRequest request) {
    Articulo articulo = articuloRepository.buscarPorId(articuloId)
        .orElseThrow(() -> new ReglaDominioException("Artículo no encontrado."));
    if (!articulo.getArtesanoId().equals(request.artesanoId())) {
        throw new ReglaDominioException("Solo el artesano dueño puede aplicar un descuento.");
    }
    Descuento descuento = new Descuento(request.porcentaje(), request.motivo());
    articulo.aplicarDescuento(descuento);
    return articuloRepository.guardar(articulo);
}

public Articulo removerDescuento(UUID articuloId, UUID artesanoId) {
    Articulo articulo = articuloRepository.buscarPorId(articuloId)
        .orElseThrow(() -> new ReglaDominioException("Artículo no encontrado."));
    if (!articulo.getArtesanoId().equals(artesanoId)) {
        throw new ReglaDominioException("Solo el artesano dueño puede remover el descuento.");
    }
    articulo.removerDescuento();
    return articuloRepository.guardar(articulo);
}
```

También agregar el import del DTO request:
`import com.uniquindio.nexuscultural.application.dto.request.AplicarDescuentoRequest;`

**Verificar:** `gradlew.bat compileJava` — sin errores.

---

### 3.6 — Agregar endpoints de descuento a `ArticuloController`

**Archivo a modificar:**
`src/main/java/com/uniquindio/nexuscultural/infraestructure/rest/ArticuloController.java`

Agregar import `import com.uniquindio.nexuscultural.application.dto.request.AplicarDescuentoRequest;`
y los dos nuevos métodos al final de la clase:

```java
/** POST /api/articulos/{id}/descuento — Aplica un descuento al artículo */
@PostMapping("/{id}/descuento")
public ResponseEntity<ArticuloDetalleResponse> aplicarDescuento(
        @PathVariable UUID id,
        @Valid @RequestBody AplicarDescuentoRequest request) {
    Articulo articulo = articuloService.aplicarDescuento(id, request);
    return ResponseEntity.ok(articuloMapper.toResponse(articulo));
}

/** DELETE /api/articulos/{id}/descuento — Remueve el descuento del artículo */
@DeleteMapping("/{id}/descuento")
public ResponseEntity<ArticuloDetalleResponse> removerDescuento(
        @PathVariable UUID id,
        @RequestParam UUID artesanoId) {
    Articulo articulo = articuloService.removerDescuento(id, artesanoId);
    return ResponseEntity.ok(articuloMapper.toResponse(articulo));
}
```

**Verificar:** `gradlew.bat build` — compila y arranca. Probar con Swagger UI en `/swagger-ui.html`.

---

## PARTE 4 — Gamificación: logros del artesano

### 4.1 — Crear enum `Logro`

**Archivo a crear:**
`src/main/java/com/uniquindio/nexuscultural/domain/valueobject/Logro.java`

```java
package com.uniquindio.nexuscultural.domain.valueobject;

public enum Logro {
    PRIMER_ARTICULO("Primer artículo", "Publicaste tu primer artículo en el catálogo."),
    CINCO_ARTICULOS("Cinco artículos", "Tienes 5 artículos publicados en el catálogo."),
    DIEZ_ARTICULOS("Diez artículos", "Tienes 10 artículos publicados en el catálogo."),
    PRIMER_VENTA("Primera venta", "Realizaste tu primera venta."),
    CINCO_VENTAS("Cinco ventas", "Completaste 5 ventas exitosas.");

    private final String nombre;
    private final String descripcion;

    Logro(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
}
```

**Verificar:** `gradlew.bat compileJava` — sin errores.

---

### 4.2 — Agregar `logros` a `Artesano`

La entidad `Artesano` no tiene logros actualmente (verificado: solo tiene `trayectoriaCultural`, `ubicacionGeografica`, `activo`).

**Archivo a modificar:**
`src/main/java/com/uniquindio/nexuscultural/domain/entity/Artesano.java`

Cambios:
1. Agregar import: `import com.uniquindio.nexuscultural.domain.valueobject.Logro;` y `import java.util.ArrayList; import java.util.Collections; import java.util.List;`
2. Agregar campo: `private List<Logro> logros = new ArrayList<>();`
3. Agregar método `otorgarLogro` (idempotente, sin duplicar):

```java
public void otorgarLogro(Logro logro) {
    if (logro == null) throw new ReglaDominioException("El logro no puede ser nulo.");
    if (!logros.contains(logro)) {
        logros.add(logro);
    }
}

public List<Logro> getLogros() {
    return Collections.unmodifiableList(logros);
}
```

> El campo `logros` debe inicializarse como `new ArrayList<>()` directamente en la declaración; el `@Builder` de Lombok ignorará campos no declarados en el builder. Si se desea que el builder pueda inyectar logros (por ejemplo, al reconstruir desde BD), agregar también un parámetro `logros` al `@Builder` privado.

**Verificar:** `gradlew.bat compileJava` — sin errores.

---

### 4.3 — Crear `LogroResponse`

**Archivo a crear:**
`src/main/java/com/uniquindio/nexuscultural/application/dto/response/LogroResponse.java`

```java
package com.uniquindio.nexuscultural.application.dto.response;

public record LogroResponse(
    String nombre,
    String descripcion
) {}
```

**Verificar:** `gradlew.bat compileJava` — sin errores.

---

### 4.4 — Crear `LogroService`

Evalúa si el artesano merece un logro nuevo luego de publicar un artículo. Recibe el `artesanoId` y el total de artículos publicados (que el llamador ya calculó), para no acoplar el servicio con `ArticuloRepository`.

**Archivo a crear:**
`src/main/java/com/uniquindio/nexuscultural/application/service/LogroService.java`

```java
package com.uniquindio.nexuscultural.application.service;

import com.uniquindio.nexuscultural.domain.entity.Artesano;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.repository.ArtesanoRepository;
import com.uniquindio.nexuscultural.domain.valueobject.Logro;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LogroService {

    private final ArtesanoRepository artesanoRepository;

    public LogroService(ArtesanoRepository artesanoRepository) {
        this.artesanoRepository = artesanoRepository;
    }

    /**
     * Evalúa y otorga logros de publicación al artesano según la cantidad total
     * de artículos que tiene publicados (totalArticulos).
     * Debe llamarse después de guardar el nuevo artículo.
     */
    public Artesano evaluarLogrosPublicacion(UUID artesanoId, int totalArticulos) {
        Artesano artesano = artesanoRepository.buscarPorId(artesanoId)
            .orElseThrow(() -> new ReglaDominioException("Artesano no encontrado."));

        if (totalArticulos >= 1)  artesano.otorgarLogro(Logro.PRIMER_ARTICULO);
        if (totalArticulos >= 5)  artesano.otorgarLogro(Logro.CINCO_ARTICULOS);
        if (totalArticulos >= 10) artesano.otorgarLogro(Logro.DIEZ_ARTICULOS);

        return artesanoRepository.guardar(artesano);
    }
}
```

**Verificar:** `gradlew.bat compileJava` — sin errores.

---

### 4.5 — Agregar llamada a `LogroService` en `ArticuloService.publicarArticulo`

Conectar la gamificación al flujo de publicación: después de guardar el artículo, contar los artículos del artesano y evaluar logros.

**Archivo a modificar:**
`src/main/java/com/uniquindio/nexuscultural/application/service/ArticuloService.java`

1. Agregar `LogroService` como dependencia inyectada por constructor.
2. Modificar `publicarArticulo` para llamar a `logroService.evaluarLogrosPublicacion(...)` al final.

```java
// Cambiar constructor:
public ArticuloService(ArticuloRepository articuloRepository, LogroService logroService) {
    this.articuloRepository = articuloRepository;
    this.logroService = logroService;
}

// Modificar publicarArticulo:
public Articulo publicarArticulo(CrearArticuloRequest request) {
    Articulo articulo = Articulo.crear(
        request.artesanoId(),
        request.titulo(),
        request.descripcion(),
        request.stock(),
        request.grupo(),
        request.imagenes(),
        request.precio(),
        LocalDateTime.now()
    );
    Articulo guardado = articuloRepository.guardar(articulo);

    // Gamificación: contar artículos del artesano y evaluar logros
    int totalArticulos = articuloRepository.listarPorArtesano(request.artesanoId()).size();
    logroService.evaluarLogrosPublicacion(request.artesanoId(), totalArticulos);

    return guardado;
}
```

> **Atención a dependencia circular:** `ArticuloService` → `LogroService` → `ArtesanoRepository`.
> `LogroService` no depende de `ArticuloService`, así que no hay ciclo.

**Verificar:** `gradlew.bat compileJava` — sin errores.

---

### 4.6 — Agregar endpoint de logros en `ArtesanoController`

Agregar `GET /api/artesanos/{id}/logros` y actualizar `ArtesanoResponse` con el campo `logros`.

**Opción elegida:** agregar el campo a `ArtesanoResponse` (más cohesivo) **y** exponer el endpoint por separado.
`ArtesanoResponse` ya no tiene `logros` (verificado), así que se agrega sin romper compatibilidad.

**Archivo a modificar:**
`src/main/java/com/uniquindio/nexuscultural/application/dto/response/ArtesanoResponse.java`

Agregar campo `List<LogroResponse> logros`:

```java
package com.uniquindio.nexuscultural.application.dto.response;

import com.uniquindio.nexuscultural.domain.valueobject.TrayectoriaCultural;
import com.uniquindio.nexuscultural.domain.valueobject.UbicacionGeografica;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ArtesanoResponse(
    UUID id,
    String nombreCompleto,
    String email,
    LocalDateTime fechaRegistro,
    boolean activo,
    TrayectoriaCultural trayectoriaCultural,
    UbicacionGeografica ubicacionGeografica,
    List<LogroResponse> logros
) {}
```

**Archivo a modificar:**
`src/main/java/com/uniquindio/nexuscultural/infraestructure/rest/mapper/ArtesanoMapper.java`

Actualizar `toResponse` para mapear `logros`:

```java
// Dentro del método toResponse, al construir ArtesanoResponse, agregar el argumento:
List<LogroResponse> logros = artesano.getLogros().stream()
    .map(l -> new LogroResponse(l.getNombre(), l.getDescripcion()))
    .toList();
// Pasar logros como último argumento al constructor de ArtesanoResponse
```

> Leer el archivo `ArtesanoMapper.java` antes de modificar para ajustar el código al patrón exacto existente.

**Archivo a modificar:**
`src/main/java/com/uniquindio/nexuscultural/infraestructure/rest/ArtesanoController.java`

Agregar endpoint:

```java
/** GET /api/artesanos/{id}/logros — Retorna los logros del artesano */
@GetMapping("/{id}/logros")
public ResponseEntity<List<LogroResponse>> consultarLogros(@PathVariable UUID id) {
    // Obtener el artesano y mapear sus logros
    Artesano artesano = artesanoService.obtenerArtesano(id); // ajustar según método real existente
    List<LogroResponse> logros = artesano.getLogros().stream()
        .map(l -> new LogroResponse(l.getNombre(), l.getDescripcion()))
        .toList();
    return ResponseEntity.ok(logros);
}
```

> **Nota:** Leer `ArtesanoController.java` antes de editar para usar el nombre correcto del método del service que obtiene un artesano por ID.

**Verificar:** `gradlew.bat build` — compila y arranca.

---

## PARTE 5 — Moderación: validaciones antes de publicar

### 5.1 — Agregar validaciones de moderación en `ArticuloService.publicarArticulo`

Antes de llamar a `Articulo.crear(...)`, validar que el título tenga al menos 5 caracteres y la descripción al menos 50. Estas son reglas de moderación de la plataforma (distintas a las reglas de dominio de `Articulo.crear`).

**Archivo a modificar:**
`src/main/java/com/uniquindio/nexuscultural/application/service/ArticuloService.java`

Agregar al inicio de `publicarArticulo`, antes de `Articulo.crear(...)`:

```java
if (request.titulo() == null || request.titulo().trim().length() < 5) {
    throw new ReglaDominioException("El título debe tener al menos 5 caracteres.");
}
if (request.descripcion() == null || request.descripcion().trim().length() < 50) {
    throw new ReglaDominioException("La descripción debe tener al menos 50 caracteres para describir el valor cultural del producto.");
}
```

**Verificar:** `gradlew.bat test` — todos los tests existentes pasan. Si algún test usa títulos o descripciones demasiado cortas, actualizarlos con datos válidos.

---

## PARTE 6 — Actualizar documentación de API

### 6.1 — Actualizar `docs/api/mapeo-dominio.api.md`

Agregar las nuevas secciones y completar las existentes con los nuevos endpoints.

**Archivo a modificar:**
`docs/api/mapeo-dominio.api.md`

Dentro del bloque **PROCESO 2**, actualizar/agregar las siguientes tablas:

**Búsqueda y Exploración Cultural** (reemplazar tabla existente):

| Operación del dominio | Método HTTP | Endpoint |
| --- | --- | --- |
| listarCatalogo() | GET | /api/catalogo |
| buscarPorTitulo(query) | GET | /api/catalogo/buscar?query= |
| listarCategorias() | GET | /api/catalogo/categorias |
| buscarPorGrupo(grupo) | GET | /api/catalogo?grupo= |
| listarPorArtesano(artesanoId) | GET | /api/catalogo/artesano/{artesanoId} |
| obtenerArticulo(id) | GET | /api/articulos/{id} |

**Carrito de Compras** (ya existe en el doc, verificar que coincida):

| Operación del dominio | Método HTTP | Endpoint |
| --- | --- | --- |
| agregarItem(compradorId, request) | POST | /api/compradores/{compradorId}/carrito/items |
| obtenerCarrito(compradorId) | GET | /api/compradores/{compradorId}/carrito |
| actualizarCantidad(compradorId, itemId, cantidad) | PUT | /api/compradores/{compradorId}/carrito/items/{itemId} |
| eliminarItem(compradorId, itemId) | DELETE | /api/compradores/{compradorId}/carrito/items/{itemId} |
| vaciarCarrito(compradorId) | DELETE | /api/compradores/{compradorId}/carrito |

**Promociones y Descuentos** (tabla nueva):

| Operación del dominio | Método HTTP | Endpoint |
| --- | --- | --- |
| aplicarDescuento(articuloId, request) | POST | /api/articulos/{id}/descuento |
| removerDescuento(articuloId, artesanoId) | DELETE | /api/articulos/{id}/descuento |

**Gamificación — Logros** (tabla nueva):

| Operación del dominio | Método HTTP | Endpoint |
| --- | --- | --- |
| consultarLogros(artesanoId) | GET | /api/artesanos/{id}/logros |

**Verificar:** `gradlew.bat build` — build final exitoso. Swagger UI en `/swagger-ui.html` debe mostrar todos los nuevos endpoints.

---

## Orden de implementación (resumen de dependencias)

```
1.1 → 1.2 → 1.3 → 1.4          (catálogo: repo → impl → service → controller)
2.1 → 2.2 → 2.3 → 2.4 → 2.5 → 2.6 → 2.7   (carrito)
3.1 → 3.2 → 3.3 → 3.4 → 3.5 → 3.6          (descuentos)
4.1 → 4.2 → 4.3 → 4.4 → 4.5 → 4.6          (gamificación)
5.1                                           (moderación — depende de 1.2 y 4.5)
6.1                                           (docs — al final)
```

Las partes 1, 2, 3 son independientes entre sí en su estructura pero el ítem 4.5 modifica `ArticuloService` que ya fue tocado en 3.5, así que la parte 4 debe ir después de la parte 3.
La parte 5 modifica también `ArticuloService` y debe ir después de la parte 4.

## Archivos nuevos a crear (paths absolutos)

| Archivo | Descripción |
|---|---|
| `src/main/java/com/uniquindio/nexuscultural/application/service/CatalogoService.java` | Servicio de catálogo público |
| `src/main/java/com/uniquindio/nexuscultural/infraestructure/rest/CatalogoController.java` | Controller de catálogo y búsqueda |
| `src/main/java/com/uniquindio/nexuscultural/domain/entity/CarritoItem.java` | Entidad carrito |
| `src/main/java/com/uniquindio/nexuscultural/domain/repository/CarritoRepository.java` | Puerto carrito |
| `src/main/java/com/uniquindio/nexuscultural/infraestructure/persistence/CarritoRepositoryEnMemoria.java` | Adaptador en memoria carrito |
| `src/main/java/com/uniquindio/nexuscultural/application/dto/request/AgregarItemCarritoRequest.java` | DTO request carrito |
| `src/main/java/com/uniquindio/nexuscultural/application/dto/request/ActualizarCantidadItemRequest.java` | DTO request cantidad |
| `src/main/java/com/uniquindio/nexuscultural/application/dto/response/CarritoItemResponse.java` | DTO response carrito |
| `src/main/java/com/uniquindio/nexuscultural/application/service/CarritoService.java` | Servicio carrito |
| `src/main/java/com/uniquindio/nexuscultural/infraestructure/rest/CarritoController.java` | Controller carrito |
| `src/main/java/com/uniquindio/nexuscultural/domain/valueobject/Descuento.java` | Value object descuento |
| `src/main/java/com/uniquindio/nexuscultural/application/dto/request/AplicarDescuentoRequest.java` | DTO request descuento |
| `src/main/java/com/uniquindio/nexuscultural/domain/valueobject/Logro.java` | Enum logros |
| `src/main/java/com/uniquindio/nexuscultural/application/dto/response/LogroResponse.java` | DTO response logro |
| `src/main/java/com/uniquindio/nexuscultural/application/service/LogroService.java` | Servicio gamificación |

## Archivos a modificar (paths absolutos)

| Archivo | Qué cambia |
|---|---|
| `src/main/java/com/uniquindio/nexuscultural/domain/repository/ArticuloRepository.java` | +4 métodos de consulta |
| `src/main/java/com/uniquindio/nexuscultural/infraestructure/persistence/ArticuloRepositoryEnMemoria.java` | +4 implementaciones |
| `src/main/java/com/uniquindio/nexuscultural/infraestructure/config/BeanConfiguration.java` | +bean CarritoRepository |
| `src/main/java/com/uniquindio/nexuscultural/domain/entity/Articulo.java` | +campo descuento, +3 métodos |
| `src/main/java/com/uniquindio/nexuscultural/application/dto/response/ArticuloDetalleResponse.java` | +2 campos nullable |
| `src/main/java/com/uniquindio/nexuscultural/infraestructure/rest/mapper/ArticuloMapper.java` | mapear descuento |
| `src/main/java/com/uniquindio/nexuscultural/application/dto/request/AplicarDescuentoRequest.java` | nuevo (ver crear) |
| `src/main/java/com/uniquindio/nexuscultural/application/service/ArticuloService.java` | +LogroService, +aplicarDescuento, +removerDescuento, +validaciones moderación |
| `src/main/java/com/uniquindio/nexuscultural/infraestructure/rest/ArticuloController.java` | +2 endpoints descuento |
| `src/main/java/com/uniquindio/nexuscultural/domain/entity/Artesano.java` | +campo logros, +otorgarLogro, +getLogros |
| `src/main/java/com/uniquindio/nexuscultural/application/dto/response/ArtesanoResponse.java` | +campo logros |
| `src/main/java/com/uniquindio/nexuscultural/infraestructure/rest/mapper/ArtesanoMapper.java` | mapear logros |
| `src/main/java/com/uniquindio/nexuscultural/infraestructure/rest/ArtesanoController.java` | +endpoint /logros |
| `docs/api/mapeo-dominio.api.md` | +tablas nuevos endpoints |
