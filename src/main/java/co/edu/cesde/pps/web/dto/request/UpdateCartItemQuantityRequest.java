package co.edu.cesde.pps.web.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record UpdateCartItemQuantityRequest(
        @NotNull @PositiveOrZero Integer quantity
) {
}