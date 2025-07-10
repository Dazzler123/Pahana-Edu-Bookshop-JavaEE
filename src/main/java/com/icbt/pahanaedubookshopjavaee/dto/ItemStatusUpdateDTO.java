package com.icbt.pahanaedubookshopjavaee.dto;

public class ItemStatusUpdateDTO {

    private String itemCode;
    private char status;

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public char getStatus() {
        return status;
    }

    public void setStatus(char status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ItemStatusUpdateDTO{" +
                "itemCode='" + itemCode + '\'' +
                ", status=" + status +
                '}';
    }
}
