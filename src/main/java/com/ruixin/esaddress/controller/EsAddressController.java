package com.ruixin.esaddress.controller;
import	java.util.List;

import com.google.common.base.Strings;
import com.ruixin.esaddress.service.EsAddressService;
import com.ruixin.esaddress.util.ApiResult;
import io.swagger.annotations.*;
import org.apache.lucene.spatial3d.geom.GeoShape;
import org.elasticsearch.common.geo.GeoPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * @author zfk
 * @date 2019/7/29
 * @description
 */
@RestController
@Api(value = "地址匹配服务", tags = "es-addressApi")
@RequestMapping("es-address")
public class EsAddressController {

    @Autowired
    private EsAddressService esAddressService;

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    /**
     *
     * @param name
     * @return
     */
    @GetMapping("/search")
    @ApiOperation(value = "根据地址匹配查询", response = ApiResult.class)
    public ApiResult search(@ApiParam(value ="地址描述",required = true) @RequestParam(name = "name",required = true) String name) {
        if (!Strings.isNullOrEmpty(name)) {
            return ApiResult.prepare().success(esAddressService.search(name));
        }
        return null;
    }

    /**
     * 根据地址匹配分页查询
     * @param name
     * @param page
     * @param limit
     * @return
     */
    @GetMapping("/search/page")
    @ApiOperation(value = "根据地址匹配分页查询", response = ApiResult.class)
    public ApiResult pageSearch(@ApiParam(value ="地址描述",required = true) @RequestParam(name = "name",required = true) String name,
                                @ApiParam(value ="页码(page>=0)",required = true) @RequestParam(name = "page",required = true) int page,
                                @ApiParam(value ="每页条数(limit>0)",required = true) @RequestParam(name = "limit",required = true) int limit) {

        if(page<0 || limit<=0)
            return ApiResult.prepare().error(null,200,"页码或条数输入有问题！");
        if (!Strings.isNullOrEmpty(name)) {
            return ApiResult.prepare().success(esAddressService.search(name,page,limit));
        }
        return null;
    }


    /**
     *
     * @param clat
     * @param clon
     * @param radius
     * @param limit
     * @return
     */
    @GetMapping("/search/circle")
    @ApiOperation(value = "周边搜索,查询结果已根据距中心点距离按照由近及远排序好",  response = ApiResult.class)
    public ApiResult circleSearch(@ApiParam(value ="中心点：纬度（如39.886707,可输入范围0~90）",required = true) @RequestParam(name = "clat",required = true) double clat,
                                  @ApiParam(value ="中心点：经度（如116.399699，可输入范围0~180）",required = true) @RequestParam(name = "clon",required = true) double clon,
                                  @ApiParam(value ="半径",required = true) @RequestParam(name = "radius",required = true) double radius,
                                  @ApiParam(value ="返回条数（范围为0~服务设置的最大条数）",required = false,defaultValue = "0") @RequestParam(name = "limit",required = false,defaultValue = "0") int limit) {
        if(clat>=90 || clat<0 || clon<0 || clon>180)
            return ApiResult.prepare().error(null,200,"中心点输入有问题");
        double[] center = new double[]{clat,clon};
        return ApiResult.prepare().success(esAddressService.circleQuery(center,radius,limit));
    }

    /**
     * 矩形查询
     * @param bbox 矩形范围 topleft_lat,topleft_lon,bottomright_lat,bottomright_lon
     * @param limit
     * @return
     */
    @GetMapping("/search/rect")
    @ApiOperation(value = "矩形查询",  response = ApiResult.class)
    public ApiResult rectSearch(@ApiParam(value ="矩形范围（如topleft_lat,topletlon,bottomright_lat,bottomright_lon,举例：29.351797,105.890864,29.0023,106.234）",required = true) @RequestParam(name = "bbox",required = true) String bbox,
                                  @ApiParam(value ="返回条数（范围为0~服务设置的最大条数）",required = false,defaultValue = "0") @RequestParam(name = "limit",required = false,defaultValue = "0") int limit) {
        //解析矩形范围
        String[] coordStr = bbox.split(",");
        double topleft_lat,topleft_lon,bottomright_lat,bottomright_lon=0;
        try{
            topleft_lat = Double.parseDouble(coordStr[0]);
            topleft_lon = Double.parseDouble(coordStr[1]);
            bottomright_lat = Double.parseDouble(coordStr[2]);
            bottomright_lon = Double.parseDouble(coordStr[3]);
        }catch (Exception e){
            return ApiResult.prepare().error(null,200,"矩形范围有问题，请检查！");
        }
        if(topleft_lat>=90 || topleft_lat<0 || topleft_lon<0 || topleft_lon>180 ||
                bottomright_lat>=90 || bottomright_lat<0 || bottomright_lon<0 || bottomright_lon>180)
            return ApiResult.prepare().error(null,200,"中心点输入有问题");
        double[] topleft = new double[]{topleft_lat,topleft_lon};
        double [] bottomright = new double[]{bottomright_lat,bottomright_lon};
        return ApiResult.prepare().success(esAddressService.rectQuery(topleft,bottomright,limit));
    }


    /**
     *
     * @param content
     * @param analyzeType
     * @return
     */
    @GetMapping("/getAnalyzeResult")
    @ApiOperation(value = "测试分词结果", notes = "提供接口用于查看分词结果", response = ApiResult.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "content", value = "分词内容", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "analyzeType", value = "分词类型(有三种 standard,ik_smart,ik_max_word)", dataType = "String", paramType = "query")
    })
    public ApiResult getAnalyzeResult(@RequestParam(name = "content") String content, @RequestParam(name = "analyzeType", required = false) String analyzeType) {
        return ApiResult.prepare().success(esAddressService.getAnalyzeResult(content, analyzeType));
    }




}
