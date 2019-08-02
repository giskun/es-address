package com.ruixin.esaddress.mapper;


import com.ruixin.esaddress.vo.Address;

import java.util.List;

public interface AddressMapper {
    List<Address> selectList();

    int insertList(List<Address> list);
}
