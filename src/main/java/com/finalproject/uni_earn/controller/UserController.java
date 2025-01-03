package com.finalproject.uni_earn.controller;

import com.finalproject.uni_earn.dto.request.UserRequestDTO;
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
}
