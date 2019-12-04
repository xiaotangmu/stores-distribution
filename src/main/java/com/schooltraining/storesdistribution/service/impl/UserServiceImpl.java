package com.schooltraining.storesdistribution.service.impl;

import com.schooltraining.storesdistribution.mapper.UserMapper;
import com.schooltraining.storesdistribution.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    UserMapper userMapper;


}
