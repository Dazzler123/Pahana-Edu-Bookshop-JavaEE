package com.icbt.pahanaedubookshopjavaee.service;

import com.icbt.pahanaedubookshopjavaee.dto.PlaceOrderDTO;
import javax.json.JsonObject;

public interface PlaceOrderService {
    String placeOrder(PlaceOrderDTO placeOrderDTO) throws Exception;
    JsonObject processOrderRequest(JsonObject orderRequest);
}

