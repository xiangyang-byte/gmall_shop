package com.atguigu.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.manage.constant.ManageConst;
import com.atguigu.gmall.manage.mapper.*;
import com.atguigu.gmall.service.ManageService;
import com.atguigu.gmall.util.RedisUtil;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ManageServiceImpl implements ManageService {

    @Autowired
    private BaseCatalog1Mapper baseCatalog1Mapper;

    @Autowired
    private BaseCatalog2Mapper baseCatalog2Mapper;

    @Autowired
    private BaseCatalog3Mapper baseCatalog3Mapper;

    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;

    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;

    @Resource
    private SpuInfoMapper spuInfoMapper;

    @Resource
    private BaseSaleAttrMapper baseSaleAttrMapper;

    @Resource
    private SpuImageMapper spuImageMapper;

    @Resource
    private SpuSaleAttrMapper spuSaleAttrMapper;

    @Resource
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;

    @Resource
    private SkuInfoMapper skuInfoMapper;

    @Resource
    private SkuImageMapper skuImageMapper;

    @Resource
    private SkuAttrValueMapper skuAttrValueMapper;

    @Resource
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Autowired
    private RedisUtil redisUtil;

    //http://localhost:8082/getCatalog1
    @Override
    public List<BaseCatalog1> getCatalog1() {
        return baseCatalog1Mapper.selectAll();
    }
    //http://localhost:8082/getCatalog2?catalog1Id=2
    @Override
    public List<BaseCatalog2> getCatalog2(BaseCatalog2 baseCatalog2) {
        return baseCatalog2Mapper.select(baseCatalog2);
    }

    @Override
    public List<BaseCatalog3> getCatalog3(BaseCatalog3 baseCatalog3) {
        return baseCatalog3Mapper.select(baseCatalog3);
    }

    @Override
    public List<BaseAttrInfo> attrInfoList(BaseAttrInfo baseAttrInfo) {
        return baseAttrInfoMapper.select(baseAttrInfo);
    }

    @Override
    @Transactional
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
         //判断attrId是否为null，不为null则是修改，为null则是添加
        if(baseAttrInfo.getId() != null && baseAttrInfo.getId().length() > 0){
            baseAttrInfoMapper.updateByPrimaryKeySelective(baseAttrInfo);
        }else{
            baseAttrInfoMapper.insertSelective(baseAttrInfo);
        }
        //把原属性值全部清空
        BaseAttrValue baseAttrValue = new BaseAttrValue();
        baseAttrValue.setAttrId(baseAttrInfo.getId());
        baseAttrValueMapper.delete(baseAttrValue);
        //判断添加的信息不为null并且长度大于0
        if(baseAttrInfo.getAttrValueList() != null && baseAttrInfo.getAttrValueList().size() > 0){
            for(BaseAttrValue attrValue : baseAttrInfo.getAttrValueList()){
                //获取属性自增Id
                attrValue.setAttrId(baseAttrInfo.getId());
                //添加数据
                baseAttrValueMapper.insertSelective(attrValue);
            }
        }
    }

    @Override
    public BaseAttrInfo getAttrValue(String attrId) {
        //获取属性信息
        BaseAttrInfo baseAttrInfo = baseAttrInfoMapper.selectByPrimaryKey(attrId);
        //创建属性值信息
        BaseAttrValue baseAttrValue = new BaseAttrValue();
        baseAttrValue.setAttrId(baseAttrInfo.getId());
        List<BaseAttrValue> attrValueList = baseAttrValueMapper.select(baseAttrValue);
        // 给属性对象中的属性值集合赋值
        baseAttrInfo.setAttrValueList(attrValueList);
        return baseAttrInfo;
    }

    @Override
    public List<SpuInfo> getSpuInfoList() {
        return spuInfoMapper.selectAll();
    }

    @Override
    public List<BaseSaleAttr> getBaseSaleAttrList() {
        return baseSaleAttrMapper.selectAll();
    }

    @Override
    @Transactional
    public void saveSpuInfo(SpuInfo spuInfo) {

        //添加spuinfo信息
        spuInfoMapper.insertSelective(spuInfo);

        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        //判断图片信息是否存在
        if(spuImageList != null && spuImageList.size() > 0){
            for(SpuImage spuImage : spuImageList){
                spuImage.setSpuId(spuInfo.getId());
                spuImageMapper.insertSelective(spuImage);

            }
        }

        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        //判断属性是否为空
        if(spuImageList != null && spuImageList.size() > 0){
            for(SpuSaleAttr spuSaleAttr : spuSaleAttrList){
                spuSaleAttr.setSpuId(spuInfo.getId());
                spuSaleAttrMapper.insertSelective(spuSaleAttr);

                List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
                //判断属性值是否为空
                if(spuSaleAttrValueList != null && spuSaleAttrValueList.size() > 0){
                    for(SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList){
                        spuSaleAttrValue.setSpuId(spuInfo.getId());
                        spuSaleAttrValueMapper.insertSelective(spuSaleAttrValue);
                    }
                }
            }
        }

    }

    @Override
    public List<SpuImage> spuImageList(SpuImage spuImage) {
        return spuImageMapper.select(spuImage);
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrList(String spuId) {
        return spuSaleAttrMapper.selectSpuSaleAttrList(spuId);
    }

    @Override
    public List<BaseAttrInfo> getAttrInfoList(String catalog3Id) {
        return baseAttrInfoMapper.getBaseAttrInfoListByCatalog3Id(catalog3Id);
    }

    @Override
    @Transactional
    public void saveSkuInfo(SkuInfo skuInfo) {
        //添加skuInfo
        skuInfoMapper.insertSelective(skuInfo);
        //添加skuImage
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        //判断skuImageList是否为空
        if(skuImageList != null && skuImageList.size() > 0){
            //循环遍历
            for (SkuImage skuImage : skuImageList) {
                skuImage.setSkuId(skuInfo.getId());
                skuImageMapper.insertSelective(skuImage);
            }
        }

        //添加skuAttrValue
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        //判断skuAttrValueList是否为空
        if(skuAttrValueList != null && skuAttrValueList.size() > 0){
            //循环遍历
            for (SkuAttrValue skuAttrValue : skuAttrValueList) {
                skuAttrValue.setSkuId(skuInfo.getId());
                skuAttrValueMapper.insertSelective(skuAttrValue);
            }
        }

        //添加skuSaleAttrValue
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        //判断skuSaleAttrValueList是否为空。
        if(skuSaleAttrValueList != null && skuSaleAttrValueList.size() > 0){
            //循环遍历
            for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
                skuSaleAttrValue.setSkuId(skuInfo.getId());
                skuSaleAttrValueMapper.insertSelective(skuSaleAttrValue);
            }
        }


    }

    @Override
    public SkuInfo getSkuInfoPage(String skuId) {
        SkuInfo skuInfo = null;
        Jedis jedis = null;
        try {

            //获取jedis
            jedis = redisUtil.getJedis();
            //定义key
            String skuKey = ManageConst.SKUKEY_PREFIX+skuId+ManageConst.SKUKEY_SUFFIX;

            String skuJson = jedis.get(skuKey);
            //判断skuJson是否为空，
            if(skuJson == null){
                //如果为空则从数据库中查询
                //创建config
                Config config = new Config();
                config.useSingleServer().setAddress("redis://192.168.19.131:6379");
                //创建redisson实例
                RedissonClient redissonClient = Redisson.create(config);
                //创建锁
                RLock lock = redissonClient.getLock("my-lock");
                System.out.println("分布式锁");

                //lock.lock();
                boolean res = lock.tryLock(100, 10, TimeUnit.SECONDS);
                if(res){
                    try {
                        // 业务逻辑
                        // 缓存中没有数据
                        skuInfo =  getSkuInfoDB(skuId);
                        // 判断skuInfo是否为空
                        if (skuInfo==null){
                            jedis.setex(skuKey,ManageConst.SKUKEY_TIMEOUT, "");  // key value = null
                        }
                        // 将数据放入缓存
                        jedis.setex(skuKey,ManageConst.SKUKEY_TIMEOUT, JSON.toJSONString(skuInfo));
                        return skuInfo;
                    } finally {
                        lock.unlock();
                    }
                }


            }else{
                //从缓存中获取
                skuInfo = JSON.parseObject(skuJson, SkuInfo.class);
                return skuInfo;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭缓存
            if(jedis != null){
                jedis.close();
            }

        }
        return getSkuInfoDB(skuId);
    }

    private SkuInfo getSkuInfoDB(String skuId) {
        //查询出skuInfo,库存表
        SkuInfo skuInfo = skuInfoMapper.selectByPrimaryKey(skuId);
        //获取商品详情页的图片。
        SkuImage skuImage = new SkuImage();
        skuImage.setSkuId(skuId);
        List<SkuImage> skuImageList = skuImageMapper.select(skuImage);
        skuInfo.setSkuImageList(skuImageList);
        //查询销售属性值信息
        SkuAttrValue skuAttrValue = new SkuAttrValue();
        skuAttrValue.setSkuId(skuId);
        List<SkuAttrValue> skuAttrValueList = skuAttrValueMapper.select(skuAttrValue);
        skuInfo.setSkuAttrValueList(skuAttrValueList);
        return skuInfo;
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(SkuInfo skuInfo) {
        return spuSaleAttrMapper.selectSpuSaleAttrListCheckBySku(skuInfo.getId(),skuInfo.getSpuId());
    }

    @Override
    public List<SkuSaleAttrValue> getSkuSaleAttrValueListBySpu(String spuId) {
        return skuSaleAttrValueMapper.selectSkuSaleAttrValueListBySpu(spuId);
    }

    @Override
    public List<BaseAttrInfo> getAttrValue(List<String> attrValueIdList) {

        String attrValueIds = StringUtils.join(attrValueIdList.toArray(), ',');
        List<BaseAttrInfo> baseAttrInfos =  baseAttrInfoMapper.selectAttrInfoListByIds(attrValueIds);
        return baseAttrInfos;
    }

}
