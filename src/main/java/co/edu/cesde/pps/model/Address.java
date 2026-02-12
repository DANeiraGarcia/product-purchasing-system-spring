package co.edu.cesde.pps.model;

import co.edu.cesde.pps.enums.AddressType;
import java.util.Objects;

/**
 * Entidad Address - Representa direcciones de envío y/o facturación de un usuario.
 *
 * Un usuario puede tener múltiples direcciones (ej: casa, oficina).
 * Cada dirección tiene un tipo: SHIPPING (envío) o BILLING (facturación).
 *
 * Campos:
 * - addressId: Identificador único de la dirección (PK)
 * - user: Usuario propietario de la dirección (N:1 con User)
 * - type: Tipo de dirección (SHIPPING o BILLING)
 * - line1: Línea 1 de dirección (calle, número)
 * - line2: Línea 2 de dirección (apartamento, piso) - opcional
 * - city: Ciudad
 * - state: Estado/Departamento/Provincia
 * - country: País
 * - postalCode: Código postal
 * - isDefault: Indica si es la dirección por defecto del usuario
 *
 * Relaciones:
 * - N:1 con User (muchas direcciones pertenecen a un usuario)
 * - 1:N con Order (como shipping_address_id o billing_address_id)
 */
public class Address {

    private Long addressId;
    private User user;
    private AddressType type;
    private String line1;
    private String line2;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private Boolean isDefault;

    // Constructor vacío (requerido para JPA futuro)
    public Address() {
    }

    // Constructor con campos obligatorios
    public Address(User user, AddressType type, String line1, String city,
                   String state, String country, String postalCode) {
        this.user = user;
        this.type = type;
        this.line1 = line1;
        this.city = city;
        this.state = state;
        this.country = country;
        this.postalCode = postalCode;
        this.isDefault = false;
    }

    // Constructor completo (excepto ID autogenerado)
    public Address(User user, AddressType type, String line1, String line2,
                   String city, String state, String country, String postalCode, Boolean isDefault) {
        this.user = user;
        this.type = type;
        this.line1 = line1;
        this.line2 = line2;
        this.city = city;
        this.state = state;
        this.country = country;
        this.postalCode = postalCode;
        this.isDefault = isDefault != null ? isDefault : false;
    }

    // Getters y Setters

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AddressType getType() {
        return type;
    }

    public void setType(AddressType type) {
        this.type = type;
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    // equals y hashCode basados en ID

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

    // toString sin navegación a objetos relacionados (solo IDs)

    @Override
    public String toString() {
        return "Address{" +
                "addressId=" + addressId +
                ", userId=" + (user != null ? user.getUserId() : null) +
                ", type=" + type +
                ", line1='" + line1 + '\'' +
                ", line2='" + line2 + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", isDefault=" + isDefault +
                '}';
    }
}
