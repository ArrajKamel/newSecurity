package com.kamel.backend.mapper;

import com.kamel.backend.dto.SellerDto;
import com.kamel.backend.model.MyUser;

public class SellerMapper {

    public static SellerDto sellerInProduct(MyUser seller){
        SellerDto sellerDto = SellerDto.builder()
                .firstname(seller.getFirstname())
                .lastname(seller.getLastname())
                .email(seller.getEmail())
                .build();
        if(seller.getPhoneNumber() != null)
            sellerDto.setPhoneNumber(seller.getPhoneNumber());
        else
            sellerDto.setPhoneNumber(null);

        return sellerDto;
    }

}
