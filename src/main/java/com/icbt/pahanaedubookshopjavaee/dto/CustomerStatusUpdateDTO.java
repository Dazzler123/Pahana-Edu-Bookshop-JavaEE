package com.icbt.pahanaedubookshopjavaee.dto;

public class CustomerStatusUpdateDTO {

    private String accountNumber;
    private char status;

    public CustomerStatusUpdateDTO() {
    }

    public CustomerStatusUpdateDTO(String accountNumber, char status) {
        this.accountNumber = accountNumber;
        this.status = status;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public char getStatus() {
        return status;
    }

    public void setStatus(char status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "CustomerStatusUpdate{" +
                "accountNumber='" + accountNumber + '\'' +
                ", status=" + status +
                '}';
    }

}
