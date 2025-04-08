package com.finalproject.uni_earn.dto.request;

import com.finalproject.uni_earn.entity.Student;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TeamRequestDTO {
    private String teamName;
    private long leader;
}
