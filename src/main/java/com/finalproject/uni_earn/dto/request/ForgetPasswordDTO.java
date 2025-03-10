package com.finalproject.uni_earn.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ForgetPasswordDTO {
    private String token;
    private String newPassword;
}
