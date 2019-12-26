package com.atguigu.gmall.orderweb.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.UserAddress;
import com.atguigu.gmall.service.UserInfoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OrderController {

    @Reference
    private UserInfoService userInfoService;

    @RequestMapping("userAddress")
    public List<UserAddress> getUserAddress(String userId){
        return userInfoService.getUserAddressByUserId(userId);
    }

}
