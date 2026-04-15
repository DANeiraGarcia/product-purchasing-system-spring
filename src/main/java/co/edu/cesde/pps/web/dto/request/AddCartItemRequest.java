package co.edu.cesde.pps.web.dto.request;

public record AddCartItemRequest(
        Long productId,
        Integer quantity
) {
}
