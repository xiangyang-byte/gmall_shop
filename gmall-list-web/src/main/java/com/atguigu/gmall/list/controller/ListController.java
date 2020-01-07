package com.atguigu.gmall.list.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.service.ListService;
import com.atguigu.gmall.service.ManageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Controller
public class ListController {

    @Reference
    private ListService listService;

    @Reference
    private ManageService manageService;

    @RequestMapping("list.html")
    //@ResponseBody
    public String getList(SkuLsParams skuLsParams, Model model){
        //设置分页每页个数
        skuLsParams.setPageSize(4);

        SkuLsResult skuLsResult = listService.search(skuLsParams);
        //获取sku列表。
        List<SkuLsInfo> skuLsInfoList = skuLsResult.getSkuLsInfoList();
        //获取平台属性信息
        List<String> attrValueIdList = skuLsResult.getAttrValueIdList();
        List<BaseAttrInfo> baseAttrInfoList = manageService.getAttrValue(attrValueIdList);
        //获取已选择的属性值列表
        String urlParam = makeUrlParam(skuLsParams);

        ArrayList<BaseAttrValue> baseAttrValueArrayList = new ArrayList<>();
        //比较选中的属性值 和 查询结果的属性值
        for (Iterator<BaseAttrInfo> iterator = baseAttrInfoList.iterator(); iterator.hasNext(); ) {
            BaseAttrInfo baseAttrInfo =  iterator.next();
            //获取平台属性值的集合
            List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
            for (BaseAttrValue attrValue : attrValueList) {
                if(skuLsParams.getValueId() != null && skuLsParams.getValueId().length >0){
                    for (String valueId : skuLsParams.getValueId()) {
                        //选中的属性值 和 查询结果的属性值
                        if(valueId.equals(attrValue.getId())){
                            //删除attrValue
                            iterator.remove();

                            //面包屑 由平台属性名称+平台属性值名称
                            BaseAttrValue baseAttrValue = new BaseAttrValue();
                            baseAttrValue.setValueName(baseAttrInfo.getAttrName()+":"+attrValue.getValueName());
                            //重构makeUrlParam方法
                            String makeUrlParam = makeUrlParam(skuLsParams,valueId);
                            baseAttrValue.setUrlParam(makeUrlParam);
                            baseAttrValueArrayList.add(baseAttrValue);
                        }
                    }
                }
            }
        }

        model.addAttribute("keyword",skuLsParams.getKeyword());
        model.addAttribute("baseAttrValueArrayList",baseAttrValueArrayList);
        model.addAttribute("totalPages",skuLsResult.getTotalPages());
        model.addAttribute("pageNo",skuLsParams.getPageNo());
        model.addAttribute("urlParam",urlParam);
        model.addAttribute("baseAttrInfoList",baseAttrInfoList);
        model.addAttribute("skuLsInfoList",skuLsInfoList);
        return "list";
    }

    /**
     * 制作查询参数
     * @param skuLsParams
     * @return
     */
    private String makeUrlParam(SkuLsParams skuLsParams,String ... excludeValueIds) {

        String urlParam = "";
        //判断是否为全文检索keyword搜索
        //http://list.gmall.com/list.html?keyword=小米
        if(skuLsParams.getKeyword() != null && skuLsParams.getKeyword().length() >0){
            urlParam+="keyword="+skuLsParams.getKeyword();
        }
        //判断是否是三级分类id进行搜索
        //http://list.gmall.com/list.html?catalog3Id=61
        if(skuLsParams.getCatalog3Id() != null && skuLsParams.getCatalog3Id().length()>0){
            urlParam+="catalog3Id="+skuLsParams.getCatalog3Id();
        }
        // 判断用户是否输入的平台属性值Id 检索条件
        // http://list.gmall.com/list.html?catalog3Id=61&valueId=82
        if(skuLsParams.getValueId() != null && skuLsParams.getValueId().length >0){
            for (String valueId : skuLsParams.getValueId()) {
                //重构makeUrlParam方法
                if(excludeValueIds != null && excludeValueIds.length > 0){
                    String excludeValueId = excludeValueIds[0];
                    if(excludeValueId.equals(valueId)){
                        // 跳出代码，后面的参数则不会继续追加【后续代码不会执行】
                        // 不能写break；如果写了break；其他条件则无法拼接！
                        continue;
                    }
                }
                urlParam+="&valueId="+valueId;
            }
        }
        return urlParam;
    }
}
