package co.edu.cesde.pps.controller;


import co.edu.cesde.pps.dto.ProductDTO;
import co.edu.cesde.pps.model.Product;
import co.edu.cesde.pps.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/products") // mapeo de la direccion (comunicacion con frontend)
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;  // se inyecta por constructor

    }

    @GetMapping
    public List<ProductDTO> getProducts() {
        return productService.findAllProducts();

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Long id) {
        try {
            ProductDTO product = productService.findById(id);
            return ResponseEntity.ok(product);

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("Producto con id " + id + " no encontrado");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno del servidor");
        }
    }  // estudiar responseEntity (el mas Usado)  // estudiar la implementacion de Advise
}