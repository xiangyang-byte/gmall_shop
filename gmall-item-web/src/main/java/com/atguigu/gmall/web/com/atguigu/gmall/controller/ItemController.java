package com.atguigu.gmall.web.com.atguigu.gmall.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.bean.SkuSaleAttrValue;
import com.atguigu.gmall.bean.SpuSaleAttr;
import com.atguigu.gmall.service.ManageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;

@Controller
public class ItemController {

    @Reference
    private ManageService manageService;

    @RequestMapping("{skuId}.html")
    public String skuInfoPage(@PathVariable(value = "skuId") String skuId, Model model){
        //通过skuId查询出skuInfo信息。并渲染。
        SkuInfo skuInfo = manageService.getSkuInfoPage(skuId);
        //通过skuInfo查询销售属性集合
        List<SpuSaleAttr> spuSaleAttrList =  manageService.getSpuSaleAttrListCheckBySku(skuInfo);
        //通过spuId查询销售属性值集合
        List<SkuSaleAttrValue> skuSaleAttrValueList = manageService.getSkuSaleAttrValueListBySpu(skuInfo.getSpuId());
        //拼接字符串
        String key = "";
        HashMap<String, String> map = new HashMap<>();
        //判断集合是否为空
        if(skuSaleAttrValueList!= null && skuSaleAttrValueList.size()>0){
            //遍历skuSaleAttrValueList集合，拼接字符串

            for (int i = 0; i < skuSaleAttrValueList.size() ; i++) {

                SkuSaleAttrValue skuSaleAttrValue = skuSaleAttrValueList.get(i);

                //判断拼接什么时候用 | 拼接
                if (key.length() > 0) {
                    key += "|";
                }
                //拼接key,value值
                key += skuSaleAttrValue.getSaleAttrValueId();
                //判断拼接结束时间
                if ((i + 1) == skuSaleAttrValueList.size() || !skuSaleAttrValue.getSkuId().equals(skuSaleAttrValueList.get((i + 1)).getSkuId())) {

                    map.put(key, skuSaleAttrValue.getSkuId());
                    //清空key
                    key = "";
                }
            }
        }
        String valuesSkuJson = JSON.toJSONString(map);

        model.addAttribute("valuesSkuJson",valuesSkuJson);

        model.addAttribute("spuSaleAttrList",spuSaleAttrList);

        model.addAttribute("skuInfo",skuInfo);
        return "item";
    }
}
