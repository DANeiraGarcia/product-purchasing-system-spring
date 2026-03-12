package co.edu.cesde.pps.model;

import co.edu.cesde.pps.enums.AddressType;
import java.util.Objects;
import co.edu.cesde.pps.model.User;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long addressId;

    @ManyToOne(fetch = FetchType.LAZY) // Muchos direcciones -> Un usuario
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference("user-addresses")
    private User user;

    @Enumerated(EnumType.STRING) // Importante para que el Enum se guarde como texto (HOME, WORK, etc.)
    @Column(name = "address_type", nullable = false)
    private AddressType type;
    @Column(nullable = false, length = 150)
    private String line1;
    @Column(length = 150)
    private String line2;
    @Column(nullable = false, length = 100)
    private String city;
    @Column(nullable = false, length = 100)
    private String state;
    @Column(nullable = false, length = 100)
    private String country;
    @Column(name = "postal_code", length = 20)
    private String postalCode;

    // ESTA ES LA FORMA CORRECTA:
    @Column(name = "is_default")
    @Builder.Default
    private Boolean isDefault = false;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(addressId, address.addressId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(addressId);
    }

    @Override
    public String toString() {
        return "Address{" +
                "addressId=" + addressId +
                ", userId=" + (user != null ? user.getUserId() : null) +
                ", type=" + type +
                ", city='" + city + '\'' +
                ", isDefault=" + isDefault +
                '}';
    }
}


    // Constructor vacío (requerido para JPA futuro)


    // Constructor con campos obligatorios


    // Constructor completo (excepto ID autogenerado)


    // Getters y Setters




    // equals y hashCode basados en ID



    // toString sin navegación a objetos relacionados (solo IDs)







