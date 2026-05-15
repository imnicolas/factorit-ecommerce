package com.ecommerce.factorIT.Controller;

import com.ecommerce.factorIT.DTO.response.ClienteResponse;
import com.ecommerce.factorIT.DTO.response.PageResponse;
import com.ecommerce.factorIT.Service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Clientes", description = "Consultas sobre clientes y su estado VIP")
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    @Operation(
            summary = "Buscar clientes paginado",
            description = "Devuelve clientes ordenados alfabéticamente. Acepta un parámetro 'search' opcional " +
                    "que filtra por nombre, apellido o dni."
    )
    public ResponseEntity<PageResponse<ClienteResponse>> buscarClientes(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ) {
        log.info("[GET] Buscando clientes search='{}' page={} size={}", search, page, size);
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("nombre").ascending().and(Sort.by("apellido").ascending()));
        return ResponseEntity.ok(clienteService.buscarClientes(search, pageable));
    }

    @GetMapping("/vip")
    @Operation(
            summary = "Clientes VIP del mes consultado",
            description = "Un cliente es VIP en un mes si la suma de sus carritos COMPLETADOS en ese mes " +
                    "supera los $10.000. Cálculo 100% dinámico desde la tabla carritos."
    )
    public ResponseEntity<PageResponse<ClienteResponse>> obtenerClientesVip(
            @Parameter(description = "Año a evaluar") @RequestParam int anio,
            @Parameter(description = "Mes a evaluar (1-12)") @RequestParam @Min(1) @Max(12) int mes,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ) {
        log.info("[GET] VIPs del mes {}/{} page={} size={}", mes, anio, page, size);
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("nombre").ascending().and(Sort.by("apellido").ascending()));
        return ResponseEntity.ok(clienteService.obtenerClientesVip(anio, mes, pageable));
    }

    @GetMapping("/vip/nuevos")
    @Operation(
            summary = "Clientes que pasaron a ser VIP en el mes consultado",
            description = "Son los que SUM(COMPLETADOS) > $10.000 en el mes consultado pero NO en el mes anterior."
    )
    public ResponseEntity<PageResponse<ClienteResponse>> obtenerClientesNuevosVip(
            @RequestParam int anio,
            @RequestParam @Min(1) @Max(12) int mes,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ) {
        log.info("[GET] Nuevos VIP {}/{} page={} size={}", mes, anio, page, size);
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("nombre").ascending().and(Sort.by("apellido").ascending()));
        return ResponseEntity.ok(clienteService.obtenerClientesQueGanaronVip(anio, mes, pageable));
    }

    @GetMapping("/vip/perdieron")
    @Operation(
            summary = "Clientes que dejaron de ser VIP en el mes consultado",
            description = "Son los que SUM(COMPLETADOS) > $10.000 en el mes anterior pero NO en el mes consultado."
    )
    public ResponseEntity<PageResponse<ClienteResponse>> obtenerClientesPerdieroVip(
            @RequestParam int anio,
            @RequestParam @Min(1) @Max(12) int mes,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ) {
        log.info("[GET] Perdieron VIP {}/{} page={} size={}", mes, anio, page, size);
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("nombre").ascending().and(Sort.by("apellido").ascending()));
        return ResponseEntity.ok(clienteService.obtenerClientesQuePerdieronVip(anio, mes, pageable));
    }
}
