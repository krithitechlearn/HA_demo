package com.shoreexcursions.app.common;

import java.util.List;

/**
 * Generic paginated response wrapper for all list endpoints.
 * Matches the pagination contract defined in LLD Section 6.4.
 *
 * @param <T> the type of content items
 */
public record PagedResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {
    /**
     * Creates a PagedResponse from a Spring Data Page object.
     *
     * @param springPage the Spring Data page
     * @param <T>        content type
     * @return wrapped PagedResponse
     */
    public static <T> PagedResponse<T> from(org.springframework.data.domain.Page<T> springPage) {
        return new PagedResponse<>(
                springPage.getContent(),
                springPage.getNumber(),
                springPage.getSize(),
                springPage.getTotalElements(),
                springPage.getTotalPages(),
                springPage.isLast()
        );
    }
}
