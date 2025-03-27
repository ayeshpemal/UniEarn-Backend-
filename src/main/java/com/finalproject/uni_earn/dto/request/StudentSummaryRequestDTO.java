package com.finalproject.uni_earn.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class StudentSummaryRequestDTO {
    private Long studentId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
