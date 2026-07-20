package com.tenderops.api;

import jakarta.validation.constraints.NotBlank;

public record CreateTenderRequest(
        @NotBlank String title,
        @NotBlank String buyer
) {
}
