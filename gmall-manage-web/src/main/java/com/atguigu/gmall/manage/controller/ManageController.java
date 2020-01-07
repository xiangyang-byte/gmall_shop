package com.atguigu.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.service.ManageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class ManageController {

    @Reference
    private ManageService manageService;

    //http://localhost:8082/getCatalog1
    @RequestMapping("getCatalog1")
    public List<BaseCatalog1> getCatalog1(){
        return manageService.getCatalog1();
    }
    //http://localhost:8082/getCatalog2?catalog1Id=2
    @RequestMapping("getCatalog2")
    public List<BaseCatalog2> getCatalog2(BaseCatalog2 baseCatalog2){
        return manageService.getCatalog2(baseCatalog2);
    }

    //http://localhost:8082/getCatalog3?catalog2Id=16
    @RequestMapping("getCatalog3")
    public List<BaseCatalog3> getCatalog3(BaseCatalog3 baseCatalog3){
        return manageService.getCatalog3(baseCatalog3);
    }

    //http://localhost:8082/attrInfoList?catalog3Id=233
    @RequestMapping("attrInfoList")
    public List<BaseAttrInfo> attrInfoList(String catalog3Id,BaseAttrInfo baseAttrInfo){
        //return manageService.attrInfoList(baseAttrInfo);
        return manageService.getAttrInfoList(catalog3Id);
    }

    //http://localhost:8082/saveAttrInfo
    @RequestMapping("saveAttrInfo")
    public void saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        manageService.saveAttrInfo(baseAttrInfo);
    }

    //http://localhost:8082/getAttrValueList?attrId=100
    @RequestMapping("getAttrValueList")
    public List<BaseAttrValue> getAttrValueList(String attrId){
        BaseAttrInfo baseAttrInfo = manageService.getAttrValue(attrId);
        return baseAttrInfo.getAttrValueList();
    }







}
