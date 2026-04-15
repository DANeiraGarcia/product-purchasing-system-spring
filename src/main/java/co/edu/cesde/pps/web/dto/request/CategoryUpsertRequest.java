package co.edu.cesde.pps.web.dto.request;


public record CategoryUpsertRequest(
        Long parentId,
        String name,
        String slug
) {
}
