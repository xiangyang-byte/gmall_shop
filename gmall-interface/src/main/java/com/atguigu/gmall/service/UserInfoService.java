package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.UserAddress;
import com.atguigu.gmall.bean.UserInfo;

import java.util.List;

public interface UserInfoService {

    /**
     * 查询所有信息
     * @return
     */
    List<UserInfo> findAll();

    /**
     * 通过userId获取user信息
     * @param userId
     * @return
     */
    List<UserAddress> getUserAddressByUserId(String userId);
}
