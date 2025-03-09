package com.finalproject.uni_earn.dto;

import com.finalproject.uni_earn.dto.Response.StudentResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TeamDTO {
    private long id;
    private String teamName;
    private StudentResponseDTO leader;
    private Set<StudentResponseDTO> members;
}
