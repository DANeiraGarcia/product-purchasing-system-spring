package co.edu.cesde.pps.model;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;
import java.util.List;
import java.util.ArrayList;


/**
 * Entidad PaymentStatus - Catálogo de estados posibles de un pago.
 *
 * Ejemplos: pending, approved, rejected, refunded
 *
 * Campos:
 * - paymentStatusId: Identificador único del estado (PK)
 * - name: Nombre único del estado (UNIQUE)
 *
 * Relaciones (futuro - etapa02):
 * - 1:N con Payment (un estado puede aplicar a múltiples pagos)
 */
@Entity
@Table(name = "payment_statuses") // Usamos el plural para la tabla
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id") // Mantenemos el estándar de nombre 'id'
    private Long paymentStatusId;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    // 2. RELACIÓN 1:N con Payment (Inversa)
    @OneToMany(mappedBy = "paymentStatus")
    @Builder.Default
    private List<Payment> payments = new ArrayList<>();

    // equals y hashCode basados en ID

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentStatus that = (PaymentStatus) o;
        return Objects.equals(paymentStatusId, that.paymentStatusId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paymentStatusId);
    }

    // toString personalizado sin navegación a objetos relacionados

    @Override
    public String toString() {
        return "PaymentStatus{" +
                "paymentStatusId=" + paymentStatusId +
                ", name='" + name + '\'' +
                '}';
    }
}

