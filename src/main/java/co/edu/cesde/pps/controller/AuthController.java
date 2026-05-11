package co.edu.cesde.pps.controller;

import co.edu.cesde.pps.application.AuthApplicationService;
import co.edu.cesde.pps.web.dto.request.LoginRequest;
import co.edu.cesde.pps.web.dto.request.RegisterRequest;
import co.edu.cesde.pps.web.dto.response.AuthSessionResponse;
import co.edu.cesde.pps.web.dto.response.UserResponse;
import co.edu.cesde.pps.web.security.CurrentSessionResolver;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "AuthController", description = "Endpoints de autenticación y gestión de sesiones")
@RestController
@RequestMapping(ApiRoutes.AUTH)
public class AuthController {

    private final AuthApplicationService authApplicationService;
    private final CurrentSessionResolver currentSessionResolver;

    public AuthController(AuthApplicationService authApplicationService,
                          CurrentSessionResolver currentSessionResolver) {
        this.authApplicationService = authApplicationService;
        this.currentSessionResolver = currentSessionResolver;
    }

    @Operation(summary = "Crear sesión de invitado", description = "Genera una sesión temporal sin registro")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Sesión creada exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/guest-session")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthSessionResponse createGuestSession() {
        return authApplicationService.createGuestSession();
    }

    @Operation(summary = "Registrar usuario", description = "Crea una nueva cuenta y retorna sesión activa")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o usuario ya existe")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthSessionResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authApplicationService.register(request));
    }

    @Operation(summary = "Iniciar sesión", description = "Autentica al usuario y retorna token de sesión")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login exitoso"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    @PostMapping("/login")
    public AuthSessionResponse login(@Valid @RequestBody LoginRequest request) {
        return authApplicationService.login(request);
    }

    @Operation(summary = "Obtener usuario actual", description = "Retorna los datos del usuario autenticado",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "401", description = "Token inválido o expirado")
    })
    @GetMapping("/me")
    public UserResponse getCurrentUser(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
                                       String authorizationHeader) {
        return authApplicationService.getCurrentUser(
                currentSessionResolver.resolveCurrentToken(authorizationHeader)
        );
    }

    @Operation(summary = "Cerrar sesión", description = "Invalida el token de sesión actual",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Sesión cerrada exitosamente"),
            @ApiResponse(responseCode = "401", description = "Token inválido o expirado")
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
                                       String authorizationHeader) {
        authApplicationService.logout(currentSessionResolver.resolveCurrentToken(authorizationHeader));
        return ResponseEntity.noContent().build();
    }
}