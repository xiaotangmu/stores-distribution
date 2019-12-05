package com.schooltraining.storesdistribution.service;

import com.schooltraining.storesdistribution.entities.Department;
import com.schooltraining.storesdistribution.mapper.DepartmentMapper;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {

    @Autowired
    DepartmentMapper departmentMapper;

    public List<Department> getDeptAll(){
        List<Department> departments = departmentMapper.selectAll();
        return departments;
    }

    public Department getById(int id){
        return departmentMapper.findById(id);
    }

    public int addDept(Department dept){
        return departmentMapper.addDept(dept);
    }
}
