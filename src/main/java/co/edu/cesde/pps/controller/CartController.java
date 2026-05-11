package co.edu.cesde.pps.controller;

import co.edu.cesde.pps.application.CartApplicationService;
import co.edu.cesde.pps.web.dto.request.AddCartItemRequest;
import co.edu.cesde.pps.web.dto.request.MergeGuestCartRequest;
import co.edu.cesde.pps.web.dto.request.UpdateCartItemQuantityRequest;
import co.edu.cesde.pps.web.dto.response.CartResponse;
import co.edu.cesde.pps.web.security.CurrentSessionResolver;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Cart", description = "Endpoints para gestión del carrito de compras")
@RestController
@RequestMapping(ApiRoutes.CART)
public class CartController {

    private final CartApplicationService cartApplicationService;
    private final CurrentSessionResolver currentSessionResolver;

    public CartController(CartApplicationService cartApplicationService,
                          CurrentSessionResolver currentSessionResolver) {
        this.cartApplicationService = cartApplicationService;
        this.currentSessionResolver = currentSessionResolver;
    }

    @Operation(summary = "Ver carrito actual", description = "Retorna el carrito activo del usuario o invitado",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Carrito obtenido exitosamente"),
            @ApiResponse(responseCode = "401", description = "Token inválido o expirado")
    })
    @GetMapping("/me")
    public CartResponse getCurrentCart(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
                                       String authorizationHeader) {
        return cartApplicationService.getCurrentCart(currentSessionResolver.resolveCurrentToken(authorizationHeader));
    }

    @Operation(summary = "Agregar producto al carrito", description = "Agrega un producto al carrito activo",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto agregado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o stock insuficiente"),
            @ApiResponse(responseCode = "401", description = "Token inválido o expirado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PostMapping("/items")
    public CartResponse addItem(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
                                String authorizationHeader,
                                @Valid @RequestBody AddCartItemRequest request) {
        return cartApplicationService.addItem(currentSessionResolver.resolveCurrentToken(authorizationHeader), request);
    }

    @Operation(summary = "Actualizar cantidad de producto", description = "Cambia la cantidad de un producto en el carrito",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cantidad actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Cantidad inválida o stock insuficiente"),
            @ApiResponse(responseCode = "401", description = "Token inválido o expirado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado en el carrito")
    })
    @PatchMapping("/items/{productId}")
    public CartResponse updateItemQuantity(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
                                           String authorizationHeader,
                                           @PathVariable Long productId,
                                           @Valid @RequestBody UpdateCartItemQuantityRequest request) {
        return cartApplicationService.updateItemQuantity(
                currentSessionResolver.resolveCurrentToken(authorizationHeader),
                productId,
                request
        );
    }

    @Operation(summary = "Eliminar producto del carrito", description = "Remueve un producto específico del carrito",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto eliminado exitosamente"),
            @ApiResponse(responseCode = "401", description = "Token inválido o expirado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado en el carrito")
    })
    @DeleteMapping("/items/{productId}")
    public CartResponse removeItem(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
                                   String authorizationHeader,
                                   @PathVariable Long productId) {
        return cartApplicationService.removeItem(currentSessionResolver.resolveCurrentToken(authorizationHeader), productId);
    }

    @Operation(summary = "Vaciar carrito", description = "Elimina todos los productos del carrito",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Carrito vaciado exitosamente"),
            @ApiResponse(responseCode = "401", description = "Token inválido o expirado")
    })
    @DeleteMapping("/items")
    public ResponseEntity<Void> clearCurrentCart(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
                                                 String authorizationHeader) {
        cartApplicationService.clearCurrentCart(currentSessionResolver.resolveCurrentToken(authorizationHeader));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Fusionar carrito de invitado", description = "Combina el carrito de invitado con el carrito del usuario registrado",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Carritos fusionados exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error en la fusión de carritos"),
            @ApiResponse(responseCode = "401", description = "Token inválido o expirado")
    })
    @PostMapping("/merge")
    public CartResponse mergeGuestCart(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
                                       String authorizationHeader,
                                       @Valid @RequestBody MergeGuestCartRequest request) {
        return cartApplicationService.mergeGuestCart(currentSessionResolver.resolveCurrentToken(authorizationHeader), request);
    }
}
