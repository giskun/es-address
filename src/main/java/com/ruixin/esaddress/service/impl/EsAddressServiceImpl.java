package com.ruixin.esaddress.service.impl;

import com.ruixin.esaddress.dao.EsAddressDao;
import com.ruixin.esaddress.mapper.AddressMapper;
import com.ruixin.esaddress.service.EsAddressService;
import com.ruixin.esaddress.vo.Address;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequest;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author zfk
 * @date 2019/7/29
 * @description
 */
@Service
public class EsAddressServiceImpl implements EsAddressService {

    @Autowired
    private EsAddressDao esAddressDao;

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    @Value("${es.initdata.init}")
    private boolean data_init;//默认是否初始化数据源

    @Value("${es.initdata.datadelete}")
    private boolean data_delete;//初始化时是否清除历史数据

    @Value("${es.initdata.datapagelimit}")
    private int pageLimit;

    @Value("${es.pinyin}")
    private boolean query_pinyin;//查询是否支持拼音

    @Value("${spring.data.elasticsearch.max_result_window}")
    private int maxReturnCount;//最大返回条数

    @Autowired
    private AddressMapper addressMapper;

    @Override
    public List<Address> search(String name) {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(structureQuery(name))
                .build();
        List<Address> list = esAddressDao.search(searchQuery).getContent();
        return list;
    }

    @Override
    public Page<Address> search(String name, int page, int limit) {
        Pageable pageable = new PageRequest(page, limit);
        QueryBuilder ikNameQuery = QueryBuilders.matchQuery("name", name);
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(ikNameQuery).withPageable(pageable)
                .build();
        Page<Address> result = esAddressDao.search(searchQuery);
        return result;
    }


    @Override
    public void saveAddress(List<Address> addresses) {
        esAddressDao.saveAll(addresses);
    }


    /**
     *
     */
    @PostConstruct
    public void initData(){
        if(!data_init){
            return;
        }
        if(data_delete){
            esAddressDao.deleteAll();
        }
        int totalCount = addressMapper.selectTotolCount();
        if(pageLimit==0) {
            pageLimit = Integer.MAX_VALUE;
        }
        int pageCount = totalCount/pageLimit + 1;
        Map<String,Object> params = new HashMap<>();
        params.put("limit",pageLimit);
        for(int i=1;i<=pageCount;i++){
            params.put("page",i);
            List<Address> list = addressMapper.selectList(params);
            if(list!=null && list.size()>0){
                saveAddress(list);
                list = null;
            }
        }

    }

    /**
     * 中文、拼音混合搜索
     *
     * @param content the content
     * @return dis max query builder
     */
    public DisMaxQueryBuilder structureQuery(String content) {
        //使用dis_max直接取多个query中，分数最高的那一个query的分数即可
        DisMaxQueryBuilder disMaxQueryBuilder = QueryBuilders.disMaxQuery();
        QueryBuilder ikNameQuery = QueryBuilders.matchQuery("name", content).boost(2f);
        disMaxQueryBuilder.add(ikNameQuery);
        if(query_pinyin){
            QueryBuilder pinyinNameQuery = QueryBuilders.matchQuery("name.pinyin", content);
            disMaxQueryBuilder.add(pinyinNameQuery);
        }
        return disMaxQueryBuilder;
    }


    /**
     * 调用 ES 获取 IK 分词后结果
     *
     * @param content      the content
     * @param analyzerType the analyzer type
     * @return analyze result
     */
    public List<AnalyzeResponse.AnalyzeToken> getAnalyzeResult(String content, String analyzerType) {
        AnalyzeRequest analyzeRequest = new AnalyzeRequest("address-index")
                .text(content)
                .analyzer(Optional.ofNullable(analyzerType).orElse("ik_max_word"));
        List<AnalyzeResponse.AnalyzeToken> tokens = elasticsearchTemplate.getClient().admin().indices()
                .analyze(analyzeRequest)
                .actionGet()
                .getTokens();
        return tokens;
    }


    /**
     *
     * @param center 中心点
     * @param radius 半径 单位m
     * @param limit
     * @return
     */
    public List<Address> circleQuery(double[] center,double radius,int limit){
        if(limit==0)
            limit=maxReturnCount;
        GeoDistanceQueryBuilder builder = QueryBuilders.geoDistanceQuery("geoPoint").point(center[0], center[1]).distance(radius, DistanceUnit.METERS);
        GeoDistanceSortBuilder sortBuilder = SortBuilders.geoDistanceSort("geoPoint",new GeoPoint(center[0], center[1])).unit(DistanceUnit.METERS).order(SortOrder.ASC);

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(builder).withSort(sortBuilder).withPageable(new PageRequest(0, limit))
                .build();
        List<Address> list = esAddressDao.search(searchQuery).getContent();
        return list;
    }

    /**
     * 拉框查询
     * @param topLeft 矩形框的左上角点【lat,lon】
     * @param bottomRight 矩形框右下角点【lat,lon】
     * @param limit
     * @return
     */
    @Override
    public List<Address> rectQuery(double[] topLeft, double[] bottomRight, int limit) {
        if(limit==0)
            limit=maxReturnCount;
        GeoBoundingBoxQueryBuilder builder = QueryBuilders.geoBoundingBoxQuery("geoPoint").setCorners(new GeoPoint(topLeft[0], topLeft[1]),new GeoPoint(bottomRight[0], bottomRight[1]));

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(builder).withPageable(new PageRequest(0, limit))
                .build();
        List<Address> list = esAddressDao.search(searchQuery).getContent();
        return list;
    }


}
