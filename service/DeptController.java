package com.schooltraining.storesdistribution.controller;

import com.schooltraining.storesdistribution.entities.Department;
import com.schooltraining.storesdistribution.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("dept")
@CrossOrigin
public class DeptController {


    //************************ 图片上传 **********************//
    @RequestMapping("fileUpload")
    public String fileUpload(@RequestParam("file") MultipartFile multipartFile) {
        // 将图片或者音视频上传到分布式的文件存储系统
        // 将图片的存储路径返回给页面
        String imgUrl = FileUploadUtil.uploadImage(multipartFile);
        System.out.println(imgUrl);
        return imgUrl;

    }

    @Autowired
    DepartmentService departmentService;



//*********************** redis 缓存 **********************//
//    @Autowired
//    RedisUtil redisUtil;
//
//    @Autowired
//    RedissonClient redissonClient;
//
//    @RequestMapping("testRedissonGet")
//    public Object testRedisson(int id) {
//        Jedis jedis = redisUtil.getJedis();
//
//        Department dept = null;
//        RLock lock = null;
//        boolean tryLock = false;
//
//        System.out.println("进来了");
//        try {
//            // 查询缓存
//            String deptKey = "dept:" + id + ":info";//k -- Obejct:id:info
//            String deptJson = jedis.get(deptKey);
//            if (StringUtils.isNotBlank(deptJson)) {//if(deptJson!=null&&!deptJson.equals(""))
////                System.out.println("ip为"++"的同学:"+Thread.currentThread().getName()+"从缓存中获取商品详情");
//                System.out.println("查询到 " + deptKey + " 的数据：" + deptJson);
//                dept = JSON.parseObject(deptJson, Department.class);
//            } else {
//                // 如果缓存中没有，查询mysql
////                System.out.println("ip为"+ip+"的同学:"+Thread.currentThread().getName()+"发现缓存中没有，申请缓存的分布式锁："+"sku:" + skuId + ":lock");
//                System.out.println("没有查询到 " + deptKey + " 的数据：" + deptJson);
//                // 设置分布式锁
//                lock = redissonClient.getLock("dept:" + id + ":lock");// 声明锁
//                tryLock = lock.tryLock();
//                //上锁
//                if (tryLock) {//成功上锁
//                    System.out.println("上锁了");
//                    Thread.sleep(3000);
//                    dept = departmentService.getById(id);
//
//                    if (dept != null) {
//                        // mysql查询结果存入redis
//                        jedis.set("dept:" + id + ":info", JSON.toJSONString(dept));
//                    } else {
//                        // 数据库中不存在该sku
//                        // 为了防止缓存穿透将，null或者空字符串值设置给redis
//                        jedis.setex("dept:" + id + ":info", 60 * 3, JSON.toJSONString(""));
//                    }
//                } else { //上锁失败，自旋
//                    System.out.println("我在自旋");
//                    return testRedisson(id);
//                }
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }finally{
//            jedis.close();
//            if(tryLock){
//                lock.unlock();// 解锁
//            }
//        }
//        return dept;
//    }



    @GetMapping("get")
    public Object get(int id) {
        System.out.println(id);
        return departmentService.getById(id);
    }

    @GetMapping("add")
    public Object add(Department dept) {
        System.out.println(dept);
        return departmentService.addDept(dept);
    }
    @GetMapping("getAll")
    public Object getAll(){

        return departmentService.getDeptAll();
    }
}
