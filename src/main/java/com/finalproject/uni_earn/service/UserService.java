package com.finalproject.uni_earn.service;

import com.finalproject.uni_earn.dto.Paginated.PaginatedAdminNotificationDTO;
import com.finalproject.uni_earn.dto.Response.LoginResponseDTO;
import com.finalproject.uni_earn.dto.Response.UserResponseDTO;
import com.finalproject.uni_earn.dto.request.LoginRequestDTO;
import com.finalproject.uni_earn.dto.request.UserRequestDTO;
import com.finalproject.uni_earn.dto.request.UserUpdateRequestDTO;
import com.finalproject.uni_earn.entity.enums.NotificationType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {
    String registerUser(UserRequestDTO userRequestDTO);

    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);
    
    UserResponseDTO getUser(Long userId);

    String updateUserDetails(Long userId, UserUpdateRequestDTO userUpdateRequestDTO);

    public String uploadProfilePicture(Long userId, MultipartFile file) throws IOException;

    String getProfilePicture(Long userId);

    String verifyUser(String token);

    String resendVerificationEmail(String username);

    String deleteUser(Long userId);

    String restoreUser(Long userId);

    void updatePassword(Long userId, String oldPassword, String newPassword);

    PaginatedAdminNotificationDTO getPublicAdminNotifications(Long userId, NotificationType type, int page, int size);

    boolean markAdminNotificationAsRead(Long notificationId);


}
