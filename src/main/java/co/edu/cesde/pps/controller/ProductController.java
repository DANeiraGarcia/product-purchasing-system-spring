package co.edu.cesde.pps.controller;

import co.edu.cesde.pps.dto.ProductDTO;

import co.edu.cesde.pps.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import co.edu.cesde.pps.exception.EntityNotFoundException;

@RestController
@RequestMapping("/api/v1/products") // mapeo de la dirección (comunicación con frontend)
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;  // se inyecta por constructor

    }
    @Tag(
            name = "Products por ID",
            description = "Endpoints públicos para consultar el catalogo de productos"
    )
    @Operation(
            summary = "Listar productos activos",
            description = "Retorna todos los productos activos disponibles en el catálogo"
    )

    @GetMapping
    public List<ProductDTO> getProducts() {
        return productService.findAllProducts();

    }
    @Operation(
            summary = "Buscar producto por ID",
            description = "Retorna un producto por ID"
    )

    @GetMapping("/{id}")

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Producto encontrado"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Producto no encontrado"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    public ResponseEntity<?> getProduct(
            @Parameter(description = "ID del producto a consultar", example = "1")
            @PathVariable Long id) {
        try {
            ProductDTO product = productService.findById(id);
            return ResponseEntity.ok(product);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body("Producto con id " + id + " no encontrado");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno del servidor");
        }
    }  // estudiar responseEntity (el más Usado)// estudiar la implementación de Advise(coger errores)
}