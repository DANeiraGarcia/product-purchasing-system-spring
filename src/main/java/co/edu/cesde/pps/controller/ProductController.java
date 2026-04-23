package co.edu.cesde.pps.controller;


import co.edu.cesde.pps.service.ProductService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products") // mapeo de la direccion (comunicacion con frontend)
public class ProductController {
    private final ProductService productService;
    public ProductController(ProductService productService) {
        this.productService = productService;

    }
}
