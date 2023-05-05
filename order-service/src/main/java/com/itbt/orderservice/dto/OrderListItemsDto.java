package com.itbt.orderservice.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderListItemsDto {
    private Long id;
    private String skuCode;
    private Integer quantity;
    private BigDecimal price;
}
