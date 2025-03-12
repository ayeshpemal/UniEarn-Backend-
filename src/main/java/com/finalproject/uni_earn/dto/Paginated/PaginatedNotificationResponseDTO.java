package com.finalproject.uni_earn.dto.Paginated;

import com.finalproject.uni_earn.dto.NotificationDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PaginatedNotificationResponseDTO {
    List<NotificationDTO> notifications;
    Long totalNotifications;
}
