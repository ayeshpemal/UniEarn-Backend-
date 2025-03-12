package com.finalproject.uni_earn.dto.Paginated;

import com.finalproject.uni_earn.dto.EmployerDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PaginatedEmployerResponseDTO {
    private List<EmployerDto> employers;
    private long totalEmployers;
}
