package com.finalproject.uni_earn.dto.Paginated;

import com.finalproject.uni_earn.dto.AdminNotificationDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PaginatedAdminNotificationDTO {
    private List<AdminNotificationDTO> notifications;
    private int totalNotifications;
}
