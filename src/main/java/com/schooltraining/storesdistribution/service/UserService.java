package com.schooltraining.storesdistribution.service;

import java.util.List;

import com.schooltraining.storesdistribution.entities.Role;
import com.schooltraining.storesdistribution.entities.User;

public interface UserService {
    
    public User getUser(Integer id);

    public int setUser(User user);

    public User login(User user);

    public void addUserToken(String token, String userId);

	public List<User> getUserLikeName(String name);
	
	public List<Integer> getRoleIdsByUserId(int userId);

	public void setUserCache(User user);

	public User getUserInfo(int userId);
	
	public List<Role> getRolesByUserIds(int userId);

	public int update(User user);

	public List<User> getAll();

	public User addUser(User user);

	int getUserByUserName(String userName);//判断是否存在用户，注册时使用

	int updateRole(Integer id, Integer roleId);
}
