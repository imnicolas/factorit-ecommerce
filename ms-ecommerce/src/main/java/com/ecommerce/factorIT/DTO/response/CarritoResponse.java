package com.ecommerce.factorIT.DTO.response;

import com.ecommerce.factorIT.Enum.EstadoCarrito;
import com.ecommerce.factorIT.Enum.TipoCarrito;
import com.ecommerce.factorIT.Model.Carrito;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class CarritoResponse {

    private Long id;
    private TipoCarrito tipo;
    private EstadoCarrito state;
    private LocalDate dateCreated;
    private BigDecimal montoBruto;
    private BigDecimal total;
    private BigDecimal descuento;
    private ClienteResponse cliente;
    private List<CarritoDetalleResponse> detalles;

    public static CarritoResponse from(Carrito carrito) {
        BigDecimal montoBruto = carrito.getMontoBruto() != null ? carrito.getMontoBruto() : BigDecimal.ZERO;
        BigDecimal total = carrito.getTotal() != null ? carrito.getTotal() : BigDecimal.ZERO;

        return CarritoResponse.builder()
                .id(carrito.getId())
                .tipo(carrito.getTipo())
                .state(carrito.getState())
                .dateCreated(carrito.getDateCreated())
                .montoBruto(montoBruto)
                .total(total)
                .descuento(montoBruto.subtract(total))
                .cliente(ClienteResponse.from(carrito.getCliente()))
                .detalles(carrito.getDetalles().stream()
                        .map(CarritoDetalleResponse::from)
                        .toList())
                .build();
    }
}
