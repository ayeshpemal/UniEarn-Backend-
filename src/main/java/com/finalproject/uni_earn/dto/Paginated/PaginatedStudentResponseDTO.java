package com.finalproject.uni_earn.dto.Paginated;

import com.finalproject.uni_earn.dto.StudentDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PaginatedStudentResponseDTO {
    private List<StudentDTO> students;
    private long totalStudents;
}
