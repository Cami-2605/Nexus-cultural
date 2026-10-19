package com.uniquindio.nexuscultural.infraestructure.rest.mapper;

import com.uniquindio.nexuscultural.domain.entity.Pedido;
import com.uniquindio.nexuscultural.domain.valueobject.EstadoPedido;
import com.uniquindio.nexuscultural.domain.valueobject.Precio;
import com.uniquindio.nexuscultural.infraestructure.persistence.entity.PedidoEntity;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.math.BigDecimal;

@Component
public class PedidoMapper {

    public PedidoEntity toEntity(Pedido domain) {
        if (domain == null) return null;

        return PedidoEntity.builder()
                .id(domain.getId())
                .compradorId(domain.getCompradorId())
                .articuloId(domain.getArticuloId())
                .cantidad(domain.getCantidad())
                .totalMonto(domain.getTotal() != null ? domain.getTotal().monto() : BigDecimal.ZERO)
                .totalMoneda(domain.getTotal() != null ? domain.getTotal().moneda() : "COP")
                .fechaCreacion(domain.getFechaCreacion())
                .estado(domain.getEstado() != null ? domain.getEstado().name() : EstadoPedido.PENDIENTE.name())
                .fechaEntrega(domain.getFechaEntrega())
                .metodoPago(domain.getMetodoPago())
                .referenciaTransaccion(domain.getReferenciaTransaccion())
                .build();
    }

    public Pedido toDomain(PedidoEntity entity) {
        if (entity == null) return null;

        BigDecimal precioUnitarioMonto = entity.getTotalMonto();
        if (entity.getCantidad() != null && entity.getCantidad() > 0 && entity.getTotalMonto() != null) {
            precioUnitarioMonto = entity.getTotalMonto().divide(BigDecimal.valueOf(entity.getCantidad()), 2, java.math.RoundingMode.HALF_UP);
        }

        Precio precioUnitario = new Precio(precioUnitarioMonto, entity.getTotalMoneda() != null ? entity.getTotalMoneda() : "COP");

        // Reconstruimos la instancia usando el método estático de fábrica de tu dominio
        Pedido pedido = Pedido.crear(
                entity.getCompradorId(),
                entity.getArticuloId(),
                entity.getCantidad(),
                precioUnitario,
                entity.getFechaCreacion()
        );

        // Asignamos los campos de estado persistidos vía reflexión
        setField(pedido, "id", entity.getId());
        setField(pedido, "estado", entity.getEstado() != null ? EstadoPedido.valueOf(entity.getEstado()) : EstadoPedido.PENDIENTE);
        setField(pedido, "fechaEntrega", entity.getFechaEntrega());
        setField(pedido, "metodoPago", entity.getMetodoPago());
        setField(pedido, "referenciaTransaccion", entity.getReferenciaTransaccion());

        return pedido;
    }

    private void setField(Object object, String fieldName, Object value) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, value);
        } catch (Exception e) {
            throw new RuntimeException("Error al mapear PedidoEntity a Pedido", e);
        }
    }
}