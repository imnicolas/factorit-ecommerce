package com.ecommerce.factorIT.Controller;

import com.ecommerce.factorIT.DTO.response.ProductoResponse;
import com.ecommerce.factorIT.Service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Productos", description = "Gestión de productos")
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping("")
    @Operation(
            summary = "Listar Productos",
            description = "Devuelve todos los productos."
    )
    public ResponseEntity<List<ProductoResponse>> obtenerProductos() {
        log.info("[GET] Consultando productos");
        return ResponseEntity.ok(productoService.obtenerProductos());
    }
}

