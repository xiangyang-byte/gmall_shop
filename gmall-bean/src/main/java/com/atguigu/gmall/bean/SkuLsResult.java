package com.atguigu.gmall.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 返回的结果集
 */
@Data
public class SkuLsResult implements Serializable {
    //商品的集合
    List<SkuLsInfo> skuLsInfoList;
    //总条数
    long total;
    //总页数
    long totalPages;
    //平台属性信息
    List<String> attrValueIdList;
}
