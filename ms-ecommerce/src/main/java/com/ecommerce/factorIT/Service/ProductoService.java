package com.ecommerce.factorIT.Service;

import com.ecommerce.factorIT.DTO.response.ProductoResponse;
import com.ecommerce.factorIT.Repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    @Transactional(readOnly = true)
    public List<ProductoResponse> obtenerProductos() {
        return productoRepository.findAllByOrderByNombreAsc().stream()
                .map(ProductoResponse::from)
                .toList();
    }
}
