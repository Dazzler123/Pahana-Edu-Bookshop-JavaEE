package com.icbt.pahanaedubookshopjavaee.service;

import com.icbt.pahanaedubookshopjavaee.dto.PlaceOrderDTO;

public interface PlaceOrderService {
    String placeOrder(PlaceOrderDTO placeOrderDTO) throws Exception;
}

