package com.athul.orderservice.service;

import com.athul.orderservice.config.WebClientConfig;
import com.athul.orderservice.dto.InventoryResponse;
import com.athul.orderservice.dto.OrderItemsDto;
import com.athul.orderservice.dto.OrderRequest;
import com.athul.orderservice.model.Order;
import com.athul.orderservice.model.OrderItems;
import com.athul.orderservice.repo.OrderRepo;
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

    private final OrderRepo orderRepo;
    private final WebClient.Builder webClientBuilder;

    public String placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderItems> orderItems = orderRequest.getOrderItemsDtoList().stream()
                .map(this::mapToModel)
                .toList();
        order.setOrderItemsList(orderItems);

        List<String> skuCodes = order.getOrderItemsList().stream().map(OrderItems::getSkuCode).toList();

        // Call inventory service and place the order if the product is in stock
        InventoryResponse[] inventoryResponsesArray = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory",uriBuilder -> uriBuilder.queryParam("skuCode",skuCodes).build())
                        .retrieve()
                                .bodyToMono(InventoryResponse[].class)
                                        .block();
        boolean allProductsInStock =  Arrays.stream(inventoryResponsesArray).allMatch( InventoryResponse::isInStock);

        if(allProductsInStock) {
            orderRepo.save(order);
            return "Order placed successfully";
        }
        else throw new IllegalArgumentException("Product is not in stock, please try again");
    }

    private OrderItems mapToModel(OrderItemsDto orderItemsDto) {
        OrderItems orderItems = new OrderItems();
        orderItems.setPrice(orderItemsDto.getPrice());
        orderItems.setQuantity(orderItemsDto.getQuantity());
        orderItems.setSkuCode(orderItemsDto.getSkuCode());
        return orderItems;
    }
}
