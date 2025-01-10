package com.finalproject.uni_earn.service;

import com.finalproject.uni_earn.dto.Response.LoginResponseDTO;
import com.finalproject.uni_earn.dto.request.LoginRequestDTO;
import com.finalproject.uni_earn.dto.request.UserRequestDTO;
import com.finalproject.uni_earn.dto.request.UserUpdateRequestDTO;

public interface UserService {
    String registerUser(UserRequestDTO userRequestDTO);

    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);

    String updateUserDetails(Long userId, UserUpdateRequestDTO userUpdateRequestDTO);
}
