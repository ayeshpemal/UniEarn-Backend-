package com.finalproject.uni_earn.dto.Paginated;

import com.finalproject.uni_earn.dto.Response.UserResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PaginatedEmployerResponseDTO {
    private List<UserResponseDTO> employers;
    private long totalEmployers;
}
