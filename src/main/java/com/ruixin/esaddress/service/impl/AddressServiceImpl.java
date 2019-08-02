package com.ruixin.esaddress.service.impl;

import com.ruixin.esaddress.mapper.AddressMapper;
import com.ruixin.esaddress.service.AddressService;
import com.ruixin.esaddress.vo.Address;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

import java.util.List;


@Service
public class AddressServiceImpl implements AddressService {

    @Resource
    private AddressMapper addressMapper;

    @Override
    public List<Address> getAll() {
        return addressMapper.selectList();
    }


    /**
     * 解析poi txt为es数据源
     */
/*
    @PostConstruct
*/
    public void readShape(){

        /* 读取数据 */
        try {
            List<Address> list = new ArrayList<>();
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File("E:\\data\\network-poi\\txt\\poi-sz.txt")),
                    "UTF-8"));
            String lineTxt = null;
            Address address = null;
            while ((lineTxt = br.readLine()) != null) {

                //数据以逗号分隔
                String[] names = lineTxt.split(",");
                if(names.length!=8)//过滤非标准组织的数据格式
                    continue;
                address = new Address();
                //根据索引赋值
                address.setLat(Double.parseDouble(names[3]));
                address.setLon(Double.parseDouble(names[4]));
                address.setName(names[5].replaceAll(";","")+names[2]);
                address.setType(names[6].split(";")[0]);
                list.add(address);
                //批量插入
                if(list.size()%1000==0){
                    Long startTime = System.currentTimeMillis();
                    addressMapper.insertList(list);
                    Long endTime = System.currentTimeMillis();
                    System.out.println("1000条插入时间："+(endTime-startTime));
                    list = new ArrayList<>();

                }

            }
            //
            if(list.size()>0)
                addressMapper.insertList(list);
            br.close();
        } catch (Exception e) {
            System.err.println("read errors :" + e);
        }
    }
}
