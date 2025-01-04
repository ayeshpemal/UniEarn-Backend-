package com.finalproject.uni_earn.controller;

import com.finalproject.uni_earn.dto.request.EmployerRequestDto;
import com.finalproject.uni_earn.entity.Employer;
import com.finalproject.uni_earn.service.EmployerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employers")
public class EmployerController {
    @Autowired
    private EmployerService employerService;



}
