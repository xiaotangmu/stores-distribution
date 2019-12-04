package com.schooltraining.storesdistribution.mapper;

import com.schooltraining.storesdistribution.entities.Department;
import tk.mybatis.mapper.common.Mapper;

public interface DepartmentMapper extends Mapper<Department>{

    public Department findById(int id);

    public int addDept(Department department);
}
