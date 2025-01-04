package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.dto.request.EmployerRequestDto;
import com.finalproject.uni_earn.entity.Employer;
import com.finalproject.uni_earn.repo.EmployerRepo;
import com.finalproject.uni_earn.service.EmployerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class EmployerServiceIMPL implements EmployerService {

    @Autowired
    private EmployerRepo employerRepo;


}
