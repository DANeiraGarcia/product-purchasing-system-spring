package co.edu.cesde.pps.web.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MergeGuestCartRequest(
        @NotNull @Positive Long guestCartId
) {
}
