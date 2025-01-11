package com.finalproject.uni_earn.controller;

import com.finalproject.uni_earn.dto.EmployerDto;
import com.finalproject.uni_earn.service.EmployerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employers")
public class EmployerController {

    @Autowired
    private EmployerService employerService;


}
