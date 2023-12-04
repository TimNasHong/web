package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AddressBookMapper {


    List<AddressBook> queryList(AddressBook addressBook);
@Select("select * from address_book where id=#{id}")
    AddressBook queryById(Long id);
@Insert("insert into address_book (user_id, consignee, sex, phone, province_code, province_name, city_code, city_name, district_code, district_name, detail, label, is_default)" +
        "values (#{userId},#{consignee},#{sex},#{phone},#{provinceCode},#{provinceName},#{cityCode},#{cityName},#{districtCode},#{districtName},#{detail},#{label},#{isDefault})")
    void insert(AddressBook addressBook);
@Update("update address_book set is_default=#{isDefault} where user_id=#{userId}")
    void setDefaultAddress(AddressBook addressBook);

    void updateDefaultAddress(AddressBook addressBook);
@Delete("delete from address_book where id=#{id}")
    void deleteById(Long id);

}
