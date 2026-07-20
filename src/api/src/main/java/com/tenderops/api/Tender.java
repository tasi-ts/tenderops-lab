package com.tenderops.api;

import java.time.Instant;
import java.util.UUID;

public record Tender(
        UUID id,
        String title,
        String buyer,
        String status,
        Instant createdAt
) {
}
