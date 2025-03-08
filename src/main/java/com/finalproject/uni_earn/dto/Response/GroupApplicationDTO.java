package com.finalproject.uni_earn.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GroupApplicationDTO {
    private Long applicationId;
    private Long groupId;
    private String groupName;
    private List<GroupMemberDTO> members;
}
