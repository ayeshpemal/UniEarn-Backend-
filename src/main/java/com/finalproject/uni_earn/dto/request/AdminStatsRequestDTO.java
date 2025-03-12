package com.finalproject.uni_earn.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AdminStatsRequestDTO {
    @Schema(description = "Start date for filtering statistics", example = "2025-01-01T00:00:00")
    private LocalDateTime startDate;

    @Schema(description = "End date for filtering statistics", example = "2025-03-01T23:59:59")
    private LocalDateTime endDate;
}
