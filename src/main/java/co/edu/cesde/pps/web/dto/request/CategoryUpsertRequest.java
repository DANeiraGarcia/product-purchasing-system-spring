package co.edu.cesde.pps.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record CategoryUpsertRequest(
        @PositiveOrZero Long parentId,
        @NotBlank String name,
        @NotBlank String slug
) {
}
