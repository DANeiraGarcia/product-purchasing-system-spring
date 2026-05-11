package co.edu.cesde.pps.controller;

import co.edu.cesde.pps.application.AddressApplicationService;
import co.edu.cesde.pps.web.dto.request.AddressUpsertRequest;
import co.edu.cesde.pps.web.dto.response.AddressResponse;
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

@Tag(name = "Addresses", description = "Endpoints para gestión de direcciones del usuario")
@RestController
@RequestMapping(ApiRoutes.USER_ADDRESSES)
public class AddressController {

    private final AddressApplicationService addressApplicationService;
    private final CurrentSessionResolver currentSessionResolver;

    public AddressController(AddressApplicationService addressApplicationService,
                             CurrentSessionResolver currentSessionResolver) {
        this.addressApplicationService = addressApplicationService;
        this.currentSessionResolver = currentSessionResolver;
    }

    @Operation(summary = "Listar mis direcciones", description = "Retorna todas las direcciones del usuario autenticado",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de direcciones obtenida exitosamente"),
            @ApiResponse(responseCode = "401", description = "Token inválido o expirado")
    })
    @GetMapping
    public List<AddressResponse> listMyAddresses(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
                                                 String authorizationHeader) {
        return addressApplicationService.listMyAddresses(currentSessionResolver.resolveCurrentToken(authorizationHeader));
    }

    @Operation(summary = "Ver dirección por ID", description = "Retorna una dirección específica del usuario",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dirección encontrada exitosamente"),
            @ApiResponse(responseCode = "401", description = "Token inválido o expirado"),
            @ApiResponse(responseCode = "404", description = "Dirección no encontrada")
    })
    @GetMapping("/{id}")
    public AddressResponse getMyAddress(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
                                        String authorizationHeader,
                                        @PathVariable Long id) {
        return addressApplicationService.getMyAddress(currentSessionResolver.resolveCurrentToken(authorizationHeader), id);
    }

    @Operation(summary = "Agregar dirección", description = "Crea una nueva dirección para el usuario autenticado",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Dirección creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "Token inválido o expirado")
    })
    @PostMapping
    public ResponseEntity<AddressResponse> addAddress(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
                                                      String authorizationHeader,
                                                      @Valid @RequestBody AddressUpsertRequest request) {
        AddressResponse response = addressApplicationService.addAddress(
                currentSessionResolver.resolveCurrentToken(authorizationHeader), request
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Actualizar dirección", description = "Actualiza una dirección existente del usuario",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dirección actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "Token inválido o expirado"),
            @ApiResponse(responseCode = "404", description = "Dirección no encontrada")
    })
    @PutMapping("/{id}")
    public AddressResponse updateAddress(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
                                         String authorizationHeader,
                                         @PathVariable Long id,
                                         @Valid @RequestBody AddressUpsertRequest request) {
        return addressApplicationService.updateAddress(currentSessionResolver.resolveCurrentToken(authorizationHeader), id, request);
    }

    @Operation(summary = "Establecer dirección por defecto", description = "Marca una dirección como la predeterminada",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dirección establecida como predeterminada"),
            @ApiResponse(responseCode = "401", description = "Token inválido o expirado"),
            @ApiResponse(responseCode = "404", description = "Dirección no encontrada")
    })
    @PatchMapping("/{id}/default")
    public AddressResponse setDefaultAddress(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
                                             String authorizationHeader,
                                             @PathVariable Long id) {
        return addressApplicationService.setDefaultAddress(currentSessionResolver.resolveCurrentToken(authorizationHeader), id);
    }

    @Operation(summary = "Eliminar dirección", description = "Elimina una dirección del usuario",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Dirección eliminada exitosamente"),
            @ApiResponse(responseCode = "401", description = "Token inválido o expirado"),
            @ApiResponse(responseCode = "404", description = "Dirección no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
                                              String authorizationHeader,
                                              @PathVariable Long id) {
        addressApplicationService.deleteAddress(currentSessionResolver.resolveCurrentToken(authorizationHeader), id);
        return ResponseEntity.noContent().build();
    }
}
