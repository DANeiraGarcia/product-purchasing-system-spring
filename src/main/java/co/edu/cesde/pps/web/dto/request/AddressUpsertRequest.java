package co.edu.cesde.pps.web.dto.request;

import co.edu.cesde.pps.enums.AddressType;

public record AddressUpsertRequest(
        AddressType type,
        String line1,
        String line2,
        String city,
        String state,
        String country,
        String postalCode,
        Boolean isDefault
) {
}