package com.finalproject.uni_earn.controller;

import com.finalproject.uni_earn.dto.Response.LoginResponseDTO;
import com.finalproject.uni_earn.dto.Response.UserResponseDTO;
import com.finalproject.uni_earn.dto.request.LoginRequestDTO;
import com.finalproject.uni_earn.dto.request.UpdatePasswordDTO;
import com.finalproject.uni_earn.dto.request.UserRequestDTO;
import com.finalproject.uni_earn.dto.request.UserUpdateRequestDTO;
import com.finalproject.uni_earn.entity.User;
import com.finalproject.uni_earn.entity.enums.NotificationType;
import com.finalproject.uni_earn.service.UserService;
import com.finalproject.uni_earn.util.StandardResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

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

    //@PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT') or hasRole('EMPLOYER')")
    @GetMapping("/get-user-by-id/{userId}")
    public ResponseEntity<StandardResponse> getUserById(@PathVariable Long userId){
        UserResponseDTO userResponseDTO = userService.getUser(userId);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "Success", userResponseDTO),
                HttpStatus.OK
        );
    }
    
    //PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT') or hasRole('EMPLOYER')")
    @PutMapping("/update/{userId}")
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
    @PutMapping("/{userId}/profile-picture")
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
    public RedirectView verifyEmail(@RequestParam String token) {
        String url = userService.verifyUser(token);
        return new RedirectView(url);
    }

    @GetMapping("/resend-verification-email")
    public ResponseEntity<StandardResponse> resendVerificationEmail(@RequestParam String username) {
        String message = userService.resendVerificationEmail(username);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "Success", message),
                HttpStatus.OK
        );
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("users/{userId}/delete")
    public ResponseEntity<StandardResponse> DeleteUser(
            @PathVariable Long userId) {
        String message = userService.deleteUser(userId);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "User deleted successfully", message),
                HttpStatus.OK
        );
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/{userId}/restore")
    public ResponseEntity<StandardResponse> restoreUser(
            @PathVariable Long userId) {
        String message = userService.restoreUser(userId);
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(200, "User successfully restored.", message),
                HttpStatus.OK
        );
    }

    //@PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT') or hasRole('EMPLOYER')")
    @PutMapping("/update-password/{userId}")
    public ResponseEntity<StandardResponse> updatePassword(
            @PathVariable Long userId,
            @RequestBody UpdatePasswordDTO updatePasswordDTO) {

        userService.updatePassword(userId, updatePasswordDTO.getOldPassword(), updatePasswordDTO.getNewPassword());
        return new  ResponseEntity<StandardResponse>(
                new StandardResponse(200, "Success", "Password updated successfully"),
                HttpStatus.OK
        );
    }

    //@PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT') or hasRole('EMPLOYER')")
    @GetMapping("/public-notifications")
    public ResponseEntity<StandardResponse> getPublicAdminNotifications(
            @RequestParam(required = false) Long userId,
            @RequestParam(value = "type") NotificationType type,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return new ResponseEntity<>(
                new StandardResponse(200, "Success", userService.getPublicAdminNotifications(userId, type, page, size)),
                HttpStatus.OK
        );
    }
}
