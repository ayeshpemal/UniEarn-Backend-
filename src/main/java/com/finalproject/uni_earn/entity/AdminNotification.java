package com.finalproject.uni_earn.entity;

import com.finalproject.uni_earn.entity.enums.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class AdminNotification extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @Column(nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", length = 50, nullable = false)
    private NotificationType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = true)
    private User recipient;

    @Column(nullable = false)
    private Boolean isRead = false;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date sentDate;

    @PrePersist
    @PreUpdate
    private void validateNotification() {
        if (type == NotificationType.REPORT || type == NotificationType.USER_SPECIFIC) {
            if (recipient == null) {
                throw new IllegalArgumentException("Recipient cannot be null for this notification type.");
            }
        }
    }
}
