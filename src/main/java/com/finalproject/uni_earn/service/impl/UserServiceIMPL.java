package com.finalproject.uni_earn.service.impl;

import com.finalproject.uni_earn.dto.request.UserRequestDTO;
import com.finalproject.uni_earn.entity.User;
import com.finalproject.uni_earn.repo.UserRepo;
import com.finalproject.uni_earn.service.UserService;
import org.hibernate.internal.build.AllowSysOut;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceIMPL implements UserService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public String registerUser(UserRequestDTO userRequestDTO) {
        System.out.println(userRequestDTO);
        User user = modelMapper.map(userRequestDTO, User.class);
        userRepo.save(user);
        return "User Registered with username " + user.getUserName();
    }
}
