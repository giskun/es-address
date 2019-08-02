package com.ruixin.esaddress.dao;

import com.ruixin.esaddress.vo.Address;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author zfk
 * @date 2019/7/29
 * @description
 */
public interface EsAddressDao extends ElasticsearchRepository<Address, Long> {
}
