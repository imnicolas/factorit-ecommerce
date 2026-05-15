package com.ecommerce.factorIT.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private BigDecimal precio;

    // Posiblemente seŕia util agregar atributos como :
    // 'codigo'
    // 'stock'
    // 'categoria'
    // Caso de Uso :
    // Cuando tenemos Categorias de Productos
    // Para poder comprar productos en stock
    // Para normalizar el objeto Producto
}
