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
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Tag(name = "Products por ID", description = "Endpoints públicos para consultar el catalogo de productos")
    @Operation(summary = "Listar productos activos", description = "Retorna todos los productos activos disponibles en el catálogo")
    @GetMapping
    public List<ProductDTO> getProducts() {
        return productService.findAllProducts();
    }

    @Operation(summary = "Buscar producto por ID", description = "Retorna un producto por ID")
    @GetMapping("/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
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
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody ProductDTO productDTO) {
        try {
            ProductDTO created = productService.createProduct(productDTO);
            return ResponseEntity.status(201).body(created);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al crear producto: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        try {
            ProductDTO updated = productService.updateProduct(id, productDTO);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body("Producto con id " + id + " no encontrado");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al actualizar producto: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body("Producto con id " + id + " no encontrado");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al eliminar producto: " + e.getMessage());
        }
    }
}