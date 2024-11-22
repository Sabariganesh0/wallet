package com.mainproject.wallet.dto;

import jakarta.validation.constraints.NotNull;

public class RechargeResponseDTO {
    @NotNull(message = "Cashback amount cannot be null")
    private double cashbackAmount;

    @NotNull(message = "New balance cannot be null")
    private double newBalance;

    public RechargeResponseDTO(double cashbackAmount, double newBalance) {
        this.cashbackAmount = cashbackAmount;
        this.newBalance = newBalance;
    }

    public double getCashbackAmount() {
        return cashbackAmount;
    }

    public double getNewBalance() {
        return newBalance;
    }
}
