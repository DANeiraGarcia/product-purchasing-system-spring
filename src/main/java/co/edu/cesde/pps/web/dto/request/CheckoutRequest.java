package co.edu.cesde.pps.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CheckoutRequest(
        @NotNull @Positive Long cartId,
        @NotNull @Positive Long shippingAddressId,
        @NotNull @Positive Long billingAddressId,
        @NotBlank String paymentMethod
) {
}