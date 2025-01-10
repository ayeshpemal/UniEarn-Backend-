package com.finalproject.uni_earn.controller;

import com.finalproject.uni_earn.dto.Response.LoginResponseDTO;
import com.finalproject.uni_earn.dto.request.LoginRequestDTO;
import com.finalproject.uni_earn.dto.request.UserRequestDTO;
import com.finalproject.uni_earn.dto.request.UserUpdateRequestDTO;
import com.finalproject.uni_earn.entity.User;
import com.finalproject.uni_earn.service.UserService;
import com.finalproject.uni_earn.util.StandardResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;
    @PostMapping("/register")
    public ResponseEntity<StandardResponse> registerUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        String message = userService.registerUser(userRequestDTO);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "success", message),
                HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<StandardResponse> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        LoginResponseDTO response = userService.login(loginRequestDTO);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, response.getMessage(), response.getToken()),
                HttpStatus.OK
        );
    }

    @PostMapping("/update/{userId}")
    public ResponseEntity<StandardResponse> updateUserDetails(
            @PathVariable Long userId,
            @RequestBody UserUpdateRequestDTO userUpdateRequestDTO) {

        String message = userService.updateUserDetails(userId, userUpdateRequestDTO);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "User updated successfully", message),
                HttpStatus.OK
        );
    }


}
