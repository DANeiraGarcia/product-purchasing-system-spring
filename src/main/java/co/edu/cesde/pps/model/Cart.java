package co.edu.cesde.pps.model;

import co.edu.cesde.pps.enums.CartStatus;
import co.edu.cesde.pps.util.CalculationUtils;
import lombok.*;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entidad Cart - Carrito de compras.
 *
 * Maneja carritos tanto de usuarios registrados como de invitados (guests).
 *
 * Campos:
 * - cartId: Identificador único del carrito (PK)
 * - user: Usuario dueño del carrito (N:1 con User) - NULLABLE para invitados
 * - session: Sesión asociada al carrito (N:1 con UserSession)
 * - status: Estado del carrito (OPEN, ABANDONED, CONVERTED)
 * - createdAt: Fecha de creación del carrito
 * - updatedAt: Fecha de última actualización
 * - items: Lista de items del carrito (1:N con CartItem)
 *
 * Estados del carrito:
 * - OPEN: Carrito activo en uso
 * - ABANDONED: Carrito abandonado (inactivo > X días)
 * - CONVERTED: Carrito convertido a orden (checkout completado)
 *
 * Relaciones:
 * - N:1 con User (opcional - muchos carritos pueden pertenecer a un usuario)
 * - N:1 con UserSession (muchos carritos pertenecen a una sesión)
 * - 1:N con CartItem (un carrito tiene muchos items)
 */
@Entity // 2. Marcar como entidad
@Table(name = "carts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {

    @Id // 4. Llave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long cartId;
    @ManyToOne(fetch = FetchType.LAZY) // 5. Relación N:1 con User
    @JoinColumn(name = "user_id") // Nullable por ser opcional para invitados
    private User user; // Nullable - NULL para invitados
    @ManyToOne(fetch = FetchType.LAZY) // 6. Relación N:1 con la sesión
    @JoinColumn(name = "session_id")
    private UserSession session;

    @Enumerated(EnumType.STRING) // 7. Guardar el estado como texto (OPEN, ABANDONED...)
    @Column(nullable = false)
    @Builder.Default
    private CartStatus status = CartStatus.OPEN;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Colección para relación 1:N
    @OneToMany(mappedBy = "cart", fetch = FetchType.LAZY)
    @JsonManagedReference("cart-items")
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();

    // Método helper para calcular total del carrito
    public BigDecimal calculateTotal() {
        return CalculationUtils.calculateCartTotal(items);
    }

    // Método helper para verificar si el carrito está abierto
    public boolean isOpen() {
        return status == CartStatus.OPEN;
    }

    public boolean isGuestCart() {
        return this.user == null;
    }

    // equals y hashCode basados en ID

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cart cart = (Cart) o;
        return Objects.equals(cartId, cart.cartId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cartId);
    }

    // toString personalizado sin navegación a objetos relacionados (solo IDs y tamaño de colección)

    @Override
    public String toString() {
        return "Cart{" +
                "cartId=" + cartId +
                ", userId=" + (user != null ? user.getUserId() : null) +
                ", sessionId=" + (session != null ? session.getSessionId() : null) +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", itemsCount=" + (items != null ? items.size() : 0) +
                ", total=" + calculateTotal() +
                '}';
    }
}
