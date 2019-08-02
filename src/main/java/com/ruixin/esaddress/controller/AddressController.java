package com.ruixin.esaddress.controller;

import com.ruixin.esaddress.service.AddressService;
import com.ruixin.esaddress.util.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Api(value = "地址数据源信息服务", tags = "AddressApi")
@RequestMapping("address")
public class AddressController {

   /* @Autowired
    private AddressService addressService;

    @ApiOperation(value = "获取所有测试地址数据", notes = "普通查询", response = ApiResult.class)
    @RequestMapping(value="/list",method= RequestMethod.GET,produces="application/json")
    public ApiResult getall() {
        return ApiResult.prepare().success(addressService.getAll());
    }*/
}
