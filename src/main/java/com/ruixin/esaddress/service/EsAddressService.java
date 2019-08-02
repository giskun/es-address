package com.ruixin.esaddress.service;

import com.ruixin.esaddress.vo.Address;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author zfk
 * @date 2019/7/29
 * @description
 */
public interface EsAddressService {

    List<Address> search(String name);

    void saveAddress(List<Address> addresses);

    List<AnalyzeResponse.AnalyzeToken> getAnalyzeResult(String content, String analyzerType);

    List<Address> circleQuery(double[] center,double radius,int limit);

    List<Address> rectQuery(double[] topLeft,double[] bottomRight,int limit);

    Page<Address> search(String name,int page,int limit);
}
