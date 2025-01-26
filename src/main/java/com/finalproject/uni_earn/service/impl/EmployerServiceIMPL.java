package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.dto.EmployerDto;
import com.finalproject.uni_earn.dto.JobDTO;
import com.finalproject.uni_earn.entity.Employer;
import com.finalproject.uni_earn.entity.Job;
import com.finalproject.uni_earn.repo.EmployerRepo;
import com.finalproject.uni_earn.service.EmployerService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployerServiceIMPL implements EmployerService {

    @Autowired
    private EmployerRepo employerRepo;

    @Autowired
    private ModelMapper modelMapper;

}
