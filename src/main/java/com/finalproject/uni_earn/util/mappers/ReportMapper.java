package com.finalproject.uni_earn.util.mappers;

import com.finalproject.uni_earn.dto.ReportDTO;
import com.finalproject.uni_earn.entity.Reports;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReportMapper {
    List<ReportDTO> entityListToDTOList(Page<Reports> Report);
    List<ReportDTO> entityListToDTOList(List<Reports> Report);
    ReportDTO entityListToDTOList(Reports updatedReport);
}
