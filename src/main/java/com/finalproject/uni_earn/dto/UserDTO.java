package com.finalproject.uni_earn.dto;

import com.finalproject.uni_earn.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDTO {
    private String userName;
    private String email;
    private String password;
    private Role role;
}
