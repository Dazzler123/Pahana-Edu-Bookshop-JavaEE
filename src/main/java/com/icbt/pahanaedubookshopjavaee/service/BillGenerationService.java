package com.icbt.pahanaedubookshopjavaee.service;

public interface BillGenerationService {
    byte[] generateBill(String orderCode) throws Exception;
}