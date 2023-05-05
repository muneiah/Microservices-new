package com.itbt.orderservice.service;

import com.itbt.orderservice.dto.InventoryResponse;
import com.itbt.orderservice.dto.OrderListItemsDto;
import com.itbt.orderservice.dto.OrderRequest;
import com.itbt.orderservice.model.Order;
import com.itbt.orderservice.model.OrderLineItems;
import com.itbt.orderservice.respository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;

    private final WebClient.Builder webClientBuilder;
    public void placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItemsList = orderRequest.getOrderListItemsDtoList().stream()
                .map(this::maptoDto)
                .toList();
        order.setOrderLineItemsList(orderLineItemsList);
        //check inventory
        List<String> skuCodes = order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

        InventoryResponse[] inventoryResponses = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();
        boolean allItemsAreInStock = Arrays.stream(inventoryResponses).allMatch(inventory -> inventory.getIsInStock());
        if (allItemsAreInStock){
            orderRepository.save(order);
        } else {
            throw new IllegalArgumentException("Product is not in stock, please try again later!");
        }
    }

    private OrderLineItems maptoDto(OrderListItemsDto orderListItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setId(orderListItemsDto.getId());
        orderLineItems.setQuantity(orderListItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderListItemsDto.getSkuCode());
        orderLineItems.setPrice(orderListItemsDto.getPrice());
        return orderLineItems;
    }
}
