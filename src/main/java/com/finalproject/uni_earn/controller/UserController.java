package com.finalproject.uni_earn.controller;

import com.finalproject.uni_earn.dto.Response.LoginResponseDTO;
import com.finalproject.uni_earn.dto.Response.UserResponseDTO;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
                new StandardResponse(201, "success", message),
                HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<StandardResponse> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        LoginResponseDTO response = userService.login(loginRequestDTO);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, response.getMessage(), response.getToken()),
                HttpStatus.OK
        );
    }

    @GetMapping("/get-user-by-id/{userId}")
    public ResponseEntity<StandardResponse> getUserById(@PathVariable Long userId){
        UserResponseDTO userResponseDTO = userService.getUser(userId);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "Success", userResponseDTO),
                HttpStatus.OK
        );
    }
    
    //PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT') or hasRole('EMPLOYER')")
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

    //@PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT') or hasRole('EMPLOYER')")
    @PostMapping("/{userId}/profile-picture")
    public ResponseEntity<StandardResponse> uploadProfilePicture(@PathVariable Long userId, @RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = userService.uploadProfilePicture(userId, file);
            return new ResponseEntity<StandardResponse>(
                    new StandardResponse(201,"Image uploaded successfully", imageUrl),
                    HttpStatus.CREATED
            );
        } catch (Exception e) {
            return new ResponseEntity<StandardResponse>(
                    new StandardResponse(400,"Failed to upload profile picture: " + e.getMessage(), null),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    //@PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT') or hasRole('EMPLOYER')")
    @GetMapping("/{userId}/profile-picture")
    public ResponseEntity<StandardResponse> getProfilePicture(@PathVariable Long userId) {
        try {
            String imageUrl = userService.getProfilePicture(userId);
            return new ResponseEntity<StandardResponse>(
                    new StandardResponse(200,"Image retrieve successfully", imageUrl),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<StandardResponse>(
                    new StandardResponse(400,"Failed to retrieve profile picture: " + e.getMessage(), null),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<StandardResponse> verifyEmail(@RequestParam String token) {
        boolean verified = userService.verifyUser(token);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "Email verified successfully", verified),
                HttpStatus.OK
        );
    }

    @DeleteMapping("users/{userId}/delete")
    public ResponseEntity<StandardResponse> DeleteUser(
            @PathVariable Long userId) {

        String message = userService.deleteUser(userId);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "User deleted successfully", message),
                HttpStatus.OK
        );
    }

    @PutMapping("/users/{userId}/restore")
    public ResponseEntity<StandardResponse> restoreUser(
            @PathVariable Long userId) {
        String message = userService.restoreUser(userId);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "User successfully restored.", message),
                HttpStatus.OK
        );
    }

    @PutMapping("/update-password/{userId}")
    public ResponseEntity<StandardResponse> updatePassword(
            @PathVariable Long userId,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {

        userService.updatePassword(userId, oldPassword, newPassword);
        return new  ResponseEntity<StandardResponse>(
                new StandardResponse(200, "Success", "Password updated successfully"),
                HttpStatus.OK
        );
    }
}
