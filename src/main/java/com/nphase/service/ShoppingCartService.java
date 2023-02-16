package com.nphase.service;

import com.nphase.entity.ProductPriceDetails;
import com.nphase.entity.ShoppingCart;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class ShoppingCartService {
    public BigDecimal calculateTotalPrice(ShoppingCart shoppingCart, String businessRule) {
        Map<String, BigDecimal> businessRulesAndDiscounts = this.getDiscountOfBusinessRules();
        Map<String, Integer> categoryWiseProductCount = this.getCategoryWiseProductCount(shoppingCart);
        //start
        return switch (businessRule) {
            case "DefaultRule" -> {
                yield shoppingCart.getProducts()
                        .stream()
                        .map(product -> {

                                    BigDecimal priceWithoutDiscount = product.getPricePerUnit().multiply(BigDecimal.valueOf(product.getQuantity()));
                                    BigDecimal discountAmount = priceWithoutDiscount.multiply(businessRulesAndDiscounts.get(businessRule));
                                    return priceWithoutDiscount.subtract(discountAmount);
                                }
                        )
                        .reduce(BigDecimal::add)
                        .orElse(BigDecimal.ZERO);

            }
            case "QuantityRule" -> {
                yield shoppingCart.getProducts()
                        .stream()
                        .map(product -> {
                                    if (product.getQuantity() > 3) {
                                        BigDecimal priceWithoutDiscount = product.getPricePerUnit().multiply(BigDecimal.valueOf(product.getQuantity()));
                                        BigDecimal discountAmount = priceWithoutDiscount.multiply(businessRulesAndDiscounts.get(businessRule));
                                        return priceWithoutDiscount.subtract(discountAmount);
                                    } else {
                                        return product.getPricePerUnit().multiply(BigDecimal.valueOf(product.getQuantity()));
                                    }
                                }
                        )
                        .reduce(BigDecimal::add)
                        .orElse(BigDecimal.ZERO);

            }
            case "CategoryRule" -> {
                yield shoppingCart.getProducts()
                        .stream()
                        .map(product -> {
                                    if (categoryWiseProductCount.get(product.getCategory()) > 3) {
                                        BigDecimal priceWithoutDiscount = product.getPricePerUnit().multiply(BigDecimal.valueOf(product.getQuantity()));
                                        BigDecimal discountAmount = priceWithoutDiscount.multiply(businessRulesAndDiscounts.get(businessRule));
                                        return priceWithoutDiscount.subtract(discountAmount);
                                    } else {
                                        return product.getPricePerUnit().multiply(BigDecimal.valueOf(product.getQuantity()));
                                    }
                                }
                        )
                        .reduce(BigDecimal::add)
                        .orElse(BigDecimal.ZERO);

            }
            default -> {
                yield shoppingCart.getProducts()
                        .stream()
                        .map(product -> product.getPricePerUnit().multiply(BigDecimal.valueOf(product.getQuantity()))
                        )
                        .reduce(BigDecimal::add)
                        .orElse(BigDecimal.ZERO);
            }
        };

    }

    public Map<String, ProductPriceDetails> getProductPriceDetails() {
        Map<String, ProductPriceDetails> priceDetailsMap = new HashMap<>();
        try {
            File file = this.getFileFromClassPath("ProductPriceDetails.xlsx");
            FileInputStream fis = new FileInputStream(file);
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            Sheet priceDetailsSheet = workbook.getSheet("ProductPriceSheet");
            Iterator<Row> rowIterator = priceDetailsSheet.rowIterator();
            int rowCount = 0;
            while (rowIterator.hasNext()) {
                Row mapRow = rowIterator.next();
                if (rowCount == 0) {
                    // Ignore header row
                    rowCount++;
                    continue;
                }
                if (mapRow != null) {
                    String productName = null;
                    BigDecimal pricePerUnit = null;
                    int discountOnItem = 0;
                    if (mapRow.getCell(0) != null) {
                        productName = mapRow.getCell(0).getStringCellValue();
                    }
                    if (mapRow.getCell(1) != null) {
                        pricePerUnit = BigDecimal.valueOf(mapRow.getCell(1).getNumericCellValue());
                    }
                    if (mapRow.getCell(2) != null) {
                        discountOnItem = (int) mapRow.getCell(1).getNumericCellValue();
                    }
                    if (productName != null && pricePerUnit != null) {
                        ProductPriceDetails productPriceDetails = new ProductPriceDetails(productName, pricePerUnit, discountOnItem);
                        priceDetailsMap.put(productName, productPriceDetails);
                    } else {
                        System.out.println("Bad product details at row " + mapRow.getRowNum() + ", name or price not specified");
                        continue;
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Exception occurred while fetching the product price details: " + e.getMessage());
        }
        return priceDetailsMap;
    }

    public Map<String, BigDecimal> getDiscountOfBusinessRules() {
        Map<String, BigDecimal> ruleDiscountMap = new HashMap<>();
        try {
            File file = this.getFileFromClassPath("ProductPriceDetails.xlsx");
            FileInputStream fis = new FileInputStream(file);
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            Sheet discountSheet = workbook.getSheet("DiscountRules");
            Iterator<Row> rowIterator = discountSheet.rowIterator();
            int rowCount = 0;
            while (rowIterator.hasNext()) {
                Row mapRow = rowIterator.next();
                if (rowCount == 0) {
                    // Ignore header row
                    rowCount++;
                    continue;
                }
                if (mapRow != null) {
                    String ruleName = null;
                    BigDecimal ruleDiscount = null;
                    if (mapRow.getCell(0) != null) {
                        ruleName = mapRow.getCell(0).getStringCellValue();
                    }
                    if (mapRow.getCell(1) != null) {
                        ruleDiscount = BigDecimal.valueOf(mapRow.getCell(1).getNumericCellValue());
                    }
                    if (ruleName != null && ruleDiscount != null) {
                        ruleDiscountMap.put(ruleName, ruleDiscount);
                    } else {
                        System.out.println("Bad product details at row " + mapRow.getRowNum() + ", name or price not specified");
                        continue;
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Exception occurred while fetching the product price details: " + e.getMessage());
        }
        return ruleDiscountMap;
    }

    private File getFileFromClassPath(String fileName) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);

        if (resource == null) {
            throw new IllegalArgumentException(fileName + " file is not found! on the classpath");
        } else {
            return new File(resource.getFile());
        }
    }

    public Map<String, Integer> getCategoryWiseProductCount(ShoppingCart shoppingCart) {
        Map<String, Integer> categoryAndCount = new HashMap<>();
        if (shoppingCart.getProducts() != null) {
            shoppingCart.getProducts().stream().forEach(p -> {
                if (categoryAndCount.containsKey(p.getCategory())) {
                    Integer updatedCount = categoryAndCount.get(p.getCategory()) + p.getQuantity();
                    categoryAndCount.put(p.getCategory(), updatedCount);
                } else {
                    categoryAndCount.put(p.getCategory(), p.getQuantity());
                }
            });

        } else {
            System.out.println("Not possible to identify business rule on empty shopping cart.");
        }


        return categoryAndCount;
    }
}
