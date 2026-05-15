package com.ecommerce.factorIT.DTO.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CrearCarritoRequest {

    @NotNull(message = "El clienteId es obligatorio")
    private Long clienteId;
}
