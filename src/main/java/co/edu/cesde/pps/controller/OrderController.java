package co.edu.cesde.pps.controller;

import co.edu.cesde.pps.application.OrderApplicationService;
import co.edu.cesde.pps.web.dto.request.CheckoutRequest;
import co.edu.cesde.pps.web.dto.response.OrderResponse;
import co.edu.cesde.pps.web.security.CurrentSessionResolver;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "OrderController", description = "Endpoints para gestión de órdenes de compra")
@RestController
@RequestMapping(ApiRoutes.ORDERS)
public class OrderController {

    private final OrderApplicationService orderApplicationService;
    private final CurrentSessionResolver currentSessionResolver;

    public OrderController(OrderApplicationService orderApplicationService,
                           CurrentSessionResolver currentSessionResolver) {
        this.orderApplicationService = orderApplicationService;
        this.currentSessionResolver = currentSessionResolver;
    }

    @Operation(summary = "Realizar checkout", description = "Convierte el carrito en una orden de compra",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Orden creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Carrito vacío o datos inválidos"),
            @ApiResponse(responseCode = "401", description = "Token inválido o expirado"),
            @ApiResponse(responseCode = "404", description = "Carrito o dirección no encontrada")
    })
    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
                                                  String authorizationHeader,
                                                  @Valid @RequestBody CheckoutRequest request) {
        OrderResponse response = orderApplicationService.checkout(
                currentSessionResolver.resolveCurrentToken(authorizationHeader),
                request
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Ver mis órdenes", description = "Retorna el historial de órdenes del usuario autenticado",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de órdenes obtenida exitosamente"),
            @ApiResponse(responseCode = "401", description = "Token inválido o expirado")
    })
    @GetMapping("/me")
    public List<OrderResponse> listMyOrders(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
                                            String authorizationHeader) {
        return orderApplicationService.listMyOrders(currentSessionResolver.resolveCurrentToken(authorizationHeader));
    }

    @Operation(summary = "Ver detalle de orden", description = "Retorna el detalle completo de una orden por ID",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orden encontrada exitosamente"),
            @ApiResponse(responseCode = "401", description = "Token inválido o expirado"),
            @ApiResponse(responseCode = "404", description = "Orden no encontrada")
    })
    @GetMapping("/{id}")
    public OrderResponse getMyOrder(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
                                    String authorizationHeader,
                                    @PathVariable Long id) {
        return orderApplicationService.getMyOrder(currentSessionResolver.resolveCurrentToken(authorizationHeader), id);
    }
}