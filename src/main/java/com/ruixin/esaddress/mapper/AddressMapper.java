package com.ruixin.esaddress.mapper;


import com.ruixin.esaddress.vo.Address;

import java.util.List;
import java.util.Map;

public interface AddressMapper {
    List<Address> selectList(Map<String, Object> params);

    int insertList(List<Address> list);

    int selectTotolCount();
}
