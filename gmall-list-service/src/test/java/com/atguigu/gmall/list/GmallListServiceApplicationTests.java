package com.atguigu.gmall.list;


import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GmallListServiceApplicationTests {

    @Autowired
    private JestClient jestClient;

    @Test
    public void contextLoads() {
    }

    @Test
    public void test() throws IOException {
        /*
      1. 定义dsl 语句
            {
              "query": {
               "match": {
                 "actorList.name": "张译"
               }
              }
            }
      2. 定义执行的动作
         GET movie_chn/movie/_search
      3. 执行
      4. 获取结果
       */

        String query = "{\n" +
                "  \"query\": {\n" +
                "    \"term\": {\n" +
                "      \"actorList.name\": \"张译\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
        Search search = new Search.Builder(query).addIndex("movie_chn").addType("movie").build();
        //执行查询
        SearchResult searchResult = jestClient.execute(search);
        //获取数据
        List<SearchResult.Hit<Map, Void>> hits = searchResult.getHits(Map.class);

        for (SearchResult.Hit<Map, Void> hit : hits) {
            Map map = hit.source;
            System.out.println("map:"+map);
        }
    }



}
