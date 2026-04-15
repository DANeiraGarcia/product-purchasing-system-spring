package co.edu.cesde.pps.web.dto.request;

public record CheckoutRequest(
        Long cartId,
        Long shippingAddressId,
        Long billingAddressId
) {
}