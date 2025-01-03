package com.finalproject.uni_earn.service;

import com.finalproject.uni_earn.dto.request.UserRequestDTO;

public interface UserService {
    String registerUser(UserRequestDTO userRequestDTO);
}
