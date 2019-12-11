package com.schooltraining.storesdistribution.mapper;

import java.util.List;

import com.schooltraining.storesdistribution.entities.User;
import tk.mybatis.mapper.common.Mapper;

public interface UserMapper  extends Mapper<User>{

	public List<Integer> getRoleIdsByUserId(int userId);

	public List<User> selectUserLikeUserNameOrName(String userName);

	public List<User> selectUserLikeName(String name);

}
