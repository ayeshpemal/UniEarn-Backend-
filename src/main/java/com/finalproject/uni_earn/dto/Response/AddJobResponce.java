package com.finalproject.uni_earn.dto.Response;

import com.finalproject.uni_earn.entity.Student;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AddJobResponce {
    private String message;
    private List<Student> studentList; //forNotifications
}
