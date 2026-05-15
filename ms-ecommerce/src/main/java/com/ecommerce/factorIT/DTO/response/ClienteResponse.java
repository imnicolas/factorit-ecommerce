package com.ecommerce.factorIT.DTO.response;

import com.ecommerce.factorIT.Model.Cliente;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClienteResponse {

    private Long id;
    private String nombre;
    private String apellido;
    private String dni;
    private boolean esVip;

    public static ClienteResponse from(Cliente cliente) {
        return ClienteResponse.builder()
                .id(cliente.getId())
                .nombre(cliente.getNombre())
                .apellido(cliente.getApellido())
                .dni(cliente.getDni())
                .esVip(cliente.isEsVip())
                .build();
    }
}
