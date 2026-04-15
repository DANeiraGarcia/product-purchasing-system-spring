package co.edu.cesde.pps.web.dto.request;
;

public record LoginRequest(
        String email,
        String password,
        Long guestCartId
) {
}