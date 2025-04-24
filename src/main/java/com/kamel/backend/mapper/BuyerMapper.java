package com.kamel.backend.mapper;

import com.kamel.backend.dto.BuyerDto;
import com.kamel.backend.dto.SellerDto;
import com.kamel.backend.model.MyUser;

public class BuyerMapper {

    public static BuyerDto buyerInCart(MyUser buyer){
        return BuyerDto.builder()
                .firstname(buyer.getFirstname())
                .lastname(buyer.getLastname())
                .email(buyer.getEmail())
                .buyerId(buyer.getId())
                .build();
    }
}
