package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.SkuLsInfo;
import com.atguigu.gmall.bean.SkuLsParams;
import com.atguigu.gmall.bean.SkuLsResult;

public interface ListService {

    /**
     * 上架：将最新的产品新增到es上
     * @param skuLsInfo
     */
    void saveSkuLsInfo (SkuLsInfo skuLsInfo);

    /**
     * 根据用户输入条件检索查询信息
     * @param skuLsParams
     * @return
     */
    SkuLsResult search(SkuLsParams skuLsParams);
}
