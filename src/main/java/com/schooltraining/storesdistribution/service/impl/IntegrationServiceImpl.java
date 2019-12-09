package com.schooltraining.storesdistribution.service.impl;

import com.schooltraining.storesdistribution.entities.User;
import com.schooltraining.storesdistribution.mapper.UserMapper;
import com.schooltraining.storesdistribution.service.IntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IntegrationServiceImpl implements IntegrationService {

    @Autowired
    UserMapper userMapper;

    @Override
    public User add(int integration, int roleId) {
        return null;
    }
}
