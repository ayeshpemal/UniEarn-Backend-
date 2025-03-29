package com.finalproject.uni_earn.dto.Response;

import com.finalproject.uni_earn.entity.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class NewGroupApplicationDTO {
    private Long applicationId;
    private Long groupId;
    private String groupName;
    private ApplicationStatus applicationStatus;
    private List<GroupMemberDTO> members;
}
