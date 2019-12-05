package com.schooltraining.storesdistribution.service;

import com.schooltraining.storesdistribution.entities.User;

public interface UserService {
    
    public User getUser(Integer id);

    public int setUser(User user);

    public User login(User user);

    public void addUserToken(String token, String userId);
    
}
