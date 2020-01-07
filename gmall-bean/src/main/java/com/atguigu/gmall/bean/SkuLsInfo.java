package com.atguigu.gmall.bean;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuLsInfo implements Serializable {

    String id;

    BigDecimal price;
    //sku名称
    String skuName;
    //三级分类id
    String catalog3Id;
    //默认图片
    String skuDefaultImg;

    Long hotScore=0L;

    List<SkuLsAttrValue> skuAttrValueList;
}
