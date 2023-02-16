package com.nphase.entity;

import java.math.BigDecimal;

public class ProductPriceDetails {
    private String name;
    private BigDecimal pricePerUnit;
    private int discountOnItem;

    public ProductPriceDetails(String name, BigDecimal pricePerUnit, int discountOnItem) {
        this.name = name;
        this.pricePerUnit = pricePerUnit;
        this.discountOnItem = discountOnItem;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }


    public int getDiscountOnItem() {
        return discountOnItem;
    }

    @Override
    public String toString() {
        return "ProductPriceDetails{" +
                "name='" + name + '\'' +
                ", pricePerUnit=" + pricePerUnit +
                ", discountOnItem=" + discountOnItem +
                '}';
    }
}
