package com.finalproject.uni_earn.controller;

import com.finalproject.uni_earn.entity.EmailRequest;
import com.finalproject.uni_earn.service.impl.EmailService;
import com.finalproject.uni_earn.util.StandardResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    // Send email to the user
    @PostMapping("/send")
    public ResponseEntity<StandardResponse> sendEmail(@Valid @RequestBody EmailRequest request) {
        emailService.sendUserEmail(request);
        return new ResponseEntity<>(
                new StandardResponse(200, "Email sent successfully.", null),
                HttpStatus.OK
        );
    }
}
