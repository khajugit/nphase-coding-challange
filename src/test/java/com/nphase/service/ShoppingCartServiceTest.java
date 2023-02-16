package com.nphase.service;


import com.nphase.entity.Product;
import com.nphase.entity.ProductPriceDetails;
import com.nphase.entity.ShoppingCart;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;


public class ShoppingCartServiceTest {
    private final ShoppingCartService service = new ShoppingCartService();

    @Test
    public void calculatesPriceRequirement1() {
        ShoppingCart cart = new ShoppingCart(Arrays.asList(
                new Product("Tea", BigDecimal.valueOf(5.0), 2, "drinks"),
                new Product("Coffee", BigDecimal.valueOf(6.5), 1, "drinks")
        ));

        BigDecimal result = service.calculateTotalPrice(cart, "DefaultRule");
        Assertions.assertEquals(0, result.compareTo(BigDecimal.valueOf(16.5)));
    }

    @Test
    public void calculatesPriceRequirement2() {
        ShoppingCart cart = new ShoppingCart(Arrays.asList(
                new Product("Tea", BigDecimal.valueOf(5.0), 5, "drinks"),
                new Product("Coffee", BigDecimal.valueOf(3.5), 3, "drinks")
        ));

        BigDecimal result = service.calculateTotalPrice(cart, "QuantityRule");
        Assertions.assertEquals(0, result.compareTo(BigDecimal.valueOf(33.0)));
    }

    @Test
    public void calculatesPriceRequirement3() {
        ShoppingCart cart = new ShoppingCart(Arrays.asList(
                new Product("Tea", BigDecimal.valueOf(5.3), 2, "drinks"),
                new Product("Coffee", BigDecimal.valueOf(3.5), 2, "drinks"),
                new Product("Cheese", BigDecimal.valueOf(8.0), 2, "food")
        ));

        BigDecimal result = service.calculateTotalPrice(cart, "CategoryRule");
        Assertions.assertEquals(0, result.compareTo(BigDecimal.valueOf(31.84)));
    }

    @Test
    public void calculatesPriceRequirement4() {
        Map<String, ProductPriceDetails> productPriceDetailsMap = service.getProductPriceDetails();
        ShoppingCart cart = new ShoppingCart(Arrays.asList(
                new Product("Tea", productPriceDetailsMap.get("Tea").getPricePerUnit(), 2, "drinks"),
                new Product("Coffee", productPriceDetailsMap.get("Coffee").getPricePerUnit(), 2, "drinks"),
                new Product("Cheese", productPriceDetailsMap.get("Cheese").getPricePerUnit(), 2, "food")
        ));

        BigDecimal result = service.calculateTotalPrice(cart, "CategoryRule");
        Assertions.assertEquals(0, result.compareTo(BigDecimal.valueOf(31.84)));
    }

}