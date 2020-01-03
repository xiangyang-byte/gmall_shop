package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.*;

import java.util.List;

public interface ManageService {

    /**
     * 查询所有一级分类
     * @return
     */
    List<BaseCatalog1> getCatalog1();
    /**
     * 通过二级分类(catalog1Id)查询二级分类信息
     * @param baseCatalog2
     * @return
     */
    List<BaseCatalog2> getCatalog2(BaseCatalog2 baseCatalog2);
    /**
     * 查询三级分类
     * @param baseCatalog3
     * @return
     */
    List<BaseCatalog3> getCatalog3(BaseCatalog3 baseCatalog3);
    /**
     * 查询平台属性信息
     * @param baseAttrInfo
     * @return
     */
    List<BaseAttrInfo> attrInfoList(BaseAttrInfo baseAttrInfo);
    /**
     * 添加和修改平台和平台属性值信息
     * @param baseAttrInfo
     */
    void saveAttrInfo(BaseAttrInfo baseAttrInfo);
    /**
     * 回显信息
     * @param attrId
     * @return
     */
    BaseAttrInfo getAttrValue(String attrId);
    /**
     * 查询所有SpuInfo
     * @return
     */
    List<SpuInfo> getSpuInfoList();
    /**
     * 查询所以销售属性
     * @return
     */
    List<BaseSaleAttr> getBaseSaleAttrList();
    /**
     * spu添加保存
     * @param spuInfo
     */
    void saveSpuInfo(SpuInfo spuInfo);
    /**
     * 获取spuImage中的所有图片列表
     * @param spuImage
     * @return
     */
    List<SpuImage> spuImageList(SpuImage spuImage);
    /**
     * 销售属性回显
     * @param spuId
     * @return
     */
    List<SpuSaleAttr> getSpuSaleAttrList(String spuId);

    /**
     * 根据三级分类id回显平台属性信息
     * @param catalog3Id
     * @return
     */
    List<BaseAttrInfo> getAttrInfoList(String catalog3Id);

    /**
     * sku添加保存
     * @param skuInfo
     */
    void saveSkuInfo(SkuInfo skuInfo);

    /**
     * 通过skuId获取商品详情页
     * @param skuId
     * @return
     */
    SkuInfo getSkuInfoPage(String skuId);

    /**
     * 通过skuInfo查询销售属性信息
     * @param skuInfo
     * @return
     */
    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(SkuInfo skuInfo);

    /**
     * 通过spuId查询销售属性值集合
     * @param spuId
     * @return
     */
    List<SkuSaleAttrValue> getSkuSaleAttrValueListBySpu(String spuId);
}
