package com.atguigu.gmall.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * 传入参数的类 -----实体类
 */
@Data
public class SkuLsParams implements Serializable {
    //skuName
    String  keyword;
    //三级分类id
    String catalog3Id;
    //平台属性值id
    String[] valueId;

    int pageNo=1;
    //每页数量
    int pageSize=20;
}
