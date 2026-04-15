package co.edu.cesde.pps.web.dto.request;


public record RegisterRequest(
        String email,
        String password,
        String firstName,
        String lastName,
        String phone,
        Long guestCartId
) {
}
