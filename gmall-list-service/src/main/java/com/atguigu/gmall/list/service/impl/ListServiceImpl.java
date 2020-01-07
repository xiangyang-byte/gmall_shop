package com.atguigu.gmall.list.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.SkuLsInfo;
import com.atguigu.gmall.bean.SkuLsParams;
import com.atguigu.gmall.bean.SkuLsResult;
import com.atguigu.gmall.service.ListService;
import io.searchbox.client.JestClient;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ListServiceImpl implements ListService {

    @Autowired
    private JestClient jestClient;

    public static final String ES_INDEX="gmall";

    public static final String ES_TYPE="SkuInfo";

    @Override
    public void saveSkuLsInfo(SkuLsInfo skuLsInfo) {
        //在es中添加信息
        Index build = new Index.Builder(skuLsInfo).index(ES_INDEX).type(ES_TYPE).id(skuLsInfo.getId()).build();

        try {
            DocumentResult execute = jestClient.execute(build);
            System.out.println(execute);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public SkuLsResult search(SkuLsParams skuLsParams) {
        //获取查询条件
        String query = makeQueryStringForSearch(skuLsParams);
        Search search = new Search.Builder(query).addIndex(ES_INDEX).addType(ES_TYPE).build();
        SearchResult searchResult = null;
        try {
            searchResult = jestClient.execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //制作返回集。
        SkuLsResult skuLsResult = makeResultForSearch(searchResult, skuLsParams);
        return skuLsResult;
    }

    /**
     * 返回结果集
     * @param skuLsParams
     * @param searchResult
     * @return
     */
    private SkuLsResult makeResultForSearch( SearchResult searchResult,SkuLsParams skuLsParams) {
        SkuLsResult skuLsResult = new SkuLsResult();
        //保存商品信息
        ArrayList<SkuLsInfo> arrayList = new ArrayList<>();
        List<SearchResult.Hit<SkuLsInfo, Void>> hits = searchResult.getHits(SkuLsInfo.class);
        if(hits != null && hits.size() >0){
            for (SearchResult.Hit<SkuLsInfo, Void> hit : hits) {
                SkuLsInfo skuLsInfo = hit.source;
                Map<String, List<String>> highlight = hit.highlight;
                //判断是否有高亮
                if(highlight != null && highlight.size() > 0){
                    List<String> stringList = highlight.get("skuName");
                    String skuInfoHI = stringList.get(0);
                    skuLsInfo.setSkuName(skuInfoHI);

                }
                arrayList.add(skuLsInfo);
            }
        }

        skuLsResult.setSkuLsInfoList(arrayList);
        //总条数
        skuLsResult.setTotal(skuLsResult.getTotal());
        // 总页数
        long totalPages = (searchResult.getTotal() + skuLsParams.getPageSize() - 1) / skuLsParams.getPageSize();
        skuLsResult.setTotalPages(totalPages);
        //平台属性信息
        ArrayList<String> stringArrayList = new ArrayList<>();
        TermsAggregation groupby_attr = searchResult.getAggregations().getTermsAggregation("groupby_attr");
       //得到平台属性的集合
        List<TermsAggregation.Entry> buckets = groupby_attr.getBuckets();
        //判断集合是否为空
        if(buckets != null && buckets.size() > 0){
            for (TermsAggregation.Entry bucket : buckets) {
                String valueId = bucket.getKey();
                stringArrayList.add(valueId);
            }
        }
        skuLsResult.setAttrValueIdList(stringArrayList);
        return skuLsResult;
    }
    /**
     * 根据检索条件生成dsl语句
     * @param skuLsParams
     * @return
     */
    private String makeQueryStringForSearch(SkuLsParams skuLsParams) {
        //dsl语句中的 {}
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //query下有bool
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //判断过滤条件是否为三级分类id
        if(skuLsParams.getCatalog3Id() != null && skuLsParams.getCatalog3Id().length()>0){
            /**
             * {"term":{"catalog3Id":"61"}}
             */
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("catalog3Id", skuLsParams.getCatalog3Id());
            boolQueryBuilder.filter(termQueryBuilder);
        }
        //判断过滤条件是否为平台属性值id
        if(skuLsParams.getValueId() != null && skuLsParams.getValueId().length > 0){
            /**
             *   {"term":{ "skuAttrValueList.valueId": "13"}},
             *   {"term":{ "skuAttrValueList.valueId": "80"}},
             */
            for (String valueId : skuLsParams.getValueId()) {
                TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId",valueId);
                boolQueryBuilder.filter(termQueryBuilder);
            }
        }
        //判断过滤条件是否为skuName
        if(skuLsParams.getKeyword() != null && skuLsParams.getKeyword().length() >0){
            /**
             * "must":
             *       { "match": { "skuName": "小米" }  }
             */
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName", skuLsParams.getKeyword());
            boolQueryBuilder.must(matchQueryBuilder);
            //设置高亮
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.preTags("<span style='color:red'>");
            highlightBuilder.postTags("</span>");
            highlightBuilder.field("skuName");
            searchSourceBuilder.highlight(highlightBuilder);
        }
        //设置分页
        //分页公式
        int from = skuLsParams.getPageSize() * (skuLsParams.getPageNo() - 1);
        searchSourceBuilder.from(from);
        searchSourceBuilder.size(skuLsParams.getPageSize());
        //设置顺序
        searchSourceBuilder.sort("hotScore", SortOrder.DESC);
        //设置聚合
        TermsBuilder groupby_attr = AggregationBuilders.terms("groupby_attr").field("skuAttrValueList.valueId.keyword");
        searchSourceBuilder.aggregation(groupby_attr);

        searchSourceBuilder.query(boolQueryBuilder);
        //把searchSourceBuilder {} 转换成字符串
        String query = searchSourceBuilder.toString();
        System.out.println("query" + query);
        return query;
    }
}
