package com.schooltraining.storesdistribution.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("sale")
@CrossOrigin(origins =  "*", maxAge = 3600)
public class SaleController {

    //获取店铺每月的销售额

}
